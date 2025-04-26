package vn.xuanhung.ELearning_Service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.xuanhung.ELearning_Service.common.BaseRequest;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseHeaderViewRequest extends BaseRequest {
    Integer courseId;
    String courseName;
    Integer categoryId;
    String courseStatus;
    String level;
    Integer userId;
}
