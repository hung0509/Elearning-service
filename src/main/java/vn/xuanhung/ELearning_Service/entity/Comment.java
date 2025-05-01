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
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_comment")
public class Comment extends AbstractMappedEntity{
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "content")
    String content;

    @Column(name = "user_id")
    Integer userId;

    @Column(name = "lesson_id")
    Integer lessonId;

    @Column(name = "article_id")
    Integer articleId;

//    @Column(name = "parent_comment_id")
//    Integer parentCommentId;

    @Column(name = "is_active")
    String isActive;
}
