package vn.xuanhung.ELearning_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.xuanhung.ELearning_Service.entity.CourseDocument;

import java.util.List;

@Repository
public interface CourseDocumentRepository extends JpaRepository<CourseDocument, Integer> {
    List<CourseDocument> findAllByCourseIdAndIsActive(Integer courseId, String isActive);
}
