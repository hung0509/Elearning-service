package vn.xuanhung.ELearning_Service.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.*;
import vn.xuanhung.ELearning_Service.dto.response.AccountResponse;
import vn.xuanhung.ELearning_Service.entity.Account;
import vn.xuanhung.ELearning_Service.entity.Role;
import vn.xuanhung.ELearning_Service.entity.UserInfo;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.repository.AccountRepository;
import vn.xuanhung.ELearning_Service.repository.RoleReposiroty;
import vn.xuanhung.ELearning_Service.repository.UserInfoRepository;
import vn.xuanhung.ELearning_Service.service.AccountService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IAccountService implements AccountService {
    AccountRepository accountRepository;
    UserInfoRepository userInfoRepository;
    RoleReposiroty roleReposiroty;
    ModelMapper modelMapper;
    MailService mailService;
    PasswordEncoder passwordEncoder;
    KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public ApiResponsePagination<List<AccountResponse>> findAll(CreateAccountRequest request) {
        return null;
    }

    @Override
    public ApiResponse<AccountResponse> findById(Integer integer) {
        return null;
    }

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    public ApiResponse<AccountResponse> save(CreateAccountRequest req) {
        log.info("***Log account service - save account***");
        Account account = modelMapper.map(req, Account.class);

        if(req.getRole() != null){
            if(req.getRole().equals(AppConstant.Role.ADMIN) || req.getRole().equals(AppConstant.Role.USER)) {
                Role role = roleReposiroty.findById(req.getRole())
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));
                account.setRole(role);
            }else{
                throw new AppException(ErrorCode.SYSTEM_ERROR);
            }
        }else{
            Role role = roleReposiroty.findById(AppConstant.Role.USER)
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));
            account.setRole(role);
        }

        UserInfo userInfo = UserInfo.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .build();
        userInfo = userInfoRepository.saveAndFlush(userInfo);

        account.setPassword(passwordEncoder.encode(req.getPassword()));
        account.setIsActive("N");
        account.setUserId(userInfo.getId());
        account = accountRepository.save(account);

        MailContentRequest mailContentRequest = MailContentRequest.builder()
                .to(userInfo.getLastName() + " " + userInfo.getFirstName())
                .title("Welcome new members")
                .userId(userInfo.getId())
                .build();
        String fromMail = mailService.formGetActiveAccount(mailContentRequest);
        MailRequest mailRequest = MailRequest.builder()
                .toEmail(req.getEmail())
                .subject("Confirm account activation")
                .htmlContent(fromMail)
                .build();
        try {
            kafkaTemplate.send(AppConstant.Topic.EMAIL_TOPIC, mailRequest).get();
            log.info("Kafka send");
        }catch (Exception e){
            e.printStackTrace();
        }
        return ApiResponse.<AccountResponse>builder()
                .result(modelMapper.map(account, AccountResponse.class))
                .build();
    }

    @Override
    public ApiResponse<String> deleteById(Integer integer) {
        return null;
    }

    @Override
    public ApiResponse<AccountResponse> update(UpdateAccountRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if(username != null && !username.isEmpty()){
            Account account = accountRepository.findByUsername(username);
            if(account != null){
                modelMapper.map(request, account);
                account.setPassword(passwordEncoder.encode(request.getPassword()));

                account = accountRepository.save(account);
                return ApiResponse.<AccountResponse>builder()
                        .message("Update successfully!")
                        .result(modelMapper.map(account, AccountResponse.class))
                        .build();
            }
        }
        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    @Override
    public ApiResponse<AccountResponse> active(Integer id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        account.setIsActive("Y");
        account = accountRepository.save(account);
        return ApiResponse.<AccountResponse>builder()
                .result(modelMapper.map(account, AccountResponse.class))
                .build();
    }
}
