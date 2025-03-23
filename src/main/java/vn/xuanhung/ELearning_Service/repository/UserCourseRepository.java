package vn.xuanhung.ELearning_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.xuanhung.ELearning_Service.entity.UserCourse;

@Repository
public interface UserCourseRepository  extends JpaRepository<UserCourse, Integer> {
    UserCourse findByCourseIdandUserId(int courseId, int userId);
}
