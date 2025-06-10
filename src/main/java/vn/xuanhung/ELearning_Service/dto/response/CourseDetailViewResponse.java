package vn.xuanhung.ELearning_Service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseDetailViewResponse {
    Integer id;
    String courseName;
    String description;
    String avatar;
    String trailer;
    BigDecimal priceEntered;
    BigDecimal priceAfterReduce;
    BigDecimal courseDuration;
    BigDecimal quantity;
    String isActive;
    String level;
    Date createdAt;
    Boolean isRegister;
    Integer completedLesson;
    Integer totalLesson;
    BigDecimal completionPercentage;
    CertificateResponse certificate;
    DiscountResponse discount;
    CategoryResponse category;
    List<LessonResponse> lessons;
    List<CourseDocumentResponse> documents;
    List<QuizHeaderResponse> quizs;
}
