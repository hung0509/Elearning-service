package vn.xuanhung.ELearning_Service.service;

import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.common.BaseServiceGeneric;
import vn.xuanhung.ELearning_Service.dto.request.ArticleRequest;
import vn.xuanhung.ELearning_Service.dto.request.ArticleUserViewRequest;
import vn.xuanhung.ELearning_Service.dto.response.ArticleResponse;
import vn.xuanhung.ELearning_Service.dto.response.ArticleUserViewResponse;

import java.util.List;

public interface ArticleService extends BaseServiceGeneric<Integer, ArticleRequest, ArticleResponse> {
    public ApiResponsePagination<List<ArticleUserViewResponse>> getArticleUserView(ArticleUserViewRequest articleRequest);
}
