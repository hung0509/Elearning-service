package vn.xuanhung.ELearning_Service.service;

import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.BaseServiceGeneric;
import vn.xuanhung.ELearning_Service.dto.request.DiscountRequest;
import vn.xuanhung.ELearning_Service.dto.response.DiscountResponse;

public interface DiscountService extends BaseServiceGeneric<Integer, DiscountRequest, DiscountResponse> {
    public ApiResponse<DiscountResponse> update(DiscountRequest req);
}
