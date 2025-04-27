package vn.xuanhung.ELearning_Service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaypalRequest {
    Integer courseId;
    Integer userId;
    BigDecimal balance;
    String description;
    String urlError;
    String urlSuccess;
    String currency;
    String intent;
    String method;
}
