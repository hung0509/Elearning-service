package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.dto.request.DiscountRequest;
import vn.xuanhung.ELearning_Service.dto.request.LessonRequest;
import vn.xuanhung.ELearning_Service.dto.response.DiscountResponse;
import vn.xuanhung.ELearning_Service.dto.response.LessonResponse;
import vn.xuanhung.ELearning_Service.service.LessonService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/lessons")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LessonController {
    LessonService lessonService;

    @PostMapping
    public ApiResponse<LessonResponse> save(@RequestBody LessonRequest request) {
        log.info("*Log lesson controller - save lesson*");
        return lessonService.save(request);
    }

    @GetMapping
    public ApiResponsePagination<List<LessonResponse>> get(@ModelAttribute LessonRequest request) {
        log.info("*Log controller lesson - get all lesson*");
        return lessonService.findAll(request);
    }

    @GetMapping("/{id}")
    public ApiResponse<LessonResponse> getById(@PathVariable Integer id) {
        log.info("*Log controller lesson - get lesson by ID*");
        return lessonService.findById(id);
    }


    @PutMapping("/{id}")
    public ApiResponse<Void> udpate(@RequestBody LessonRequest request, @PathVariable Integer id) {
        log.info("*Log controller lesson - edit lesson*");
        return lessonService.update(request, id);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Integer id) {
        log.info("*Log controller lesson - delete lesson*");
        return lessonService.deleteById(id);
    }
}
