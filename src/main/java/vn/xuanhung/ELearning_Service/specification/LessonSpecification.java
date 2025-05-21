package vn.xuanhung.ELearning_Service.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.xuanhung.ELearning_Service.dto.request.ArticleRequest;
import vn.xuanhung.ELearning_Service.dto.request.LessonRequest;
import vn.xuanhung.ELearning_Service.entity.Article;
import vn.xuanhung.ELearning_Service.entity.Lesson;

import java.math.BigDecimal;

public class LessonSpecification {
    public static Specification<Lesson> isCourseId(Integer courseId)
    {
        return (root, query, criteriaBuilder) ->
                courseId == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("courseId"), courseId);
    }


    public static Specification<Lesson> isLessonName(String lessonName)
    {
        return (root, query, criteriaBuilder) ->
                lessonName == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(root.get("courseId"), "%" + lessonName + "%");
    }

    public static Specification<Lesson> isActive(String isActive){
        return (root, query, criteriaBuilder) ->
                isActive == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(root.get("isActive"), isActive);
    }

    public static Specification<Lesson> getSpecification(LessonRequest req) {
        Specification<Lesson> spec = Specification.where(isCourseId(req.getCourseId()))
                .and(isLessonName(req.getLessonName()));
        return spec;
    }
}
