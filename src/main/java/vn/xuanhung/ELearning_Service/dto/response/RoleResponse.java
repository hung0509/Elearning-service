package vn.xuanhung.ELearning_Service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.xuanhung.ELearning_Service.entity.Permission;

import java.util.Set;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    String roleName;
    String description;
    Set<Permission> permissions;
}
