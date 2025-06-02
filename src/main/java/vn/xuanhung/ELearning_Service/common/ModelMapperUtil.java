package vn.xuanhung.ELearning_Service.common;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import vn.xuanhung.ELearning_Service.entity.AuditLog;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class ModelMapperUtil {

    @SuppressWarnings("unchecked")
    public static <S, D> List<AuditLog> mapWithLog(S source, D destination, ModelMapper modelMapper) {
        AtomicReference<List<AuditLog>> auditLogs = new AtomicReference<>(new ArrayList<>());
        if (source == null || destination == null) {
            log.warn("Source or Destination is null, mapping skipped.");
            return null;
        }

        Class<S> sourceType = (Class<S>) source.getClass();
        Class<D> destinationType = (Class<D>) destination.getClass();

        TypeMap<S, D> typeMap = modelMapper.getTypeMap(sourceType, destinationType);
        if (typeMap == null) {
            // Nếu chưa có TypeMap, tạo mới
            typeMap = modelMapper.createTypeMap(sourceType, destinationType);
        }

        // Chỉ set postConverter 1 lần nếu chưa có
        if (typeMap.getPreConverter() == null) {
            typeMap.setPreConverter(context -> {
                S src = context.getSource();
                D dst = context.getDestination();

                log.info(">> Post Mapping Log: {} → {}", src.getClass().getSimpleName(), dst.getClass().getSimpleName());
                log.info("Source data: {}", src);
                log.info("Destination data: {}", dst);
                auditLogs.set(logChangedFields(src, dst));

                return dst;
            });
        }

        // Thực hiện mapping từ source sang destination (map vào đối tượng destination hiện có)
        modelMapper.map(source, destination);
        return auditLogs.get();
    }


    private static List<AuditLog> logChangedFields(Object source, Object target) {
        if (source == null || target == null) return null;

        List<AuditLog> auditLogs = new ArrayList<>();

        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        Field[] sourceFields = sourceClass.getDeclaredFields();
        Field[] targetFields = targetClass.getDeclaredFields();

        for (Field srcField : sourceFields) {
            srcField.setAccessible(true);
            // Bỏ qua các field kỹ thuật
            if (Modifier.isStatic(srcField.getModifiers())
                    || Modifier.isTransient(srcField.getModifiers())
                    || srcField.getName().equals("log")
                    || srcField.getName().equals("serialVersionUID")) {
                continue;
            }

            try {
                Object srcValue = srcField.get(source);

                for (Field tgtField : targetFields) {
                    if (tgtField.getName().equals(srcField.getName())
                            && tgtField.getType().isAssignableFrom(srcField.getType())) {
                        tgtField.setAccessible(true);
                        Object tgtValue = tgtField.get(target);

                        if (!Objects.equals(srcValue, tgtValue) && srcValue != null) {
                            AuditLog auditLog = AuditLog.builder()
                                    .fieldChange(srcField.getName())
                                    .valueNew(tgtValue + "")
                                    .valueOld(srcValue + "")
                                    .classAudit(targetClass.getName())
                                    .build();

                            auditLogs.add(auditLog);
                            log.info("Field '{}' changed: {} → {}", srcField.getName(), srcValue, tgtValue);
                        }
                    }
                }

            } catch (IllegalAccessException e) {
                log.warn("Unable to access field: {}", srcField.getName(), e);
            }
        }
        return auditLogs;
    }
}
