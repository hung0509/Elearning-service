package vn.xuanhung.ELearning_Service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponse {
    Integer id;
    Integer instructorId;
    Integer categoryId;
    String courseName;
    String description;
    String avatar;
    String trailer;
    BigDecimal priceEntered;
    BigDecimal priceAfterReduce;
    BigDecimal courseDuration;
    BigDecimal quantity;
    Integer discountId;
    String isActive;
    String certificateId;
    String level;
}
