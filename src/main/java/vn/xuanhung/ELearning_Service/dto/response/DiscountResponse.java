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
public class DiscountResponse {
    Integer id;
    String discountCode;
    String discountDescription;
    BigDecimal discountRate;
    String isActive;
    Date expiredDate;
    Date createdAt;
    Date updatedAt;
}
