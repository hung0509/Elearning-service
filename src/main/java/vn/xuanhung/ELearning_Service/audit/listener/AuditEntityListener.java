package vn.xuanhung.ELearning_Service.audit.listener;

import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import vn.xuanhung.ELearning_Service.audit.config.SpringContext;
import vn.xuanhung.ELearning_Service.audit.context.AuditorContext;
import vn.xuanhung.ELearning_Service.audit.util.AuditLogUtil;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.AuditLogRequest;
import vn.xuanhung.ELearning_Service.entity.AuditLog;
import vn.xuanhung.ELearning_Service.repository.AuditLogRepository;

import java.util.List;
import java.util.Set;

@Slf4j
public class AuditEntityListener {

    private static final ThreadLocal<Object> oldStateHolder = new ThreadLocal<>();

    @PrePersist
    public void prePersist(Object entity) {
        log.info("Pre persist...");
        List<AuditLog> logs = AuditLogUtil.logCreate(entity, getExcludedFields(entity));
        saveLogs(logs);
    }

    @PreRemove
    public void preRemove(Object entity) {
        log.info("Pre remove...");
        List<AuditLog> logs = AuditLogUtil.logDelete(entity, getExcludedFields(entity));
        saveLogs(logs);
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        oldStateHolder.set(AuditLogUtil.deepCopy(entity)); // Optional deep copy
    }

    @PostUpdate
    public void postUpdate(Object entity) {
        log.info("Pre update...");
        Object oldState = oldStateHolder.get();
        log.info("Obj old: {}", oldState);
        log.info("Obj new: {}", entity);

        List<AuditLog> logs = AuditLogUtil.logUpdate(oldState, entity, getExcludedFields(entity));
        saveLogs(logs);
        oldStateHolder.remove();
    }

    private Set<String> getExcludedFields(Object entity) {
        return Set.of("createdAt", "updatedAt", "serialVersionUID"); //Những trường mặc định
    }

    private void saveLogs(List<AuditLog> logs) {
        log.info("Dto log: {}", logs);
        if (!logs.isEmpty()) {
            KafkaTemplate<String, Object> kafkaTemplate = SpringContext.getBean(KafkaTemplate.class);
            AuditLogRequest auditLogRequest = AuditLogRequest.builder()
                    .auditLogs(logs)
                    .build();
            log.info("Dto log: {}", logs);
            kafkaTemplate.send(AppConstant.Topic.WRITE_LOG, auditLogRequest);
        }
    }
}
