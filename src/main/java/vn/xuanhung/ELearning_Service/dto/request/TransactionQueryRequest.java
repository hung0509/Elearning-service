package vn.xuanhung.ELearning_Service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.xuanhung.ELearning_Service.common.BaseRequest;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionQueryRequest extends BaseRequest {
    Integer id;
    String paymentId;
    BigDecimal fromBalance;
    BigDecimal toBalance;
    Date paymentDate;
    String statusPayment;
}
