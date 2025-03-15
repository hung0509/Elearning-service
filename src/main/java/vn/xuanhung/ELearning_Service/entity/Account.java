package vn.xuanhung.ELearning_Service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import vn.xuanhung.ELearning_Service.common.AbstractMappedEntity;

import java.util.Set;

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

    @Column(name = "is_active", columnDefinition = "Y")
    String isActive;

    @Column(name = "token")
    String token;

    @Column(name = "expires_at")
    DateTime expiresAt;

    @JsonManagedReference
    @OneToOne
    @JoinColumn(name = "role_name")
    Role role;
}
