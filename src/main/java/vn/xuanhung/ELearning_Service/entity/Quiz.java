package vn.xuanhung.ELearning_Service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_quiz")
@Builder
public class Quiz extends AbstractMappedEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    Integer id;

    @Column(name = "course_id")
    Integer courseId;

    @Column(name = "title")
    String title;

    @Column(name = "description")
    String description;

    @Column(name ="time_limit")
    Integer timeLimit;

    @Column(name = "is_active")
    String isActive;
}
