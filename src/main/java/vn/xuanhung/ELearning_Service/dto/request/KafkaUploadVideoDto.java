package vn.xuanhung.ELearning_Service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaUploadVideoDto {
    Integer courseId;
    Integer lessonId;
    String s3Url;
    String playlistId;
    String title;
    String description;
}
