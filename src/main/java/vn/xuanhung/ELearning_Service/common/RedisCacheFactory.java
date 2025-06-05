package vn.xuanhung.ELearning_Service.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCacheFactory {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public <T> RedisGenericCacheService<T> create(String prefix, Class<T> clazz) {
        return new RedisGenericCacheService<>(redisTemplate, objectMapper, prefix, clazz);
    }
}
