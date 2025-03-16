package vn.xuanhung.ELearning_Service.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.xuanhung.ELearning_Service.dto.request.DiscountRequest;
import vn.xuanhung.ELearning_Service.entity.Discount;

import java.math.BigDecimal;
import java.util.Date;

public class DiscountSpecification {
    public static Specification<Discount> isActive(String isActive)
    {
        return (root, query, criteriaBuilder) ->
                isActive == null ? criteriaBuilder.conjunction() : criteriaBuilder.like(root.get("isActive"), isActive);
    }

    public static Specification<Discount> isCode(String discountCode)
    {
        return (root, query, criteriaBuilder) ->
                discountCode == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(criteriaBuilder.lower(root.get("discountCode")), "%" + discountCode + "%");
    }

    public static Specification<Discount> expiredDate(Date expireDate)
    {
        return (root, query, criteriaBuilder) ->
                expireDate == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.lessThanOrEqualTo(root.get("expiredDate"), expireDate);
    }

    public static Specification<Discount> getSpecification(DiscountRequest req) {
        Specification<Discount> spec = Specification.where(isActive(req.getIsActive()))
                .and(isCode(req.getDiscountCode()))
                .and(expiredDate(req.getExpiredDate()));
        return spec;

    }
}
