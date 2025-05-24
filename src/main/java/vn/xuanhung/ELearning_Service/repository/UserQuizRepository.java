package vn.xuanhung.ELearning_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.xuanhung.ELearning_Service.entity.UserQuiz;

@Repository
public interface UserQuizRepository extends JpaRepository<UserQuiz, Integer> {
}
