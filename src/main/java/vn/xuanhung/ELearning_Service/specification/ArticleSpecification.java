package vn.xuanhung.ELearning_Service.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.xuanhung.ELearning_Service.dto.request.ArticleRequest;
import vn.xuanhung.ELearning_Service.dto.request.DiscountRequest;
import vn.xuanhung.ELearning_Service.entity.Article;

import java.util.Date;

public class ArticleSpecification {
    public static Specification<Article> isTitle(String title)
    {
        return (root, query, criteriaBuilder) ->
                title == null ? criteriaBuilder.conjunction() :
                        criteriaBuilder.like( criteriaBuilder.lower(root.get("title")), "%" + title + "%");
    }

    public static Specification<Article> isStatus(String status)
    {
        return (root, query, criteriaBuilder) ->
                status == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(root.get("status"),status);
    }

    public static Specification<Article> instructorId(Integer instructorId)
    {
        return (root, query, criteriaBuilder) ->
                instructorId == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("instructorId"), instructorId);
    }

    public static Specification<Article> getSpecification(ArticleRequest req) {
        Specification<Article> spec = Specification.where(isTitle(req.getTitle()))
                .and(isStatus(req.getStatus()))

                .and(instructorId(req.getInstructorId()));
        return spec;

    }
}
