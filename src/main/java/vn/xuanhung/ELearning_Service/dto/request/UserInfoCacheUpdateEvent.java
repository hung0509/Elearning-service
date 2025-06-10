package vn.xuanhung.ELearning_Service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoCacheUpdateEvent {
    private Integer userId;
    private String action; // "INVALIDATE" | "REBUILD"
}
