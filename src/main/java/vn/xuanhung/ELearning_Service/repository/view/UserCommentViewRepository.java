package vn.xuanhung.ELearning_Service.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.xuanhung.ELearning_Service.entity.view.UserCommentView;

@Repository
public interface UserCommentViewRepository extends JpaRepository<UserCommentView, Integer>,
        JpaSpecificationExecutor<UserCommentView> {
}
