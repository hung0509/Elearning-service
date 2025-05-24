package vn.xuanhung.ELearning_Service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_user_quiz_answers")
public class UserQuiz extends AbstractMappedEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "user_id")
    Integer userId;

    @Column(name = "quiz_id")
    Integer quizId;

    @Column(name = "score")
    BigDecimal score;

    @Column(name = "submitted_at")
    Date submittedAt;
}
