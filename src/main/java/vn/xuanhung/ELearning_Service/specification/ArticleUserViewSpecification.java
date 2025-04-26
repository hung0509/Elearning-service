package vn.xuanhung.ELearning_Service.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.xuanhung.ELearning_Service.dto.request.ArticleUserViewRequest;
import vn.xuanhung.ELearning_Service.entity.view.ArticleUserView;

import java.util.List;

public class ArticleUserViewSpecification {
    public static Specification<ArticleUserView> isTitle(String title)
    {
        return (root, query, criteriaBuilder) ->
                title == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title + "%");
    }

    public static Specification<ArticleUserView> isStatus(String status)
    {
        return (root, query, criteriaBuilder) ->
                status == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(criteriaBuilder.lower(root.get("isStatus")),  status );
    }

    public static Specification<ArticleUserView> isFullNameIn(List<String> fullNames) {
        return (root, query, criteriaBuilder) -> {
            if (fullNames == null || fullNames.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("fullName").in(fullNames);
        };
    }


    public static Specification<ArticleUserView> isArticleId(Integer articleId)
    {
        return (root, query, criteriaBuilder) ->
                articleId == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("id"), articleId );
    }

    public static Specification<ArticleUserView> isInstructorId(Integer instructorId)
    {
        return (root, query, criteriaBuilder) ->
                instructorId == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("instructorId"), instructorId );
    }

    public static Specification<ArticleUserView> getSpecification(ArticleUserViewRequest req) {
        Specification<ArticleUserView> spec = Specification.where(isTitle(req.getTitle()))
                .and(isStatus(req.getStatus()))
                .and(isFullNameIn(req.getFullNames()))
                .and(isInstructorId(req.getInstructorId()))
                .and(isArticleId(req.getId())
                );
        return spec;

    }
}
