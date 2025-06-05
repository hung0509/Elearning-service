package vn.xuanhung.ELearning_Service.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.RedisCacheFactory;
import vn.xuanhung.ELearning_Service.common.RedisGenericCacheService;
import vn.xuanhung.ELearning_Service.dto.request.CategoryRequest;
import vn.xuanhung.ELearning_Service.dto.response.CategoryResponse;
import vn.xuanhung.ELearning_Service.entity.Category;
import vn.xuanhung.ELearning_Service.repository.CategoryRepository;
import vn.xuanhung.ELearning_Service.service.CategoryService;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ICategoryService implements CategoryService {
    RedisCacheFactory redisCacheFactory;
    CategoryRepository categoryRepository;
    ModelMapper modelMapper;

    @NonFinal
    String PREFIX_CATEGORY = "category:all";

    @Override
    public ApiResponse<List<CategoryResponse>> findAll() {
        log.info("***Log category service - get all category***");
        RedisGenericCacheService<Category> redisGenericCacheService = redisCacheFactory.create(PREFIX_CATEGORY, Category.class);
        List<Category> categories = redisGenericCacheService.getByPrefix();
        if(!categories.isEmpty() && categories != null) {
            return ApiResponse.<List<CategoryResponse>>builder()
                    .result(categories.stream().map(category
                            -> modelMapper.map(category, CategoryResponse.class)).toList())
                    .build();
        }

        categories = categoryRepository.findAll();
        redisGenericCacheService.saveItemList(categories, Duration.ofHours(6));

        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categories.stream().map(category
                        -> modelMapper.map(category, CategoryResponse.class)).toList())
                .build();
    }


    @Override
    @Transactional
    public ApiResponse<CategoryResponse> save(CategoryRequest req) {
        log.info("***Log category service - save category***");
        Category category = modelMapper.map(req, Category.class);
        category = categoryRepository.save(category);

        return ApiResponse.<CategoryResponse>builder()
                .result(modelMapper.map(category, CategoryResponse.class))
                .build();
    }
}
