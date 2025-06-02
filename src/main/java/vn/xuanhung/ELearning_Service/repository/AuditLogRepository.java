package vn.xuanhung.ELearning_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xuanhung.ELearning_Service.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
}
