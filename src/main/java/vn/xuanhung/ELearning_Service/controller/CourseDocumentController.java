package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.dto.request.CourseDocumentRequest;
import vn.xuanhung.ELearning_Service.dto.response.CourseDocumentResponse;
import vn.xuanhung.ELearning_Service.service.CourseDocumentService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/documents")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CourseDocumentController {
    CourseDocumentService courseDocumentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CourseDocumentResponse> add(@ModelAttribute CourseDocumentRequest request) {
        log.info("*Log controller documents - save document*");
        return courseDocumentService.save(request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<CourseDocumentResponse> delete(@PathVariable Integer id) {
        log.info("*Log controller documents - delete document*");
        return courseDocumentService.remove(id);
    }
}
