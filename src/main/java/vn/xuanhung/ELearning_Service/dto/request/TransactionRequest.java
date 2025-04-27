package vn.xuanhung.ELearning_Service.dto.request;

import jakarta.persistence.Column;
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
public class TransactionRequest {
    Integer id;
    String paymentId;
    Integer courseId;
    Integer userId;
    BigDecimal balance;
    String description;
    String paymentMethod;
    Date paymentDate;
    String statusPayment;
    String currency;
}
