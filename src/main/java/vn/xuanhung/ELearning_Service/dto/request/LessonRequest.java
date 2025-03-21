package vn.xuanhung.ELearning_Service.dto.request;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import vn.xuanhung.ELearning_Service.common.BaseRequest;

import java.math.BigDecimal;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonRequest extends BaseRequest {
    Integer id;
    Integer courseId;
    String lessonName;
    MultipartFile urlLesson;
    String description;
    BigDecimal lessonTime;
    String isActive;
}
