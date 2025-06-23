package vn.xuanhung.ELearning_Service.service.impl;

import com.amazonaws.services.s3.model.ObjectMetadata;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import vn.xuanhung.ELearning_Service.common.*;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.*;
import vn.xuanhung.ELearning_Service.dto.response.UserInfoResponse;
import vn.xuanhung.ELearning_Service.entity.*;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.helper.UserInfoHelper;
import vn.xuanhung.ELearning_Service.jwt.UserDetailCustom;
import vn.xuanhung.ELearning_Service.repository.*;
import vn.xuanhung.ELearning_Service.repository.view.ArticleUserViewRepository;
import vn.xuanhung.ELearning_Service.repository.view.CourseHeaderViewRepository;
import vn.xuanhung.ELearning_Service.repository.view.CourseRegisterViewRepository;
import vn.xuanhung.ELearning_Service.service.UserInfoService;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IUserInfoService implements UserInfoService {
    UserInfoRepository userInfoRepository;
    CourseRepository courseRepository;
    UserCourseRepository userCourseRepository;
    CourseRegisterViewRepository courseRegisterViewRepository;
    UserLessonRepository userLessonRepository;
    LessonRepository lessonRepository;
    ArticleUserViewRepository articleUserViewRepository;
    CourseHeaderViewRepository courseHeaderViewRepository;
    UserInfoHelper userInfoHelper;
    UserCertificateRepository userCertificateRepository;

    KafkaTemplate<String, Object> kafkaTemplate;
    JdbcTemplate jdbcTemplate;
    ModelMapper modelMapper;
    RedisCacheFactory redisCacheFactory;
    S3Client s3Client;

    @NonFinal
    @Value("${aws.bucket}")
    String AWS_BUCKET;

    @NonFinal
    @Value("${aws.folder}")
    String AWS_FOLDER;


    @Override
    public ApiResponse<UserInfoResponse> getMyInfo() {
        log.info("***Log user-info service - get my info account***");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailCustom userDetails) {
            Integer id =  userDetails.getUserId(); // Retrieve the userId

            RedisGenericCacheService<UserInfoResponse> redisGenericCacheService = redisCacheFactory
                    .create(AppConstant.PREFIX.USER_INFO , UserInfoResponse.class);

            UserInfoResponse userInfoResponse = redisGenericCacheService.getByPrefixById(id);
            if(userInfoResponse != null){
                return ApiResponse.<UserInfoResponse>builder()
                        .result(userInfoResponse)
                        .build();
            }

            userInfoResponse  = userInfoHelper.buildUserInfoResponse(id);

            redisGenericCacheService.saveItem(id, userInfoResponse, Duration.ofDays(1)); // Cache one day

            return ApiResponse.<UserInfoResponse>builder()
                    .result(userInfoResponse)
                    .build();
        }
        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    @Override
    public ApiResponse<String> registerCourse(UserCourseRequest req) {
        log.info("***Log user-info service - register course***");

        if(!courseRepository.existsById(req.getCourseId())) throw new AppException(ErrorCode.COURSE_NOT_EXIST);

        UserCourse userCourse = UserCourse.builder()
                .userId(req.getUserId())
                .courseId(req.getCourseId())
                .status(AppConstant.REGISTER)
                .enrollmentDate(new Date())
                .progression(BigDecimal.valueOf(0))
                .build();
        log.info("userCourse: {}", userCourse);

        log.info("Update cache user-info");
        kafkaTemplate.send(AppConstant.Topic.USER_CACHE_UPDATE_EVENT, UserInfoCacheUpdateEvent.builder()
                        .userId(req.getUserId())
                        .action(AppConstant.ACTION.REBUILD)
                .build());

        //Câp nhật lại trạng thái khóa học! xóa c
        kafkaTemplate.send(AppConstant.Topic.COURSE_UPDATE_EVENT, CourseCacheUpdateEvent.builder()
                .courseId(req.getCourseId())
                .action(AppConstant.ACTION.INVALIDATE)
                .build());

        //Xử lý tiền paypal
        userCourseRepository.save(userCourse);
        return ApiResponse.<String>builder()
                .result("Register course successfully!")
                .build();

    }

    @Override
    @Transactional
    public ApiResponse<String> learnLesson(UserLessonRequest req) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailCustom userDetails) {
            Integer userId = userDetails.getUserId();

            Lesson lesson = lessonRepository.findById(req.getLessonId())
                    .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_EXIST));

            Course course = courseRepository.findById(lesson.getCourseId())
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));

            UserLesson userLesson = userLessonRepository.findByLessonIdAndUserId(req.getLessonId(), userId);

            if(userLesson == null) {
                userLesson = UserLesson.builder()
                        .userId(userId)
                        .lessonId(req.getLessonId())
                        .courseId(course.getId())
                        .status(AppConstant.COMPLETE)
                        .build();
                userLessonRepository.save(userLesson);

                log.info("Send Kafka with topic: {}", AppConstant.Topic.COURSE_UPDATE_EVENT);
                kafkaTemplate.send(AppConstant.Topic.COURSE_UPDATE_EVENT, CourseCacheUpdateEvent.builder()
                        .courseId(lesson.getCourseId())
                        .action(AppConstant.ACTION.INVALIDATE)
                        .build());
            }

            UserCourse userCourse = userCourseRepository.findByCourseIdAndUserId(lesson.getCourseId(), userId);

            if(userCourse != null) {
                Integer totalLessons = lessonRepository.countAllByCourseId(course.getId());

                Integer completedLessons = userLessonRepository.countByCourseIdAndUserId(course.getId(), userId);

                BigDecimal progression = BigDecimal.valueOf(completedLessons)
                        .divide(BigDecimal.valueOf(totalLessons), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));

                userCourse.setProgression(progression);

                if(BigDecimal.valueOf(100).compareTo(progression) == 0 ){
                    userCourse.setStatus(AppConstant.COMPLETE);

                    log.info("Created certificate for user");
                    UserCertificate certificate = UserCertificate.builder()
                            .certificateId(course.getCertificateId())
                            .userId(userId)
                            .obtainedDate(new Date())
                            .status(AppConstant.COMPLETE)
                            .build();
                    userCertificateRepository.save(certificate);

                    log.info("Send Kafka with topic: {}", AppConstant.Topic.COURSE_UPDATE_EVENT);
                    kafkaTemplate.send(AppConstant.Topic.COURSE_UPDATE_EVENT, CourseCacheUpdateEvent.builder()
                            .courseId(lesson.getCourseId())
                            .action(AppConstant.ACTION.INVALIDATE)
                            .build());
                }

                userCourseRepository.save(userCourse);
            }else{
                throw new AppException(ErrorCode.USER_NOT_REGISTER);
            }

            return ApiResponse.<String>builder()
                    .build();
        }

        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    @Override
    public ApiResponsePagination<List<UserInfoResponse>> getAll(BaseRequest req) {
        log.info("***Log user-info service - get all user***");
        Pageable pageable = PageRequest.of(req.getPage(), req.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<UserInfo> page = userInfoRepository.findAll(pageable);

        return ApiResponsePagination.<List<UserInfoResponse>>builder()
                .currentPage(req.getPage())
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageSize(req.getPageSize())
                .result(page.getContent().stream().map(item -> modelMapper.map(item, UserInfoResponse.class)).toList())
                .build();
    }

    @Override
    public ApiResponse<List<UserInfoResponse>> getUserSpecial() {
        log.info("***Log course service - get user special***");
        StringBuilder sql = new StringBuilder("select user_info_id, first_name, last_name, avatar from d_user_special_view ");

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString());
        List<UserInfoResponse> data = new ArrayList<>();
        try{
            for(Map<String, Object> rs : result){
                UserInfoResponse user = UserInfoResponse.builder()
                        .id(ParseHelper.INT.parse(rs.get("user_info_id")))
                        .firstName(ParseHelper.STRING.parse(rs.get("first_name")))
                        .lastName(ParseHelper.STRING.parse(rs.get("last_name")))
                        .avatar(ParseHelper.STRING.parse(rs.get("avatar")))
                        .build();
                data.add(user);
            }
            return ApiResponse.<List<UserInfoResponse>>builder()
                    .result(data)
                    .build();
        }catch (Exception e){
            log.error("Error: {}", e.getMessage());
            throw new AppException(ErrorCode.SYSTEM_ERROR);
        }
    }

    @Override
    public ApiResponse<UserInfoResponse> update(UserInfoRequest req) {
        if(req.getId() != null){
            UserInfo userInfo = userInfoRepository.findById(req.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
            modelMapper.map(req, userInfo);

            userInfo = userInfoRepository.save(userInfo);

            log.info("Update cache user-info");
            kafkaTemplate.send(AppConstant.Topic.USER_CACHE_UPDATE_EVENT, UserInfoCacheUpdateEvent.builder()
                    .userId(req.getId())
                    .action(AppConstant.ACTION.REBUILD)
                    .build());

            return ApiResponse.<UserInfoResponse>builder()
                    .result(modelMapper.map(userInfo, UserInfoResponse.class))
                    .build();
        }else{
            return null;
        }
    }


    @Override
    public ApiResponse<UserInfoResponse> update2(UserInfoUpdateRequest req) {
        if(req.getId() != null){
            UserInfo userInfo = userInfoRepository.findById(req.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
            modelMapper.map(req, userInfo);

            try {
                userInfo.setAvatar(uploadImage(req.getAvatar()));
            }catch(Exception e) {
                e.printStackTrace();
            }
            userInfo = userInfoRepository.save(userInfo);

            //Update Cache
            log.info("Update cache user-info");
            kafkaTemplate.send(AppConstant.Topic.USER_CACHE_UPDATE_EVENT, UserInfoCacheUpdateEvent.builder()
                    .userId(req.getId())
                    .action(AppConstant.ACTION.REBUILD)
                    .build());

            return ApiResponse.<UserInfoResponse>builder()
                    .result(modelMapper.map(userInfo, UserInfoResponse.class))
                    .build();
        }else{
            return null;
        }
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

        String originalFilename = file.getOriginalFilename();
        String safeFilename = sanitizeFilename(originalFilename);  // Xử lý tên an toàn

        if (safeFilename.length() > 100) {
            safeFilename = safeFilename.substring(safeFilename.length() - 100);
        }

        String keyName = AWS_FOLDER + "/" + safeFilename;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
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

    private String sanitizeFilename(String filename) {
        if (filename == null) return "unknown";

        String normalized = Normalizer.normalize(filename, Normalizer.Form.NFD);
        String ascii = normalized.replaceAll("[^\\p{ASCII}]", "");
        ascii = ascii.replaceAll("\\s+", "-");
        ascii = ascii.replaceAll("[^a-zA-Z0-9._-]", "");
        ascii = ascii.replaceAll("[-_]{2,}", "-"); // Gộp gạch
        return ascii;
    }


}
