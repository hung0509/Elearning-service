package vn.xuanhung.ELearning_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.xuanhung.ELearning_Service.entity.UserLesson;

@Repository
public interface UserLessonRepository extends JpaRepository<UserLesson, Integer> {
    UserLesson findByLessonIdAndUserId(Integer lessonId, Integer userId);
}
