package vn.xuanhung.ELearning_Service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCommentViewResponse {
    Integer id;
    String content;
    Integer userId;
    Integer lessonId;
    Date createdAt;
    Date updatedAt;
    String isActive;
    String firstName;
    String lastName;
    String avatar;;

}
