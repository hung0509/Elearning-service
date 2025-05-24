package vn.xuanhung.ELearning_Service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.xuanhung.ELearning_Service.dto.request.AnswerResultRequest;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizResultResponse {
    QuizHeaderResponse quiz;
    UserInfoResponse user;
    Integer totalQuestion;
    Integer amountCorrect;
    Integer amountWrong;
    BigDecimal percentageCorrect;
}
