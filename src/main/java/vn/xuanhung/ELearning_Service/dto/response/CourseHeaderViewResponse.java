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
public class CourseHeaderViewResponse {
    Integer id;
    String courseName;
    Integer instructorId;
    String fullName;
    String description;
    String avatar;
    String trailer;
    BigDecimal priceEntered;
    BigDecimal priceAfterReduce;
    BigDecimal courseDuration;
    BigDecimal quantity;
    String isActive;
    String level;
    CertificateResponse certificate;
    DiscountResponse discount;
    CategoryResponse category;
}
