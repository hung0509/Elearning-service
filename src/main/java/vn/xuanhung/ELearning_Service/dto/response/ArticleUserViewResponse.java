package vn.xuanhung.ELearning_Service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import vn.xuanhung.ELearning_Service.common.BaseRequest;

import java.util.Date;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ArticleUserViewResponse extends BaseRequest {
    Integer id;
    String title;
    String content;
    String description;
    String image;
    Integer instructorId;
    Date publishedDate;
    String status;
    String fullName;
}
