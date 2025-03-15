package vn.xuanhung.ELearning_Service.common;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractMappedEntity {
    @Column(name = "created_at",updatable = false)
    @CreationTimestamp
    private DateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private DateTime updatedAt;
}
