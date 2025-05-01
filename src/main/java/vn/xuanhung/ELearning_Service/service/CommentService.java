package vn.xuanhung.ELearning_Service.service;

import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.dto.request.CommentRequest;
import vn.xuanhung.ELearning_Service.dto.response.UserCommentViewResponse;

import java.util.List;

public interface CommentService {
    public void sendMessage(CommentRequest comment);

    public ApiResponsePagination<List<UserCommentViewResponse>> getAll(CommentRequest req);
}
