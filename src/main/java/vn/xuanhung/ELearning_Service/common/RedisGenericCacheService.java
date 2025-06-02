package vn.xuanhung.ELearning_Service.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Setter
@Getter
@Slf4j
public class RedisGenericCacheService<T> {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private String prefix = "entity";

    // Hàm load fallback DB theo ID
    private Function<Integer, T> dbLoaderById;

    // Class<T> để deserialize JSON về object generic đúng kiểu
    private Class<T> clazz;

    // Random TTL ± phần trăm, ví dụ ±10%
    private Duration randomizeTTL(Duration baseTTL, double percent) {
        long millis = baseTTL.toMillis();
        long delta = (long) (millis * percent);
        long randomized = millis - delta + ThreadLocalRandom.current().nextLong(delta * 2 + 1);
        return Duration.ofMillis(randomized);
    }

    public Optional<T> getById(Integer id, Duration baseTTL) {
        try {
            if (id == null) {
                log.info("getById called with null id");
                return Optional.empty();
            }
            String key = prefix + ":" + id;
            String cachedJson = redisTemplate.opsForValue().get(key);
            log.info("Cache lookup key {}: {}", key, cachedJson);
            if (cachedJson != null) {
                T obj = objectMapper.readValue(cachedJson, clazz);
                return Optional.ofNullable(obj);
            }
            if (dbLoaderById == null) {
                log.info("dbLoaderById function is not set!");
                return Optional.empty();
            }
            T fromDb = dbLoaderById.apply(id);
            log.info("data: {}", fromDb);
            if (fromDb != null) {
                saveItem(id, fromDb, baseTTL);
                return Optional.of(fromDb);
            }
            return Optional.empty();

        } catch (Exception e) {
            log.error("Error in getById for id {}", id, e);
            return Optional.empty();
        }
    }

    public void cacheIdListAsJson(String key, List<Integer> ids, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(ids);
            redisTemplate.opsForValue().set(key, json,  randomizeTTL(ttl, 0.1));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize and cache ID list", e);
        }
    }

    public List<Optional<T>> getByPrefix(String prefix){
        try{
            String cachedJson = redisTemplate.opsForValue().get(prefix);

            if (cachedJson == null || cachedJson.isEmpty()) {
                return List.of(); // hoặc return Collections.emptyList();
            }

            List<Integer> data = objectMapper.readValue(cachedJson, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, Integer.class));


        } catch (Exception e) {
            log.error("Error in prefix:  {}", prefix);
        }
        return List.of();
    }

    public void saveItem(Integer id, T item, Duration baseTTL) {
        try {
            if (id == null || item == null) {
                log.info("saveItem called with null id or item");
                return;
            }
            String key = prefix + ":" + id;
            String json = objectMapper.writeValueAsString(item);
            Duration ttl = randomizeTTL(baseTTL, 0.1);
            redisTemplate.opsForValue().set(key, json, ttl);
            log.info("Saved item to cache with key {}, TTL {}", key, ttl);
            log.info("json {}", json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize item with id {}", id, e);
        } catch (Exception e) {
            log.error("Error saving item with id {}", id, e);
        }
    }

    public void invalidateById(String id) {
        if (id == null) {
            log.warn("invalidateById called with null id");
            return;
        }
        String key = prefix + ":" + id;
        redisTemplate.delete(key);
        log.debug("Invalidated cache key {}", key);
    }
}


//Role đặt prefix chung: product:id , product:all,     Lấy cái gì thì : thêm cái đó
