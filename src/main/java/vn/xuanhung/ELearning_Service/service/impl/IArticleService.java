package vn.xuanhung.ELearning_Service.service.impl;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.common.Base64DecodedMultipartFile;
import vn.xuanhung.ELearning_Service.common.RedisGenericCacheService;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.controller.CategoryController;
import vn.xuanhung.ELearning_Service.dto.request.ArticleRequest;
import vn.xuanhung.ELearning_Service.dto.request.ArticleUserViewRequest;
import vn.xuanhung.ELearning_Service.dto.response.ArticleResponse;
import vn.xuanhung.ELearning_Service.dto.response.ArticleUserViewResponse;
import vn.xuanhung.ELearning_Service.entity.Article;
import vn.xuanhung.ELearning_Service.entity.view.ArticleUserView;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.repository.ArticleRepository;
import vn.xuanhung.ELearning_Service.repository.UserInfoRepository;
import vn.xuanhung.ELearning_Service.repository.view.ArticleUserViewRepository;
import vn.xuanhung.ELearning_Service.service.ArticleService;
import vn.xuanhung.ELearning_Service.specification.ArticleSpecification;
import vn.xuanhung.ELearning_Service.specification.ArticleUserViewSpecification;


import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IArticleService implements ArticleService {
    ArticleRepository articleRepository;
    ArticleUserViewRepository articleUserViewRepository;
    UserInfoRepository articleUserRepository;
    ModelMapper modelMapper;
    RedisGenericCacheService<ArticleUserView> redisGenericCacheService;
    RedisGenericCacheService<Article> redisGenericCacheService1;

    S3Client s3Client;
    private final CategoryController categoryController;

    @NonFinal
    @Value("${aws.bucket}")
    String AWS_BUCKET;

    @NonFinal
    @Value("${aws.folder}")
    String AWS_FOLDER;

    @Override
    public ApiResponsePagination<List<ArticleResponse>> findAll(ArticleRequest request) {
        log.info("***Log article service - get all article by pagination***");
//        if(request.getId() != null){
//            redisGenericCacheService1.setClazz(Article.class);
//            redisGenericCacheService1.setPrefix("article");
//            redisGenericCacheService1.setDbLoaderById(id -> articleRepository.findById(request.getId()).orElse(null));
//            Optional<Article> article = redisGenericCacheService1.getById(request.getId(), Duration.ofMinutes(5));
//            log.info("data");
//            List<ArticleResponse> data = new ArrayList<>();
//            Article
//            modelMapper.map(article.get() , ArticleResponse.class);
//            data.add(article);
//
//            return ApiResponsePagination.<List<ArticleResponse>>builder()
//                    .result(data)
//                    .build();
//        }

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Specification<Article> specification = ArticleSpecification.getSpecification(request);

        Page<Article> discounts = articleRepository.findAll(specification, pageable);

        List<Article> data = discounts.getContent();
        return ApiResponsePagination.<List<ArticleResponse>>builder()
                .result(data.stream().map(item -> modelMapper.map(item , ArticleResponse.class)).toList())
                .totalPages(discounts.getTotalPages())
                .currentPage(request.getPage())
                .pageSize(request.getPageSize())
                .totalItems(discounts.getTotalElements())
                .build();
    }

    @Override
    public ApiResponsePagination<List<ArticleUserViewResponse>> getArticleUserView(ArticleUserViewRequest req) {
        log.info("***Log article service - get all article by pagination***");
        if(req.getId() != null){
            redisGenericCacheService.setClazz(ArticleUserView.class);
            redisGenericCacheService.setPrefix("article:user");
            redisGenericCacheService.setDbLoaderById(id -> articleUserViewRepository.findById(req.getId()).orElse(null));
            ArticleUserView article = redisGenericCacheService.getById(req.getId(), Duration.ofMinutes(5)).get();
            List<ArticleUserViewResponse> data = new ArrayList<>();
            data.add(modelMapper.map(article , ArticleUserViewResponse.class));

            return ApiResponsePagination.<List<ArticleUserViewResponse>>builder()
                    .result(data)
                    .build();
        }
        Pageable pageable = PageRequest.of(req.getPage(), req.getPageSize(),
                Sort.by(Sort.Direction.DESC, "updatedAt"));

        Specification<ArticleUserView> spec = ArticleUserViewSpecification.getSpecification(req);

        Page<ArticleUserView> page = articleUserViewRepository.findAll(spec, pageable);

        List<ArticleUserView> data = page.getContent();
        return ApiResponsePagination.<List<ArticleUserViewResponse>>builder()
                .result(data.stream().map(item -> modelMapper.map(item , ArticleUserViewResponse.class)).toList())
                .totalPages(page.getTotalPages())
                .currentPage(req.getPage())
                .pageSize(req.getPageSize())
                .totalItems(page.getTotalElements())
                .build();
    }

    @Override
    public ApiResponse<ArticleResponse> updateArticle(ArticleRequest req) {
        log.info("***Log article service - update article***");
        Article article = articleRepository.findById(req.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ARTICLE_NOT_EXIST));

        modelMapper.map(req, article);

        article = articleRepository.save(article);
        return ApiResponse.<ArticleResponse>builder()
                .result(modelMapper.map(article , ArticleResponse.class))
                .build();
    }

    @Override
    public ApiResponse<ArticleResponse> save(ArticleRequest req) {
        log.info("***Log article service - save article***");
        if(req.getInstructorId() != null){
            boolean check = articleUserRepository.existsById(req.getInstructorId());
            if(check){
                Article article = modelMapper.map(req, Article.class);

                try {
                    article.setImage(uploadImage(req.getImage()));
                }catch (Exception e){
                    log.error("Error: {}", e.getMessage());
                    throw new AppException(ErrorCode.ERROR_SQL);
                }

                article.setStatus(AppConstant.STATUS_PENDING);
                article.setPublishedDate(new Date());
                article.setContent(handleContentAndUploadImage(req.getContent()));

                article = articleRepository.save(article);

                return ApiResponse.<ArticleResponse>builder()
                        .result(modelMapper.map(article, ArticleResponse.class))
                        .build();
            }else
                throw new AppException(ErrorCode.ARTICLE_NOT_EXIST);
        }
        throw new AppException(ErrorCode.NOT_ENOUGH_INFO);
    }

    @Override
    public ApiResponse<String> deleteById(Integer integer) {
        return null;
    }

    @Override
    public ApiResponse<ArticleResponse> findById(Integer integer) {
        return null;
    }

//    private String uploadImage(MultipartFile file) throws IOException {
//        String contentType = file.getContentType();
//        InputStream inputStream = file.getInputStream();
//
//        //Kiểm tra nếu không phải là ảnh thì không cho phép tiếp tục
//        if (!contentType.equals("image/jpeg")
//                && !contentType.equals("image/png")
//                && !contentType.equals("image/webp")
//                && !contentType.equals("image/gif")
//                && !contentType.equals("image/bmp")) {
//            throw new AppException(ErrorCode.NOT_VALID_FORMAT_IMAGE);
//        }
//
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentType(contentType); // Hoặc loại nội dung phù hợp khác
//
//        String keyName = AWS_FOLDER + "/" + file.getOriginalFilename();
//        PutObjectRequest request = new PutObjectRequest(AWS_BUCKET, keyName, inputStream, metadata);
//        amazonS3.putObject(request);//Đẩy hình ảnh lên trên bucket
//
//        URL url = amazonS3.getUrl(AWS_BUCKET, keyName);
//        //Ở đây đang để ở public access
//        //nếu block access đi ta cần cấu hình IAM role...(Tìm hiểu thêm)
//        return url.toString();
//    }

    private String uploadImage(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        InputStream inputStream = file.getInputStream();

        //Kiểm tra nếu không phải là ảnh thì không cho phép tiếp tục
        if (!contentType.equals("image/jpeg")
                && !contentType.equals("image/png")
                && !contentType.equals("image/webp")
                && !contentType.equals("image/gif")
                && !contentType.equals("image/bmp")) {
            throw new AppException(ErrorCode.NOT_VALID_FORMAT_IMAGE);
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType); // Hoặc loại nội dung phù hợp khác

        String keyName = AWS_FOLDER + "/" + file.getOriginalFilename();

        software.amazon.awssdk.services.s3.model.PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(AWS_BUCKET)
                .key(keyName)
                .contentType(contentType)
                .build();

        PutObjectResponse future = s3Client.putObject(
                putObjectRequest,
                RequestBody.fromInputStream(inputStream, file.getSize())
        );   //Đẩy hình ảnh lên trên bucket

//        URL url = s3AsyncClient.getUrl(AWS_BUCKET, keyName);
//        //Ở đây đang để ở public access
//        //nếu block access đi ta cần cấu hình IAM role...(Tìm hiểu thêm)
//        return url.toString();

        return String.format("https://%s.s3.amazonaws.com/%s", AWS_BUCKET, keyName);
    }

    private MultipartFile base64ToMultipartFile(String base64) throws IOException {
        String[] parts = base64.split(",");
        String metaInfo = parts[0]; // data:image/png;base64
        String base64Data = parts[1];

        if(metaInfo.equals("data:image/png;base64")) {
            String contentType = metaInfo.split(":")[1].split(";")[0];
            byte[] data = Base64.getDecoder().decode(base64Data);

            return new Base64DecodedMultipartFile(data, contentType, "image.png");
        }
        return null;
    }

    private String handleContentAndUploadImage(String content)  {
        Document document = Jsoup.parse(content); // Parse HTML content
        Elements imgElements = document.select("img"); // Lấy tất cả thẻ img

        for (Element img : imgElements) {
            String src = img.attr("src"); // Lấy src trong img
            if (src.startsWith("data:image")) {
                try {
                    MultipartFile file = base64ToMultipartFile(src);

                    if (file != null) {
                        String urlUploaded = uploadImage(file); // upload cloud return url
                        img.attr("src", urlUploaded); // replace src mới
                    }
                }catch (Exception e){
                    log.error("Error: {}", e.getMessage());
                    throw new AppException(ErrorCode.SYSTEM_ERROR);
                }
            }
        }

        return document.body().html();
    }


}
