package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.common.BaseRequest;
import vn.xuanhung.ELearning_Service.dto.request.UserCourseRequest;
import vn.xuanhung.ELearning_Service.dto.request.UserInfoRequest;
import vn.xuanhung.ELearning_Service.dto.response.UserInfoResponse;
import vn.xuanhung.ELearning_Service.service.UserInfoService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserInfoController {
    UserInfoService userInfoService;

    @GetMapping("/all")
    public ApiResponsePagination<List<UserInfoResponse>> getAllUsers(@ModelAttribute BaseRequest req) {
        log.info("Log user controller - get all account");
        return userInfoService.getAll(req);
    }

    @GetMapping
    public ApiResponse<UserInfoResponse> getUserInfo() {
        log.info("Log user controller - get my-info account");
        return userInfoService.getMyInfo();
    }

    @PostMapping("/register/course")
    public ApiResponse<String> registerCourse(@RequestBody UserCourseRequest req) {
        log.info("Log user controller - register course");
        return userInfoService.registerCourse(req);
    }

    @PostMapping("/update")
    public ApiResponse<UserInfoResponse> registerCourse(@RequestBody UserInfoRequest req) {
        log.info("Log user controller - update user info");
        return userInfoService.update(req);
    }
}
