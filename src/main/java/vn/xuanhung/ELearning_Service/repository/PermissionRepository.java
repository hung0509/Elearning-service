package vn.xuanhung.ELearning_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.xuanhung.ELearning_Service.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String>{
}
