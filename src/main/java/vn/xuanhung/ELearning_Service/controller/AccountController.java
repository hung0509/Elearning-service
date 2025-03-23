package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.dto.request.CreateAccountRequest;
import vn.xuanhung.ELearning_Service.dto.request.UpdateAccountRequest;
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
    public ApiResponse<AccountResponse> addAccount(@RequestBody CreateAccountRequest req) {
        log.info("Log controller account - save account");
        return accountService.save(req);
    }

    @GetMapping("/active/{id}")
    public ApiResponse<AccountResponse> active(@PathVariable Integer id) {
        log.info("Log controller account - change status account is active");
        return accountService.active(id);
    }

    @PostMapping("/update")
    public ApiResponse<AccountResponse> updateAccount(@RequestBody UpdateAccountRequest req) {
        log.info("Log controller account - change password or active account");
        return accountService.update(req);
    }
}
