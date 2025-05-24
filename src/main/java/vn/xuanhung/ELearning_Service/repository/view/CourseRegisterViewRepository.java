package vn.xuanhung.ELearning_Service.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.xuanhung.ELearning_Service.entity.Course;
import vn.xuanhung.ELearning_Service.entity.view.CourseRegisterView;

import java.util.List;

@Repository
public interface CourseRegisterViewRepository extends JpaRepository<CourseRegisterView, Integer> {
    List<CourseRegisterView> findAllByUserId(Integer userId);
}
