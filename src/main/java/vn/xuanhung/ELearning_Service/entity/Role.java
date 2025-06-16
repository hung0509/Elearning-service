package vn.xuanhung.ELearning_Service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.xuanhung.ELearning_Service.audit.listener.AuditEntityListener;

import java.util.Set;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_role")
@EntityListeners(AuditEntityListener.class)
public class Role {
    @Id
    @Column(name = "role_name", length = 20, nullable = false)
    String roleName;

    @Column(name = "description")
    String description;

    @ManyToMany()
    @JoinTable(
            name = "d_role_permission",
            joinColumns = @JoinColumn(name= "role_name"),
            inverseJoinColumns = @JoinColumn(name = "permission_name")
    )
    @JsonManagedReference
    Set<Permission> permissions;

    @JsonManagedReference
    @OneToMany(mappedBy = "role")
    Set<Account> accounts;

}
