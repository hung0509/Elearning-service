package vn.xuanhung.ELearning_Service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Date;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_account")
public class Account extends AbstractMappedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    Integer accountId;

    @Column(name = "user_id", nullable = false)
    Integer userId;

    @Column(name = "username", nullable = false, unique = true)
    String username;

    @Column(name = "password")
    String password;

    @Column(name = "is_active")
    String isActive;

    @Column(name = "token")
    String token;

    @Column(name = "expires_at")
    Date expiresAt;

    @JsonManagedReference
    @OneToOne
    @JoinColumn(name = "role_name")
    Role role;
}
