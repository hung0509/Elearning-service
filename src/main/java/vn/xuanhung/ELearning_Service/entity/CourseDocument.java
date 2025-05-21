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
@Table(name = "d_course_document")
public class CourseDocument extends AbstractMappedEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "course_id")
    Integer courseId;

    @Column(name = "document_name")
    String documentName;

    @Column(name = "document_url")
    String documentUrl;

    @Column(name = "file_type")
    String fileType;

    @Column(name = "is_active")
    String isActive;
}
