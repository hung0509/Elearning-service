package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.dto.request.AuthenticationRequest;
import vn.xuanhung.ELearning_Service.dto.response.AuthenticationResponse;
import vn.xuanhung.ELearning_Service.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/auth")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest req) {
        log.info("$---------Log authentication---------$");
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Authentication success")
                .result(authenticationService.authenticate(req))
                .build();
    }
}
