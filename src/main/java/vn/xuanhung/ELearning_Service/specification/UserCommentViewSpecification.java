package vn.xuanhung.ELearning_Service.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.xuanhung.ELearning_Service.dto.request.CommentRequest;
import vn.xuanhung.ELearning_Service.entity.view.UserCommentView;

public class UserCommentViewSpecification {
    public static Specification<UserCommentView> isLessonId(Integer lessonId)
    {
        return (root, query, criteriaBuilder) ->
                lessonId == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("lessonId"), lessonId );
    }

    public static Specification<UserCommentView> getSpecification(CommentRequest req) {
        Specification<UserCommentView> spec = Specification.where(isLessonId(req.getLessonId()));
        return spec;
    }
}
