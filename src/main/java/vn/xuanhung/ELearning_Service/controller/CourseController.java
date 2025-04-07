package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.dto.request.CourseRequest;
import vn.xuanhung.ELearning_Service.dto.response.CourseResponse;
import vn.xuanhung.ELearning_Service.service.CourseService;

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
}
