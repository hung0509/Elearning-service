package vn.xuanhung.ELearning_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xuanhung.ELearning_Service.entity.UserCertificate;
import vn.xuanhung.ELearning_Service.entity.view.CourseRegisterView;

import java.util.List;

public interface UserCertificateRepository extends JpaRepository<UserCertificate, Integer> {
    List<UserCertificate> findAllByUserId(Integer userId);
}
