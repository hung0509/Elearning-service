package vn.xuanhung.ELearning_Service.audit.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import vn.xuanhung.ELearning_Service.audit.annotation.NoAudit;
import vn.xuanhung.ELearning_Service.entity.AuditLog;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@Slf4j
public class AuditLogUtil {

    public static List<AuditLog> logCreate(Object entity, Set<String> excluded) {
        return buildLogs(null, entity, excluded, "CREATE");
    }

    public static List<AuditLog> logDelete(Object entity, Set<String> excluded) {
        return buildLogs(entity, null, excluded, "DELETE");
    }

    public static List<AuditLog> logUpdate(Object oldObj, Object newObj, Set<String> excluded) {
        return buildLogs(oldObj, newObj, excluded, "UPDATE");
    }

    private static List<AuditLog> buildLogs(Object oldObj, Object newObj, Set<String> excluded, String action) {
        if (oldObj == null && newObj == null) return Collections.emptyList();

        List<AuditLog> logs = new ArrayList<>();
        Object reference = (newObj != null) ? newObj : oldObj;
        Class<?> clazz = reference.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if (shouldSkipField(field, excluded)) continue;

            try {
                Object oldVal = oldObj != null ? field.get(oldObj) : null;
                Object newVal = newObj != null ? field.get(newObj) : null;

                if (!Objects.equals(oldVal, newVal)) {
                    log.info("Field changed: {}, from '{}' to '{}'", fieldName, oldVal, newVal);
                    logs.add(AuditLog.builder()
                            .fieldChange(fieldName)
                            .valueOld(oldVal != null ? oldVal.toString() : null)
                            .valueNew(newVal != null ? newVal.toString() : null)
                            .classAudit(clazz.getSimpleName())
                            .action(action)
                            .build());
                }
            } catch (IllegalAccessException e) {
                log.warn("Can't access field: {}", fieldName);
            }
        }
        return logs;
    }

    private static boolean shouldSkipField(Field field, Set<String> excluded) {
        return Modifier.isStatic(field.getModifiers())
                || Modifier.isTransient(field.getModifiers())
                || field.isAnnotationPresent(NoAudit.class)
                || (excluded != null && excluded.contains(field.getName()));
    }

    public static <T> T deepCopy(Object source) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(source);
            return (T) objectMapper.readValue(json, source.getClass());
        } catch (Exception e) {
            throw new RuntimeException("Deep copy failed", e);
        }
    }
}
