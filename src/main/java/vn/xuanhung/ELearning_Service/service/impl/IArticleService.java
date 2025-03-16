package vn.xuanhung.ELearning_Service.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.dto.request.ArticleRequest;
import vn.xuanhung.ELearning_Service.dto.response.ArticleResponse;
import vn.xuanhung.ELearning_Service.dto.response.DiscountResponse;
import vn.xuanhung.ELearning_Service.entity.Article;
import vn.xuanhung.ELearning_Service.entity.Discount;
import vn.xuanhung.ELearning_Service.entity.UserInfo;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.repository.ArticleRepository;
import vn.xuanhung.ELearning_Service.repository.UserInfoRepository;
import vn.xuanhung.ELearning_Service.service.ArticleService;
import vn.xuanhung.ELearning_Service.specification.ArticleSpecification;
import vn.xuanhung.ELearning_Service.specification.DiscountSpecification;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IArticleService implements ArticleService {
    ArticleRepository articleRepository;
    UserInfoRepository articleUserRepository;
    ModelMapper modelMapper;

    @Override
    public ApiResponsePagination<List<ArticleResponse>> findAll(ArticleRequest request) {
        log.info("***Log article service - get all article by pagination***");
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Specification<Article> specification = ArticleSpecification.getSpecification(request);

        Page<Article> discounts = articleRepository.findAll(specification, pageable);

        List<Article> data = discounts.getContent();
        return ApiResponsePagination.<List<ArticleResponse>>builder()
                .result(data.stream().map(item -> modelMapper.map(item , ArticleResponse.class)).toList())
                .totalPages(discounts.getTotalPages())
                .currentPage(request.getPage())
                .pageSize(request.getPageSize())
                .totalItems(discounts.getTotalElements())
                .build();
    }

    @Override
    public ApiResponse<ArticleResponse> findById(Integer integer) {
        return null;
    }

    @Override
    public ApiResponse<ArticleResponse> save(ArticleRequest req) {
        log.info("***Log article service - save article***");
        if(req.getInstructorId() != null){
            Boolean check = articleUserRepository.existsById(req.getInstructorId());
            if(check){
                Article article = modelMapper.map(req, Article.class);
                article = articleRepository.save(article);

                return ApiResponse.<ArticleResponse>builder()
                        .result(modelMapper.map(article, ArticleResponse.class))
                        .build();
            }else
                throw new AppException(ErrorCode.ARTICLE_NOT_EXIST);
        }
        throw new AppException(ErrorCode.NOT_ENOUGH_INFO);
    }

    @Override
    public ApiResponse<String> deleteById(Integer integer) {
        return null;
    }
}
