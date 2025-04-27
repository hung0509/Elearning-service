package vn.xuanhung.ELearning_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xuanhung.ELearning_Service.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Transaction findByPaymentId(String paymentId);
}
