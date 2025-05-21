package vn.xuanhung.ELearning_Service.entity;

import ch.qos.logback.core.model.Model;
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
@Table(name = "d_answer")
@Builder
public class Answer extends AbstractMappedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    Integer id;

    @Column(name = "question_id")
    Integer questionId;

    @Column(name = "answer_text")
    String answerText;

    @Column(name = "is_correct")
    String isCorrect;
}
