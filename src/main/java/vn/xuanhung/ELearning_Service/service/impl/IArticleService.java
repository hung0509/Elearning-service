package vn.xuanhung.ELearning_Service.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
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
import java.net.URL;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IArticleService implements ArticleService {
    ArticleRepository articleRepository;
    ArticleUserViewRepository articleUserViewRepository;
    UserInfoRepository articleUserRepository;
    ModelMapper modelMapper;

    AmazonS3 amazonS3;

    @NonFinal
    @Value("${aws.bucket}")
    String AWS_BUCKET;

    @NonFinal
    @Value("${aws.folder}")
    String AWS_FOLDER;

    @Override
    public ApiResponsePagination<List<ArticleResponse>> findAll(ArticleRequest request) {
        log.info("***Log article service - get all article by pagination***");
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
    public ApiResponse<ArticleResponse> save(ArticleRequest req) {
        log.info("***Log article service - save article***");
        if(req.getInstructorId() != null){
            Boolean check = articleUserRepository.existsById(req.getInstructorId());
            if(check){
                Article article = modelMapper.map(req, Article.class);

                try {
                    article.setImage(uploadImage(req.getImage()));
                }catch (Exception e){
                    log.error("Error: {}", e.getMessage());
                    throw new AppException(ErrorCode.ERROR_SQL);
                }

                article.setContent(req.getContent().replaceAll("<img(.*?)>", "<img$1 />"));
                article.setStatus(AppConstant.STATUS_PENDING);
                article.setPublishedDate(new Date());

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
        PutObjectRequest request = new PutObjectRequest(AWS_BUCKET, keyName, inputStream, metadata);
        amazonS3.putObject(request);//Đẩy hình ảnh lên trên bucket

        URL url = amazonS3.getUrl(AWS_BUCKET, keyName);
        //Ở đây đang để ở public access
        //nếu block access đi ta cần cấu hình IAM role...(Tìm hiểu thêm)
        return url.toString();
    }


}
