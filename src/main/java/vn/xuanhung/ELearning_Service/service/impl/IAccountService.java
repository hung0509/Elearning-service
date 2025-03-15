package vn.xuanhung.ELearning_Service.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.CreateAccountRequest;
import vn.xuanhung.ELearning_Service.dto.response.AccountResponse;
import vn.xuanhung.ELearning_Service.entity.Account;
import vn.xuanhung.ELearning_Service.entity.Role;
import vn.xuanhung.ELearning_Service.entity.UserInfo;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.repository.AccountRepository;
import vn.xuanhung.ELearning_Service.repository.RoleReposiroty;
import vn.xuanhung.ELearning_Service.service.AccountService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IAccountService implements AccountService {
    AccountRepository accountRepository;
    RoleReposiroty roleReposiroty;
    ModelMapper modelMapper;

    @Override
    public ApiResponsePagination<List<AccountResponse>> findAll(CreateAccountRequest request) {
        return null;
    }

    @Override
    public ApiResponse<AccountResponse> findById(Integer integer) {
        return null;
    }

    @Override
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
            throw new AppException(ErrorCode.NOT_ENOUGH_INFO);
        }

        UserInfo userInfo = UserInfo.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .build();

        account.setIsActive("Y");
        account = accountRepository.save(account);
        return ApiResponse.<AccountResponse>builder()
                .result(modelMapper.map(account, AccountResponse.class))
                .build();
    }

    @Override
    public ApiResponse<String> deleteById(Integer integer) {
        return null;
    }
}
