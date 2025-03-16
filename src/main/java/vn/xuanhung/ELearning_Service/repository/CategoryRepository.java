package vn.xuanhung.ELearning_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xuanhung.ELearning_Service.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
