package vn.xuanhung.ELearning_Service.service.impl;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.common.BaseRequest;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.ArticleUserViewRequest;
import vn.xuanhung.ELearning_Service.dto.request.CourseHeaderViewRequest;
import vn.xuanhung.ELearning_Service.dto.request.UserCourseRequest;
import vn.xuanhung.ELearning_Service.dto.request.UserLessonRequest;
import vn.xuanhung.ELearning_Service.dto.response.ArticleUserViewResponse;
import vn.xuanhung.ELearning_Service.dto.response.CourseHeaderViewResponse;
import vn.xuanhung.ELearning_Service.dto.response.UserInfoResponse;
import vn.xuanhung.ELearning_Service.entity.*;
import vn.xuanhung.ELearning_Service.entity.view.ArticleUserView;
import vn.xuanhung.ELearning_Service.entity.view.CourseHeaderView;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.jwt.UserDetailCustom;
import vn.xuanhung.ELearning_Service.repository.*;
import vn.xuanhung.ELearning_Service.repository.view.ArticleUserViewRepository;
import vn.xuanhung.ELearning_Service.repository.view.CourseHeaderViewRepository;
import vn.xuanhung.ELearning_Service.service.UserInfoService;
import vn.xuanhung.ELearning_Service.specification.ArticleUserViewSpecification;
import vn.xuanhung.ELearning_Service.specification.CourseHeaderSpecification;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IUserInfoService implements UserInfoService {
    UserInfoRepository userInfoRepository;
    CourseRepository courseRepository;
    UserCourseRepository userCourseRepository;
    UserLessonRepository userLessonRepository;
    LessonRepository lessonRepository;
    ArticleUserViewRepository articleUserViewRepository;
    CourseHeaderViewRepository courseHeaderViewRepository;

    ModelMapper modelMapper;


    @Override
    public ApiResponse<UserInfoResponse> getMyInfo() {
        log.info("***Log user-info service - get my info account***");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailCustom userDetails) {
            Integer id =  userDetails.getUserId(); // Retrieve the userId
            UserInfo userInfo = userInfoRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
            UserInfoResponse userInfoResponse =  modelMapper.map(userInfo, UserInfoResponse.class);

            //Get article have created by UserId
            Pageable pageable = PageRequest.of(0, 100,
                    Sort.by(Sort.Direction.DESC, "createdAt"));
            ArticleUserViewRequest req = ArticleUserViewRequest.builder()
                    .instructorId(id)
                    .build();
            Specification<ArticleUserView> spec = ArticleUserViewSpecification.getSpecification(req);
            Page<ArticleUserView> page = articleUserViewRepository.findAll(spec, pageable);

            userInfoResponse.setArticles(page.getContent().stream()
                    .map(item -> modelMapper.map(item, ArticleUserViewResponse.class)).toList());

            //Get courses have registered by UserId
            CourseHeaderViewRequest req1 = CourseHeaderViewRequest.builder()
                    .userId(id)
                    .build();
            Specification<CourseHeaderView> spec1 = CourseHeaderSpecification.getSpecification(req1);

            Page<CourseHeaderView> page1 = courseHeaderViewRepository.findAll(spec1, pageable);
            userInfoResponse.setCourses(page1.getContent().stream()
                    .map(item -> modelMapper.map(item, CourseHeaderViewResponse.class)).toList());

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

        //Xử lý tiền paypal
        userCourseRepository.save(userCourse);
        return ApiResponse.<String>builder()
                .result("Register course successfully!")
                .build();

    }

    @Override
    public ApiResponse<String> learnLesson(UserLessonRequest req) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailCustom userDetails) {
            Integer userId = userDetails.getUserId();

            Lesson lesson = lessonRepository.findById(req.getLessonId())
                    .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_EXIST));

            UserLesson userLesson = UserLesson.builder()
                    .userId(userId)
                    .lessonId(req.getLessonId())
                    .status(AppConstant.COMPLETE)
                    .build();
            userLessonRepository.save(userLesson);

            Course course = courseRepository.findById(lesson.getCourseId())
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));

            UserCourse userCourse = userCourseRepository.findByCourseIdAndUserId(lesson.getCourseId(), userId);

            Integer countLessonLearning = lessonRepository.countAllByCourseId(lesson.getCourseId()).intValue();

            userCourse.setProgression(
                    BigDecimal.valueOf(countLessonLearning)
                    .divide(course.getQuantity(), 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
            );

            if(course.getQuantity().compareTo(BigDecimal.valueOf(countLessonLearning)) == 0){
                userCourse.setStatus(AppConstant.COMPLETE);
            }

            userCourseRepository.save(userCourse);
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


}
