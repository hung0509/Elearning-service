package vn.xuanhung.ELearning_Service.service;

import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.common.BaseServiceGeneric;
import vn.xuanhung.ELearning_Service.dto.request.CourseDetailViewRequest;
import vn.xuanhung.ELearning_Service.dto.request.CourseHeaderViewRequest;
import vn.xuanhung.ELearning_Service.dto.request.CourseRequest;
import vn.xuanhung.ELearning_Service.dto.response.CourseDetailViewResponse;
import vn.xuanhung.ELearning_Service.dto.response.CourseHeaderViewResponse;
import vn.xuanhung.ELearning_Service.dto.response.CourseResponse;

import java.util.List;

public interface CourseService extends BaseServiceGeneric<Integer, CourseRequest, CourseResponse> {
    ApiResponsePagination<List<CourseHeaderViewResponse>> getCourseHeader(CourseHeaderViewRequest req);

    ApiResponse<CourseDetailViewResponse> getCourseDetail(CourseDetailViewRequest req);

    ApiResponse<List<CourseHeaderViewResponse>> getCourseHeaderSpecial();
}
