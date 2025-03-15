package vn.xuanhung.ELearning_Service.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_permission")
public class Permission {
    @Id
    @Column(name = "permission_name" , length = 50, nullable = false)
    String permissionName;

    @Column(name = "description")
    String description;

    @ManyToMany(mappedBy = "permissions")
    @JsonBackReference
    Set<Role> roles;
}
