package vn.xuanhung.ELearning_Service.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.UserCourseRequest;
import vn.xuanhung.ELearning_Service.dto.request.UserLessonRequest;
import vn.xuanhung.ELearning_Service.dto.response.UserInfoResponse;
import vn.xuanhung.ELearning_Service.entity.*;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.jwt.UserDetailCustom;
import vn.xuanhung.ELearning_Service.repository.*;
import vn.xuanhung.ELearning_Service.service.UserInfoService;

import java.math.BigDecimal;
import java.util.Date;

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

    ModelMapper modelMapper;


    @Override
    public ApiResponse<UserInfoResponse> getMyInfo() {
        log.info("***Log user-info service - get my info account***");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailCustom userDetails) {
            Integer id =  userDetails.getUserId(); // Retrieve the userId
            UserInfo userInfo = userInfoRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
            return ApiResponse.<UserInfoResponse>builder()
                    .result( modelMapper.map(userInfo, UserInfoResponse.class))
                    .build();
        }
        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    @Override
    public ApiResponse<String> registerCourse(UserCourseRequest req) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailCustom userDetails) {
            Integer userId = userDetails.getUserId();

            if(!courseRepository.existsById(req.getCourseId())) throw new AppException(ErrorCode.COURSE_NOT_EXIST);

            UserCourse userCourse = UserCourse.builder()
                    .userId(userId)
                    .courseId(req.getCourseId())
                    .status(AppConstant.REGISTER)
                    .enrollmentDate(new Date())
                    .progression(BigDecimal.valueOf(0))
                    .build();

            //Xử lý tiền paypal
            userCourseRepository.save(userCourse);
            return ApiResponse.<String>builder()
                    .result("Register course successfully!")
                    .build();
        }

        throw new AppException(ErrorCode.UNAUTHENTICATED);
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

            UserCourse userCourse = userCourseRepository.findByCourseIdandUserId(lesson.getCourseId(), userId);

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


}
