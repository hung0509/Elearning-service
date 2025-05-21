package vn.xuanhung.ELearning_Service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;

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
    String userAvatar;;
    String trailer;
    String courseStatus;
    BigDecimal priceEntered;
    BigDecimal priceAfterReduce;
    BigDecimal courseDuration;
    BigDecimal quantity;
    String isActive;
    String level;
    Date createdAt;
    CertificateResponse certificate;
    DiscountResponse discount;
    CategoryResponse category;

}
