package vn.xuanhung.ELearning_Service.service;

import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.dto.request.CategoryRequest;
import vn.xuanhung.ELearning_Service.dto.request.CourseDocumentRequest;
import vn.xuanhung.ELearning_Service.dto.response.CategoryResponse;
import vn.xuanhung.ELearning_Service.dto.response.CourseDocumentResponse;

public interface CourseDocumentService {
    public ApiResponse<CourseDocumentResponse> save(CourseDocumentRequest request);

    public ApiResponse<CourseDocumentResponse> remove(Integer id);
}
