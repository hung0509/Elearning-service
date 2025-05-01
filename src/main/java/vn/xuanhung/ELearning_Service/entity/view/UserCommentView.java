package vn.xuanhung.ELearning_Service.entity.view;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Immutable;
import vn.xuanhung.ELearning_Service.entity.AbstractMappedEntity;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Immutable
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_user_comment_view")
public class UserCommentView extends AbstractMappedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    Integer id;

    @Column(name = "content")
    String content;

    @Column(name = "lesson_id")
    Integer lessonId;

    @Column(name = "parent_comment_id")
    Integer parentCommentId;

    @Column(name = "is_active")
    String isActive;

    @Column(name = "user_id")
    Integer userId;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "avatar")
    String avatar;
}
