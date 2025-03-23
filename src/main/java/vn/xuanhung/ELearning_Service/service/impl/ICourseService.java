package vn.xuanhung.ELearning_Service.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import jakarta.transaction.Transactional;
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
import vn.xuanhung.ELearning_Service.dto.request.CourseHeaderViewRequest;
import vn.xuanhung.ELearning_Service.dto.request.CourseRequest;
import vn.xuanhung.ELearning_Service.dto.response.CourseHeaderViewResponse;
import vn.xuanhung.ELearning_Service.dto.response.CourseResponse;
import vn.xuanhung.ELearning_Service.entity.Certificate;
import vn.xuanhung.ELearning_Service.entity.Course;
import vn.xuanhung.ELearning_Service.entity.Discount;
import vn.xuanhung.ELearning_Service.entity.Lesson;
import vn.xuanhung.ELearning_Service.entity.view.CourseHeaderView;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.mapper.CourseHeaderMapper;
import vn.xuanhung.ELearning_Service.repository.*;
import vn.xuanhung.ELearning_Service.service.CourseService;
import vn.xuanhung.ELearning_Service.specification.CourseHeaderSpecification;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ICourseService implements CourseService {
    CourseRepository courseRepository;
    UserInfoRepository userInfoRepository;
    CategoryRepository categoryRepository;
    DiscountRepository discountRepository;
    CertificateRepository certificateRepository;
    LessonRepository lessonRepository;

    CourseHeaderViewRepository courseHeaderViewRepository;

    ModelMapper modelMapper;
    CourseHeaderMapper courseHeaderMapper;

    AmazonS3 amazonS3;
    YouTube youtube;

    @NonFinal
    @Value("${aws.bucket}")
    String AWS_BUCKET;

    @NonFinal
    @Value("${aws.folder}")
    String AWS_FOLDER;

    @Override
    public ApiResponsePagination<List<CourseResponse>> findAll(CourseRequest request) {
        return null;
    }

    @Override
    public ApiResponse<CourseResponse> findById(Integer integer) {
        return null;
    }

    @Override
    public ApiResponsePagination<List<CourseHeaderViewResponse>> getCourseHeader(CourseHeaderViewRequest req) {
        log.info("***Log course service - get header course ***");
        Pageable pageable = PageRequest.of(
                req.getPage(),
                req.getPageSize(),
                Sort.by(Sort.Direction.ASC, "createdAt")
        );
        Specification<CourseHeaderView> spec = CourseHeaderSpecification.getSpecification(req);

        Page<CourseHeaderView> page = courseHeaderViewRepository.findAll(spec, pageable);
        List<CourseHeaderView> list = page.getContent();


        return ApiResponsePagination.<List<CourseHeaderViewResponse>>builder()
                .result(courseHeaderMapper.convertToDtoList(list))
                .build();
    }

    // Mỗi hàm chỉ xử lý 1 chức năng
    @Override
    @Transactional
    public ApiResponse<CourseResponse> save(CourseRequest req) {
        log.info("***Log course service - get save course ***");
        Course entitySave = null;
        if(req.getId() != null){
            entitySave = courseRepository.findById(req.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));

            modelMapper.map(req, entitySave);
            entitySave.setPriceAfterReduce(setPriceAfterDiscount(req, entitySave));
        }else{
            if(!categoryRepository.existsById(req.getCategoryId()) || !userInfoRepository.existsById(req.getInstructorId())){
                throw new AppException(ErrorCode.NOT_ENOUGH_INFO);
            }

            entitySave = modelMapper.map(req, Course.class);


            try {
                entitySave.setAvatar(uploadImage(req.getAvatar()));
                entitySave.setTrailer(uploadVideo(req.getTrailer(), req.getCourseName(), req.getDescription()));
            }catch(Exception e){
                e.printStackTrace();
            }
            entitySave.setPriceAfterReduce(setPriceAfterDiscount(req, entitySave));

            Certificate certificate = Certificate.builder()
                    .certificateName(req.getCertificateName())
                    .description(req.getCertificateDescription())
                    .certificateLevel(req.getLevel())
                    .validityPeriod(BigDecimal.valueOf(3))//3 month
                    .build();
            certificate = certificateRepository.save(certificate);

            entitySave.setCertificateId(certificate.getId());
        }

        entitySave = courseRepository.save(entitySave);
        if(req.getLessons() != null && !req.getLessons().isEmpty()){
            Integer id = entitySave.getId();
            req.getLessons().forEach((lesson) -> {
                try {
                    Lesson lesson1 = Lesson.builder()
                            .courseId(id)
                            .lessonName(lesson.getLessonName())
                            .lessonTime(lesson.getLessonTime())
                            .urlLesson(uploadVideo(lesson.getUrlLesson(), lesson.getLessonName(), lesson.getDescription()))
                            .isActive("Y")
                            .description(lesson.getDescription())
                            .build();

                    lessonRepository.save(lesson1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }

        return ApiResponse.<CourseResponse>builder()
                .result(modelMapper.map(entitySave, CourseResponse.class))
                .build();
    }

    @Override
    public ApiResponse<String> deleteById(Integer id) {
        log.info("***Log course service - delete course ***");
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));

        course.setIsActive("N");
        courseRepository.save(course);
        return ApiResponse.<String>builder()
                .result("Delete successfully!")
                .build();
    }

    private BigDecimal setPriceAfterDiscount(CourseRequest req, Course entitySave){
        if(req.getDiscountCode() != null){
            Discount discount = discountRepository.findByDiscountCode(req.getDiscountCode());
            if(discount != null){
                BigDecimal priceAfterReduce = entitySave.getPriceEntered()
                        .multiply(BigDecimal.ONE.subtract(discount.getDiscountRate().divide(BigDecimal.valueOf(100))));

                return priceAfterReduce;
            }
        }
        return entitySave.getPriceEntered();
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

    private String uploadVideo(MultipartFile filePath, String title, String description) throws Exception {
        // Tạo Metadata cho video
        Video video = new Video();

        VideoStatus status = new VideoStatus();
        status.setPrivacyStatus("public"); // Mặc định là public
        video.setStatus(status);

        VideoSnippet snippet = new VideoSnippet();
        snippet.setTitle(title);
        snippet.setDescription(description);
        video.setSnippet(snippet);

        // Lưu file tạm từ MultipartFile
        File tempFile = File.createTempFile("upload", filePath.getOriginalFilename());
        filePath.transferTo(tempFile);

        // Chuẩn bị nội dung file để upload
        InputStreamContent mediaContent = new InputStreamContent("video/*", new FileInputStream(tempFile));

        try {
            // Thực hiện upload video
            YouTube.Videos.Insert request = youtube.videos()
                    .insert("snippet,status", video, mediaContent);
            Video response = request.execute();

            // Trả về URL video đã upload
            System.out.println("Video uploaded successfully. Video ID: " + response.getId());
            return "https://www.youtube.com/embed/" + response.getId();
        } catch (Exception e) {
            throw new RuntimeException("Error uploading video to YouTube", e);
        } finally {
            // Xóa file tạm
            tempFile.delete();
        }
    }


}
