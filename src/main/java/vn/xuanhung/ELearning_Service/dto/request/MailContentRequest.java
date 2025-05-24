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
public class MailContentRequest {
    String title;
    String content;
    String to;
    Integer userId;
    String password;
}
