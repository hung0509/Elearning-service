package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.dto.request.QuizRequest;
import vn.xuanhung.ELearning_Service.dto.response.QuizDetailResponse;
import vn.xuanhung.ELearning_Service.dto.response.QuizHeaderResponse;
import vn.xuanhung.ELearning_Service.service.QuizService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/quizzes")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class QuizController {
    QuizService quizService;

    @PostMapping
    public ApiResponse<QuizDetailResponse> add(@RequestBody QuizRequest req){
        log.info("Log controller quiz - save quiz");
        return quizService.add(req);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<QuizHeaderResponse> delete(@PathVariable int id){
        log.info("Log controller quiz - delete quiz");
        return quizService.remove(id);
    }
}
