package vn.xuanhung.ELearning_Service.dto.request;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import vn.xuanhung.ELearning_Service.common.AbstractMappedEntity;
import vn.xuanhung.ELearning_Service.common.BaseRequest;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAccountRequest extends BaseRequest {
    Integer accountId;
    String role;
    String username;
    String password;
    String isActive;
    String token;
    DateTime expiresAt;
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
