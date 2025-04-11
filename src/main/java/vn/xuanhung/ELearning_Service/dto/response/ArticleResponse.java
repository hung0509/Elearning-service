package vn.xuanhung.ELearning_Service.dto.response;

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
public class ArticleResponse {
    Integer id;
    String title;
    String description;
    String image;
    String content;
    Integer instructorId;
    Date publishedDate;
    String status;
    Date createdAt;
    Date updatedAt;
}
