package vn.xuanhung.ELearning_Service.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.dto.response.UserInfoResponse;
import vn.xuanhung.ELearning_Service.entity.UserInfo;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.jwt.UserDetailCustom;
import vn.xuanhung.ELearning_Service.repository.UserInfoRepository;
import vn.xuanhung.ELearning_Service.service.UserInfoService;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IUserInfoService implements UserInfoService {
    UserInfoRepository userInfoRepository;
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
}
