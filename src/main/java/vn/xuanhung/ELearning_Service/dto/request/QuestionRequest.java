package vn.xuanhung.ELearning_Service.dto.request;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.xuanhung.ELearning_Service.dto.response.AnswerResponse;

import java.util.List;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionRequest {
    Integer id;
    Integer quizId;
    String questionText;
//    String questionType;
    String description;
    List<AnswerRequest> answers;
}
