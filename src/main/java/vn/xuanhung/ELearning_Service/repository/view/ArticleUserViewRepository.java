package vn.xuanhung.ELearning_Service.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.xuanhung.ELearning_Service.entity.view.ArticleUserView;

public interface ArticleUserViewRepository extends JpaRepository<ArticleUserView, Integer>,
        JpaSpecificationExecutor<ArticleUserView> {
}
