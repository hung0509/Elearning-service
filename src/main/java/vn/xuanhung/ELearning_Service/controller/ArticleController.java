package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.dto.request.ArticleRequest;
import vn.xuanhung.ELearning_Service.dto.request.ArticleUpdateRequest;
import vn.xuanhung.ELearning_Service.dto.request.ArticleUserViewRequest;
import vn.xuanhung.ELearning_Service.dto.request.DiscountRequest;
import vn.xuanhung.ELearning_Service.dto.response.ArticleResponse;
import vn.xuanhung.ELearning_Service.dto.response.ArticleUserViewResponse;
import vn.xuanhung.ELearning_Service.dto.response.DiscountResponse;
import vn.xuanhung.ELearning_Service.service.ArticleService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/articles")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ArticleController {
    ArticleService articleService;

    @PostMapping
    public ApiResponse<ArticleResponse> save(@ModelAttribute ArticleRequest request) {
        log.info("*Log controller article - save article*");
        return articleService.save(request);
    }

    @GetMapping
    public ApiResponsePagination<List<ArticleResponse>> get(@ModelAttribute ArticleRequest request) {
        log.info("*Log controller article - get all article*");
        return articleService.findAll(request);
    }

    @GetMapping("/user")
    public ApiResponsePagination<List<ArticleUserViewResponse>> getArticleUser(@ModelAttribute ArticleUserViewRequest req) {
        log.info("*Log controller article - get all article(have info user)*");
        return articleService.getArticleUserView(req);
    }

    @PutMapping
    public ApiResponse<ArticleResponse> update(@RequestBody ArticleUpdateRequest request) {
        log.info("*Log controller article - update article*");
        return articleService.updateArticle(request);
    }
}
