package vn.xuanhung.ELearning_Service.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.xuanhung.ELearning_Service.entity.Course;
import vn.xuanhung.ELearning_Service.entity.view.TransactionView;

@Repository
public interface TransactionViewRepository extends JpaRepository<TransactionView, Integer>,
        JpaSpecificationExecutor<TransactionView> {
}
