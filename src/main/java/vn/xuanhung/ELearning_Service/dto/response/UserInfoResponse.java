package vn.xuanhung.ELearning_Service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoResponse {
    Integer id;
    String firstName;
    String lastName;
    String gender;
    String dateOfBirth;
    String address;
    String email;
    String phone;
    String balance;
    String avatar;
}
