package vn.xuanhung.ELearning_Service.service;

import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.BaseServiceGeneric;
import vn.xuanhung.ELearning_Service.dto.request.CategoryRequest;
import vn.xuanhung.ELearning_Service.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    public ApiResponse<List<CategoryResponse>> findAll();

    public ApiResponse<CategoryResponse> save(CategoryRequest request);
}
