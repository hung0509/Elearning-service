package vn.xuanhung.ELearning_Service.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@Table(name = "d_permission")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "permissionName")
public class Permission {
    @Id
    @Column(name = "permission_name" , length = 50, nullable = false)
    String permissionName;

    @Column(name = "description")
    String description;

    @ManyToMany(mappedBy = "permissions")
    //@JsonBackReference(value = "role-permissions")
    Set<Role> roles;
}
