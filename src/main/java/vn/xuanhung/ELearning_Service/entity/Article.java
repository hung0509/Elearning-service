package vn.xuanhung.ELearning_Service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.xuanhung.ELearning_Service.audit.listener.AuditEntityListener;

import java.util.Date;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_article")
@EntityListeners(AuditEntityListener.class)
public class Article extends AbstractMappedEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    Integer id;

    @Column(name = "title")
    String title;

    @Column(name = "content")
    String content;

    @Column(name = "instructor_id")
    Integer instructorId;

    @Column(name = "published_date")
    Date publishedDate;

    @Column(name = "status")
    String status;

    @Column(name = "image")
    String image;

    @Column(name = "description")
    String description;
}
