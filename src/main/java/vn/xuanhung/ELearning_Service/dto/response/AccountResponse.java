package vn.xuanhung.ELearning_Service.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountResponse{
    Integer accountId;
    Integer userId;
    RoleResponse role;
    String username;
    String password;
    String isActive;
    String token;
    LocalDateTime expiresAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
