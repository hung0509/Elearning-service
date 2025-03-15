package vn.xuanhung.ELearning_Service.service;

import vn.xuanhung.ELearning_Service.common.BaseServiceGeneric;
import vn.xuanhung.ELearning_Service.dto.request.CreateAccountRequest;
import vn.xuanhung.ELearning_Service.dto.response.AccountResponse;

public interface AccountService extends BaseServiceGeneric<Integer, CreateAccountRequest, AccountResponse> {
}
