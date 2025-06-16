package vn.xuanhung.ELearning_Service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.joda.time.DateTime;
import vn.xuanhung.ELearning_Service.audit.listener.AuditEntityListener;

import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_invalidated_token")
@EntityListeners(AuditEntityListener.class)
public class InvalidatedToken {
    @Id
    String id;

    @Column(name = "expire_date")
    Date expireDate;
}
