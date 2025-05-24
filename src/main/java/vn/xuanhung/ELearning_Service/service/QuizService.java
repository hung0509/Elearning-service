package vn.xuanhung.ELearning_Service.service;

import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.dto.request.QuizRequest;
import vn.xuanhung.ELearning_Service.dto.request.QuizResultRequest;
import vn.xuanhung.ELearning_Service.dto.response.QuizDetailResponse;
import vn.xuanhung.ELearning_Service.dto.response.QuizHeaderResponse;
import vn.xuanhung.ELearning_Service.dto.response.QuizResultResponse;

public interface QuizService {
    public ApiResponse<QuizDetailResponse> add(QuizRequest req);

    public ApiResponse<QuizHeaderResponse> remove(Integer id);

    public ApiResponse<QuizDetailResponse> getById(Integer id);

    public ApiResponse<QuizResultResponse> calculate(QuizResultRequest req);
}
