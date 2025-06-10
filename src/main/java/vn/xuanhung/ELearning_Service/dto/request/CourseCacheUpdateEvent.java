package vn.xuanhung.ELearning_Service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCacheUpdateEvent {
    private Integer courseId;
    private String action; // "INVALIDATE" | "REBUILD"
}
