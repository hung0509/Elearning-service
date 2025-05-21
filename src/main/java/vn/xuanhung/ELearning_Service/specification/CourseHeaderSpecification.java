package vn.xuanhung.ELearning_Service.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.xuanhung.ELearning_Service.dto.request.CourseHeaderViewRequest;
import vn.xuanhung.ELearning_Service.entity.view.CourseHeaderView;

public class CourseHeaderSpecification {
    public static Specification<CourseHeaderView> likeCourseName(String name)
    {
        return (root, query, criteriaBuilder) ->
                name == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(criteriaBuilder.lower(root.get("courseName")), "%" + name + "%");
    }

    public static Specification<CourseHeaderView> isCourseId(Integer id)
    {
        return (root, query, criteriaBuilder) ->
                id == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("courseId"), id);
    }

    public static Specification<CourseHeaderView> isCategoryId(Integer id)
    {
        return (root, query, criteriaBuilder) ->
                id == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("categoryId"), id);
    }

    public static Specification<CourseHeaderView> isUserId(Integer id)
    {
        return (root, query, criteriaBuilder) ->
                id == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("instructorId"), id);
    }

    public static Specification<CourseHeaderView> isStatus(String status)
    {
        return (root, query, criteriaBuilder) ->
                status == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("courseStatus"), status);
    }

    public static Specification<CourseHeaderView> isLevel(String level)
    {
        return (root, query, criteriaBuilder) ->
                level == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("level"), level);
    }

    public static Specification<CourseHeaderView> getSpecification(CourseHeaderViewRequest req) {
        Specification<CourseHeaderView> spec = Specification.where(likeCourseName(req.getCourseName()))
                .and(isCourseId(req.getCourseId()))
                .and(isUserId(req.getUserId()))
                .and(isCategoryId(req.getCategoryId()))
                .and(isStatus(req.getCourseStatus())
                .and(isLevel(req.getLevel()))
                );
        return spec;

    }
}
