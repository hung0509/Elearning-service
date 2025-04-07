package vn.xuanhung.ELearning_Service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_course")
public class Course extends AbstractMappedEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    Integer id;

    @Column(name = "instructor_id") //user_id
    Integer instructorId;

    @Column(name = "category_id")
    Integer categoryId;

    @Column(name = "course_name")
    String courseName;

    @Column(name = "description")
    String description;

    @Column(name = "avatar")
    String avatar;

    @Column(name = "trailer")
    String trailer;

    @Column(name = "price_entered")
    BigDecimal priceEntered;

    @Column(name = "price_after_reduce")
    BigDecimal priceAfterReduce;

    @Column(name = "course_duration")
    BigDecimal courseDuration;

    @Column(name = "quatity")
    BigDecimal quantity;

    @Column(name = "discount_id")
    Integer discountId;

    @Column(name = "is_active")
    String isActive;

    @Column(name = "certificate_id")
    Integer certificateId;

    @Column(name = "level")
    String level;
}
