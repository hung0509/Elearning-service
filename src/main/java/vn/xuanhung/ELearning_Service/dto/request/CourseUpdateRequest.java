package vn.xuanhung.ELearning_Service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseUpdateRequest {
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
