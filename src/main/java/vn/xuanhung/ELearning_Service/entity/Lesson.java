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
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_lesson")
public class Lesson extends AbstractMappedEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    Integer id;

    @Column(name = "course_id")
    Integer courseId;

    @Column(name = "lesson_name")
    String lessonName;

    @Column(name = "url_lesson")
    String urlLesson;

    @Column(name = "description")
    String description;

    @Column(name = "lesson_time")
    BigDecimal lessonTime;

    @Column(name = "is_active", columnDefinition = "Y")
    String isActive;

    @Column(name = "playlist_id")
    String playListId;
}
