package vn.xuanhung.ELearning_Service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoUpdateRequest {
    Integer id;
    String firstName;
    String lastName;
    String gender;
    String dateOfBirth;
    String address;
    String email;
    String phone;
    String balance;
    MultipartFile avatar;
}
