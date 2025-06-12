package vn.xuanhung.ELearning_Service.dto.request;

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
public class ArticleUpdateRequest extends BaseRequest {
    Integer id;
    String title;
    String content;
    String description;
    Integer instructorId;
    Date publishedDate;
    String status;
}
