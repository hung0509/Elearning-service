package vn.xuanhung.ELearning_Service.service;

import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.BaseServiceGeneric;
import vn.xuanhung.ELearning_Service.dto.request.RoleRequest;
import vn.xuanhung.ELearning_Service.dto.response.RoleResponse;
import vn.xuanhung.ELearning_Service.dto.response.UserInfoResponse;

public interface UserInfoService  {
    ApiResponse<UserInfoResponse> getMyInfo();
}
