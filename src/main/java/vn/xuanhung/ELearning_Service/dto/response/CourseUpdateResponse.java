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
public class CourseUpdateResponse {
    Integer id;
    Integer categoryId;
    String courseName;
    String description;
    BigDecimal priceEntered;
    String discountCode;
    String isActive;
    String certificateName;
    String level;
}
