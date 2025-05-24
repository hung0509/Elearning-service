package vn.xuanhung.ELearning_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xuanhung.ELearning_Service.entity.Answer;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    List<Answer> findByQuestionId(int questionId);
}
