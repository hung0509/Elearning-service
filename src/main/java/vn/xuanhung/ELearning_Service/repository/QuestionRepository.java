package vn.xuanhung.ELearning_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xuanhung.ELearning_Service.entity.Question;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findAllByQuizId(int quizId);
}
