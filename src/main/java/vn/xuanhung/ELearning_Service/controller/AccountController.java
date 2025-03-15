package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.dto.request.CreateAccountRequest;
import vn.xuanhung.ELearning_Service.dto.response.AccountResponse;
import vn.xuanhung.ELearning_Service.service.AccountService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/accounts")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AccountController {
    AccountService accountService;

    @PostMapping
    public ApiResponse<AccountResponse> addPermission(@RequestBody CreateAccountRequest req) {
        log.info("Log controller account - save account");
        return accountService.save(req);
    }
}
