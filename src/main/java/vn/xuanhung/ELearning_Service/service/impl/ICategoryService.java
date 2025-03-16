package vn.xuanhung.ELearning_Service.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.dto.request.CategoryRequest;
import vn.xuanhung.ELearning_Service.dto.response.CategoryResponse;
import vn.xuanhung.ELearning_Service.entity.Category;
import vn.xuanhung.ELearning_Service.repository.CategoryRepository;
import vn.xuanhung.ELearning_Service.service.CategoryService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ICategoryService implements CategoryService {
    CategoryRepository categoryRepository;
    ModelMapper modelMapper;

    @Override
    public ApiResponse<List<CategoryResponse>> findAll() {
        log.info("***Log category service - get all category***");
        List<Category> list = categoryRepository.findAll();

        return ApiResponse.<List<CategoryResponse>>builder()
                .result(list.stream().map(category -> modelMapper.map(category, CategoryResponse.class)).toList())
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
