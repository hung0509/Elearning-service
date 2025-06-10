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
import vn.xuanhung.ELearning_Service.dto.request.DiscountRequest;
import vn.xuanhung.ELearning_Service.dto.response.CategoryResponse;
import vn.xuanhung.ELearning_Service.dto.response.DiscountResponse;
import vn.xuanhung.ELearning_Service.entity.Category;
import vn.xuanhung.ELearning_Service.entity.Discount;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.repository.DiscountRepository;
import vn.xuanhung.ELearning_Service.service.DiscountService;
import vn.xuanhung.ELearning_Service.specification.DiscountSpecification;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IDiscountService implements DiscountService {
    DiscountRepository discountRepository;
    ModelMapper modelMapper;

    @Override
    public ApiResponsePagination<List<DiscountResponse>> findAll(DiscountRequest request) {
        log.info("***Log discount service - get all discount by pagination***");
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getPageSize(),
                Sort.by(Sort.Direction.ASC, "createdAt")
        );
        Specification<Discount> specification = DiscountSpecification.getSpecification(request);

        Page<Discount> discounts = discountRepository.findAll(specification, pageable);

        List<Discount> data = discounts.getContent();
        return ApiResponsePagination.<List<DiscountResponse>>builder()
                .result(data.stream().map(item -> modelMapper.map(item , DiscountResponse.class)).toList())
                .totalPages(discounts.getTotalPages())
                .currentPage(request.getPage())
                .pageSize(request.getPageSize())
                .totalItems(discounts.getTotalElements())
                .build();
    }

    @Override
    public ApiResponse<DiscountResponse> findById(Integer integer) {
        return null;
    }

    @Override
    public ApiResponse<DiscountResponse> save(DiscountRequest req) {
        log.info("***Log discount service - save discount***");
        Discount discount = modelMapper.map(req, Discount.class);
        discount.setIsActive("Y");
        discount = discountRepository.save(discount);

        return ApiResponse.<DiscountResponse>builder()
                .result(modelMapper.map(discount, DiscountResponse.class))
                .build();
    }

    @Override
    public ApiResponse<String> deleteById(Integer integer) {
        return null;
    }

    @Override
    public ApiResponse<DiscountResponse> update(DiscountRequest req) {
        log.info("***Log discount service - alert discount***");
        Discount discount = discountRepository.findById(req.getId())
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_EXIST));

        modelMapper.map(req, discount);
        discount = discountRepository.save(discount);
        return ApiResponse.<DiscountResponse>builder()
                .result(modelMapper.map(discount, DiscountResponse.class))
                .build();
    }
}
