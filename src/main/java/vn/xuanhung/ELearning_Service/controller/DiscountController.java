package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.dto.request.DiscountRequest;
import vn.xuanhung.ELearning_Service.dto.response.CategoryResponse;
import vn.xuanhung.ELearning_Service.dto.response.DiscountResponse;
import vn.xuanhung.ELearning_Service.service.DiscountService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/discounts")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DiscountController {
    DiscountService discountService;

    @PostMapping
    public ApiResponse<DiscountResponse> save(@RequestBody DiscountRequest request) {
        log.info("*Log controller discount - save discount*");
        return discountService.save(request);
    }

    @GetMapping
    public ApiResponsePagination<List<DiscountResponse>> get(@ModelAttribute DiscountRequest request) {
        log.info("*Log controller discount - get all discount*");
        return discountService.findAll(request);
    }

    @PutMapping("/{id}")
    public ApiResponse<DiscountResponse> udpate(@RequestBody DiscountRequest request) {
        log.info("*Log controller discount - alert discount*");
        return discountService.update(request);
    }
}
