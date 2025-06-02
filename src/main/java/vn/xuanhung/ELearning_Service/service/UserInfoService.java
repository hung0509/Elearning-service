package vn.xuanhung.ELearning_Service.service;

import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.common.BaseRequest;
import vn.xuanhung.ELearning_Service.dto.request.UserCourseRequest;
import vn.xuanhung.ELearning_Service.dto.request.UserInfoRequest;
import vn.xuanhung.ELearning_Service.dto.request.UserLessonRequest;
import vn.xuanhung.ELearning_Service.dto.response.UserInfoResponse;

import java.util.List;

public interface UserInfoService  {
    ApiResponse<UserInfoResponse> getMyInfo();

    ApiResponse<String> registerCourse(UserCourseRequest req);

    ApiResponse<String> learnLesson(UserLessonRequest req);

    ApiResponsePagination<List<UserInfoResponse>> getAll(BaseRequest req);

    ApiResponse<UserInfoResponse> update(UserInfoRequest req);

    ApiResponse<UserInfoResponse> update2(UserInfoRequest req);
}
