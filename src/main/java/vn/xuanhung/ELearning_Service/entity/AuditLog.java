package vn.xuanhung.ELearning_Service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_log")
@Builder
public class AuditLog extends AbstractMappedEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "value_old")
    String valueOld;

    @Column(name = "value_new")
    String valueNew;

    @Column(name = "field_change")
    String fieldChange;

    @Column(name = "log_level")
    String logLevel;

    @Column(name = "class_audit")
    String classAudit;

    @Column(name = "user_id")
    Integer userId;

    @Column(name = "username")
    String userName;

    @Column(name = "action")
    String action;
}
