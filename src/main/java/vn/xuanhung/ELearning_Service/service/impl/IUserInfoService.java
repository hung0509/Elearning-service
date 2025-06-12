package vn.xuanhung.ELearning_Service.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.common.*;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.*;
import vn.xuanhung.ELearning_Service.dto.response.ArticleUserViewResponse;
import vn.xuanhung.ELearning_Service.dto.response.CertificateResponse;
import vn.xuanhung.ELearning_Service.dto.response.CourseHeaderViewResponse;
import vn.xuanhung.ELearning_Service.dto.response.UserInfoResponse;
import vn.xuanhung.ELearning_Service.entity.*;
import vn.xuanhung.ELearning_Service.entity.view.ArticleUserView;
import vn.xuanhung.ELearning_Service.entity.view.CourseHeaderView;
import vn.xuanhung.ELearning_Service.entity.view.CourseRegisterView;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.helper.UserInfoHelper;
import vn.xuanhung.ELearning_Service.jwt.UserDetailCustom;
import vn.xuanhung.ELearning_Service.repository.*;
import vn.xuanhung.ELearning_Service.repository.view.ArticleUserViewRepository;
import vn.xuanhung.ELearning_Service.repository.view.CourseHeaderViewRepository;
import vn.xuanhung.ELearning_Service.repository.view.CourseRegisterViewRepository;
import vn.xuanhung.ELearning_Service.service.UserInfoService;
import vn.xuanhung.ELearning_Service.specification.ArticleUserViewSpecification;
import vn.xuanhung.ELearning_Service.specification.CourseHeaderSpecification;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public ApiResponse<UserInfoResponse> update2(UserInfoRequest req) {
        if(req.getId() != null){
            UserInfo userInfo = userInfoRepository.findById(req.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
            //List<AuditLog> auditLogs = ModelMapperUtil.mapWithLog(req, userInfo, modelMapper);
            modelMapper.map(req, userInfo);

            userInfo = userInfoRepository.save(userInfo);

            //Update Cache
            log.info("Update cache user-info");
            kafkaTemplate.send(AppConstant.Topic.USER_CACHE_UPDATE_EVENT, UserInfoCacheUpdateEvent.builder()
                    .userId(req.getId())
                    .action(AppConstant.ACTION.REBUILD)
                    .build());

//            if(auditLogs != null) {
//                AuditLogRequest auditLogRequest = AuditLogRequest.builder()
//                        .auditLogs(auditLogs)
//                        .build();
//                log.info("Dto log: {}", auditLogs);
//                kafkaTemplate.send(AppConstant.Topic.WRITE_LOG, auditLogRequest);
//            }
            return ApiResponse.<UserInfoResponse>builder()
                    .result(modelMapper.map(userInfo, UserInfoResponse.class))
                    .build();
        }else{
            return null;
        }
    }

}
