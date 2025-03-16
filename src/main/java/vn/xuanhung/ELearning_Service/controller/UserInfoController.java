package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.dto.response.UserInfoResponse;
import vn.xuanhung.ELearning_Service.entity.UserInfo;
import vn.xuanhung.ELearning_Service.service.UserInfoService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserInfoController {
    UserInfoService userInfoService;

    @GetMapping
    public ApiResponse<UserInfoResponse> getUserInfo() {
        log.info("Log user controller - get my-info account");
        return userInfoService.getMyInfo();
    }
}
