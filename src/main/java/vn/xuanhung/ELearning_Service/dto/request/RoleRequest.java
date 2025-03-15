package vn.xuanhung.ELearning_Service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.xuanhung.ELearning_Service.common.BaseRequest;

import java.util.List;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest extends BaseRequest {
    String roleName;
    String description;
    List<String> listOfPermissions;
}
