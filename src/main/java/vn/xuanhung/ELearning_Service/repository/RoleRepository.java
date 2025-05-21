package vn.xuanhung.ELearning_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.xuanhung.ELearning_Service.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    @Query("SELECT a FROM Role a WHERE a.roleName = :roleName")
    Role findByRoleName(String roleName);
}
