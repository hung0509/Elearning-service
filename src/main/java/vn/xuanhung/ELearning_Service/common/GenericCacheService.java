package vn.xuanhung.ELearning_Service.common;

import java.time.Duration;
import java.util.List;

public interface GenericCacheService<T> {
    void save(T entity, String id, long score, Duration ttl);
    void update(T entity, String id, Duration ttl);
    void delete(String id);
    List<T> getList(String listKey, int start, int end);
    T getById(String id);
}
