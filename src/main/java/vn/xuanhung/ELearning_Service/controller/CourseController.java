package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.dto.request.CourseDetailViewRequest;
import vn.xuanhung.ELearning_Service.dto.request.CourseHeaderViewRequest;
import vn.xuanhung.ELearning_Service.dto.request.CourseRequest;
import vn.xuanhung.ELearning_Service.dto.response.CourseDetailViewResponse;
import vn.xuanhung.ELearning_Service.dto.response.CourseHeaderViewResponse;
import vn.xuanhung.ELearning_Service.dto.response.CourseResponse;
import vn.xuanhung.ELearning_Service.service.CourseService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/courses")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CourseController {
    CourseService courseService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CourseResponse> addCourse(@ModelAttribute CourseRequest req) {
        log.info("*Log controller course - save course*");
        return courseService.save(req);
    }

    @GetMapping
    public ApiResponsePagination<List<CourseHeaderViewResponse>> getCourseHeader(@ModelAttribute CourseHeaderViewRequest req) {
        log.info("*Log controller course - get header course*");
        return courseService.getCourseHeader(req);
    }

    @GetMapping("/detail")
    public ApiResponse<CourseDetailViewResponse> getCourseDetail(@ModelAttribute CourseDetailViewRequest req) {
        log.info("*Log controller course - get detail course*");
        return courseService.getCourseDetail(req);
    }

    @GetMapping("/special")
    public ApiResponse<List<CourseHeaderViewResponse>> getCourseHeader() {
        log.info("*Log controller course - get header course special*");
        return courseService.getCourseHeaderSpecial();
    }
}
