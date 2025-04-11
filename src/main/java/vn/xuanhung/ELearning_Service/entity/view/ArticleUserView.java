package vn.xuanhung.ELearning_Service.entity.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Immutable;

import java.util.Date;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Immutable
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_article_use_view")
public class ArticleUserView {
    @Id
    @Column(name = "article_id")
    Integer id;

    @Column(name = "title")
    String title;

    @Column(name = "content")
    String content;

    @Column(name = "description")
    String description;

    @Column(name = "instructor_id")
    Integer instructorId;

    @Column(name = "published_date")
    Date publishedDate;

    @Column(name = "status")
    String status;

    @Column(name = "image")
    String image;

    @Column(name = "full_name")
    String fullName;

    @Column(name = "updated_at")
    Date updatedAt;
}
