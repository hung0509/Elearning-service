package vn.xuanhung.ELearning_Service.entity.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Immutable
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_course_user_view")
public class CourseRegisterView {
    @Id
    @Column(name = "course_id")
    Integer id;

    @Column(name = "course_name")
    String courseName;

    @Column(name = "description")
    String description;

    @Column(name = "avatar")
    String avatar;

    @Column(name = "price_entered")
    BigDecimal priceEntered;

    @Column(name = "price_after_reduce")
    BigDecimal priceAfterReduce;

    @Column(name = "course_duration")
    BigDecimal courseDuration;

    @Column(name = "quantity")
    BigDecimal quantity;

    @Column(name = "level")
    String level;

    @Column(name = "user_id")
    Integer userId;
}
