package vn.xuanhung.ELearning_Service.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.common.ParseHelper;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.CourseDetailViewRequest;
import vn.xuanhung.ELearning_Service.dto.request.CourseHeaderViewRequest;
import vn.xuanhung.ELearning_Service.dto.request.CourseRequest;
import vn.xuanhung.ELearning_Service.dto.request.KafkaUploadVideoDto;
import vn.xuanhung.ELearning_Service.dto.response.CourseDetailViewResponse;
import vn.xuanhung.ELearning_Service.dto.response.CourseHeaderViewResponse;
import vn.xuanhung.ELearning_Service.dto.response.CourseResponse;
import vn.xuanhung.ELearning_Service.dto.response.LessonResponse;
import vn.xuanhung.ELearning_Service.entity.Certificate;
import vn.xuanhung.ELearning_Service.entity.Course;
import vn.xuanhung.ELearning_Service.entity.Discount;
import vn.xuanhung.ELearning_Service.entity.Lesson;
import vn.xuanhung.ELearning_Service.entity.view.CourseHeaderView;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.mapper.CourseHeaderMapper;
import vn.xuanhung.ELearning_Service.repository.*;
import vn.xuanhung.ELearning_Service.repository.view.CourseHeaderViewRepository;
import vn.xuanhung.ELearning_Service.service.CourseService;
import vn.xuanhung.ELearning_Service.specification.CourseHeaderSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ICourseService implements CourseService {
    CourseRepository courseRepository;
    UserCourseRepository userCourseRepository;
    UserInfoRepository userInfoRepository;
    CategoryRepository categoryRepository;
    DiscountRepository discountRepository;
    CertificateRepository certificateRepository;
    LessonRepository lessonRepository;

    CourseHeaderViewRepository courseHeaderViewRepository;

    ModelMapper modelMapper;
    CourseHeaderMapper courseHeaderMapper;
   // EntityManager entityManager;
    JdbcTemplate jdbcTemplate;

    AmazonS3 amazonS3;
    YouTube youTube;
    KafkaTemplate<String, Object> kafkaTemplate;


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
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Specification<CourseHeaderView> spec = CourseHeaderSpecification.getSpecification(req);

        Page<CourseHeaderView> page = courseHeaderViewRepository.findAll(spec, pageable);
        List<CourseHeaderView> list = page.getContent();


        return ApiResponsePagination.<List<CourseHeaderViewResponse>>builder()
                .result(courseHeaderMapper.convertToDtoList(list))
                .pageSize(req.getPageSize())
                .totalPages(page.getTotalPages())
                .currentPage(req.getPage())
                .totalItems(page.getTotalElements())
                .build();
    }

    @Override
    public ApiResponse<CourseDetailViewResponse> getCourseDetail(CourseDetailViewRequest req) {
        log.info("***Log course service - get detail course ***");
        if(req.getCourseId() != null){
            Course course = courseRepository.findById(req.getCourseId())
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));

            List<Lesson> lessons = lessonRepository.findAllByCourseId(req.getCourseId());

            List<LessonResponse> lessonResponses = lessons.stream()
                    .map((item) -> modelMapper.map(item, LessonResponse.class)).toList();
            CourseDetailViewResponse courseDetailViewResponse =  CourseDetailViewResponse.builder()
                    .id(course.getId())
                    .courseName(course.getCourseName())
                    .description(course.getDescription())
                    .courseDuration(course.getCourseDuration())
                    .quantity(course.getQuantity())
                    .createdAt(course.getCreatedAt())
                    .avatar(course.getAvatar())
                    .trailer(course.getTrailer())
                    .level(course.getLevel())
                    .lessons(lessonResponses)
                    .build();

            Boolean check = userCourseRepository.existsByCourseIdAndUserId(req.getCourseId(), req.getUserId());
            courseDetailViewResponse.setIsRegister(check);

            return ApiResponse.<CourseDetailViewResponse>builder()
                    .result(courseDetailViewResponse)
                    .build();
        }else
            throw new AppException(ErrorCode.NOT_ENOUGH_INFO);
    }

    @Override
    public ApiResponse<List<CourseHeaderViewResponse>> getCourseHeaderSpecial() {
        log.info("***Log course service - get header course special***");
        StringBuilder sql = new StringBuilder("SELECT* FROM d_course_special_view");

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString());
        List<CourseHeaderViewResponse> data = new ArrayList<>();
        try{
           for(Map<String, Object> rs : result){
               CourseHeaderViewResponse courseHeaderViewRequest = CourseHeaderViewResponse.builder()
                       .id(ParseHelper.INT.parse(rs.get("course_id")))
                       .courseName(ParseHelper.STRING.parse(rs.get("course_name")))
                       .description(ParseHelper.STRING.parse(rs.get("description")))
                       .avatar(ParseHelper.STRING.parse(rs.get("avatar")))
                       .build();
               data.add(courseHeaderViewRequest);
           }
            return ApiResponse.<List<CourseHeaderViewResponse>>builder()
                    .result(data)
                    .build();
        }catch (Exception e){
            log.error("Error: {}", e.getMessage());
            throw new AppException(ErrorCode.SYSTEM_ERROR);
        }
    }

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
            if(!categoryRepository.existsById(req.getCategoryId())  ){
                throw new AppException(ErrorCode.NOT_ENOUGH_INFO);
            }

            if(!userInfoRepository.existsById(req.getInstructorId())){
                throw new AppException(ErrorCode.NOT_ENOUGH_INFO);
            }

            entitySave = modelMapper.map(req, Course.class);

            try {
                entitySave.setAvatar(uploadImage(req.getAvatar()));
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

        entitySave.setIsActive("Y");
        entitySave = courseRepository.saveAndFlush(entitySave);
        String fileTemp = uploadTempFile(req.getTrailer()); // upload tạm lên S3
        String playlistId = createPlaylist(entitySave.getCourseName(), entitySave.getDescription());

        log.info("Send kafka upload video: {}", fileTemp);
        kafkaTemplate.send(AppConstant.Topic.VIDEO_TOPIC,
                KafkaUploadVideoDto.builder()
                        .s3Url(fileTemp)
//                        .video(req.getTrailer())
                        .courseId(entitySave.getId())
                        .playlistId(playlistId)
                        .title(entitySave.getCourseName())
                        .description(entitySave.getDescription())
                        .build());

        if(req.getLessons() != null && !req.getLessons().isEmpty()){
            Integer id = entitySave.getId();
            req.getLessons().forEach((lesson) -> {
                try {
                    Lesson lesson1 = Lesson.builder()
                            .courseId(id)
                            .lessonName(lesson.getLessonName())
                            .lessonTime(lesson.getLessonTime())
                            .isActive("Y")
                            .description(lesson.getDescription())
                            .build();

                    lesson1 = lessonRepository.saveAndFlush(lesson1);

                    String fileTempLesson = uploadTempFile(lesson.getUrlLesson()); // upload tạm lên S3
                    log.info("Send kafka upload video: {}", fileTempLesson);
                    kafkaTemplate.send(AppConstant.Topic.VIDEO_TOPIC,
                            KafkaUploadVideoDto.builder()
                                    .s3Url(fileTempLesson)
//                                    .video(lesson.getUrlLesson())
                                    .lessonId(lesson1.getId())
                                    .playlistId(playlistId)
                                    .title(lesson1.getLessonName())
                                    .description(lesson1.getDescription())
                                    .build());
                }catch (Exception e){
                    log.error("Error: {}", e.getMessage());
                    throw new AppException(ErrorCode.SYSTEM_ERROR);
                }
            });
        }

        return ApiResponse.<CourseResponse>builder()
                .result(modelMapper.map(entitySave, CourseResponse.class))
                .build();
    }

    public String uploadTempFile(MultipartFile file) {
        String key = "temp/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(new PutObjectRequest(AWS_BUCKET, key, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.Private)); // Private vì tạm

            return key; // Trả về
        } catch (IOException e) {
            throw new AppException(ErrorCode.UPLOAD_S3_FAIL);
        }
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

    private String createPlaylist(String title, String description) {
        try {
            PlaylistSnippet snippet = new PlaylistSnippet();
            snippet.setTitle(title);
            snippet.setDescription(description);

            PlaylistStatus status = new PlaylistStatus();
            status.setPrivacyStatus("private"); // Hoặc private / unlisted tuỳ bạn

            Playlist playlist = new Playlist();
            playlist.setSnippet(snippet);
            playlist.setStatus(status);

            Playlist response = youTube.playlists()
                    .insert("snippet,status", playlist)
                    .execute();

            return response.getId(); // Trả về Playlist Id
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
