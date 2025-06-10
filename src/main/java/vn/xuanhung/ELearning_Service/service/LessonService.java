package vn.xuanhung.ELearning_Service.service;

import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.BaseServiceGeneric;
import vn.xuanhung.ELearning_Service.dto.request.LessonRequest;
import vn.xuanhung.ELearning_Service.dto.request.LessonUpdateRequest;
import vn.xuanhung.ELearning_Service.dto.response.LessonResponse;

public interface LessonService extends BaseServiceGeneric<Integer, LessonRequest, LessonResponse> {
    ApiResponse<LessonResponse> update(LessonUpdateRequest lessonRequest, Integer id);
}
