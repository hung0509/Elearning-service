package vn.xuanhung.ELearning_Service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.xuanhung.ELearning_Service.common.BaseRequest;

import java.util.Date;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequest extends BaseRequest {
    Integer id;
    String content;
    Integer userId;
    Integer lessonId;
    Integer articleId;
    Date createdAt;
    Date updatedAt;
    String isActive;;
}
