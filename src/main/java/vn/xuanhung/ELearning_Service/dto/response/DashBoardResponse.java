package vn.xuanhung.ELearning_Service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashBoardResponse {
    Integer amountUser;
    Integer amountCourse;
    Integer amountArticle;
    Integer amountTeacher;
    List<Integer> amountUserEMonth;
}
