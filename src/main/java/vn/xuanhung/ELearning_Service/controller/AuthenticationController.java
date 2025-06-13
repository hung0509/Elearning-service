package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.dto.request.*;
import vn.xuanhung.ELearning_Service.dto.response.AuthenticationResponse;
import vn.xuanhung.ELearning_Service.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/auth")
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/outbound/authentication")
    ApiResponse<AuthenticationResponse> outboundAuthenticate(@RequestBody OutboundRequest request){
        log.info("$---------Log authentication auth by google---------$");
        var result = authenticationService.outboundAuthentication(request.getCode());
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest req) {
        log.info("$---------Log authentication. auth---------$");
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Authentication success")
                .result(authenticationService.authenticate(req))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@RequestBody IntrospectRequest req) {
        log.info("$---------Log authentication. log out---------$");
        return ApiResponse.<String>builder()
                .message("Authentication success")
                .result(authenticationService.logout(req))
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refresh(@RequestBody IntrospectRequest req) {
        log.info("$---------Log authentication. refresh---------$");
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Authentication success")
                .result(authenticationService.refresh(req))
                .build();
    }

    @PostMapping("/reset")
    public ApiResponse<Void> refresh(@RequestBody UpdateAccountRequest req) {
        log.info("$---------Log authentication. reset password---------$");
        authenticationService.resetPassword(req);
        return ApiResponse.<Void>builder()
                .message("Reset password success")
                .build();
    }
}
