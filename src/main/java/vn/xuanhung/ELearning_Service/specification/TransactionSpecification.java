package vn.xuanhung.ELearning_Service.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.xuanhung.ELearning_Service.dto.request.TransactionQueryRequest;
import vn.xuanhung.ELearning_Service.entity.view.TransactionView;

import java.math.BigDecimal;

public class TransactionSpecification {
    public static Specification<TransactionView> isTransactionId(Integer id)
    {
        return (root, query, criteriaBuilder) ->
                id == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("id"),  id );
    }

    public static Specification<TransactionView> isPaymentId(String paymentId)
    {
        return (root, query, criteriaBuilder) ->
                paymentId == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("paymentId"),  paymentId );
    }

    public static Specification<TransactionView> IsPaymentStatus(String status)
    {
        return (root, query, criteriaBuilder) ->
                status == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("statusPayment"),  status );
    }

    public static Specification<TransactionView> isBalance(BigDecimal fromBalance, BigDecimal toBalance)
    {
        if(fromBalance != null && toBalance != null){
            return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("balance"), fromBalance, toBalance);
        }

        if(fromBalance == null && toBalance == null){
            return (root, query, criteriaBuilder) ->  criteriaBuilder.conjunction();
        }
        if(fromBalance == null){
            return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("balance"), fromBalance);
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("balance"), toBalance);
    }


    public static Specification<TransactionView> getSpecification(TransactionQueryRequest req) {
        return Specification.where(IsPaymentStatus(req.getStatusPayment()))
                .and(isBalance(req.getFromBalance(), req.getToBalance()))
                .and(isTransactionId(req.getId()))
                .and(isPaymentId(req.getPaymentId()));
    }

}
