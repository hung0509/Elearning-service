package vn.xuanhung.ELearning_Service.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseRequest {
    int page= 0;
    int pageSize= 10;
    String order= "desc";
    String sortBy= "created";
}
