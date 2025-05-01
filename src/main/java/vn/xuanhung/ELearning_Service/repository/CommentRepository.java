package vn.xuanhung.ELearning_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.xuanhung.ELearning_Service.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
