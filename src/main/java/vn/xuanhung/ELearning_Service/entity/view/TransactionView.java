package vn.xuanhung.ELearning_Service.entity.view;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Immutable
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_transaction_view")
public class TransactionView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "payment_id")
    String paymentId;

    @Column(name = "course_id")
    Integer courseId;

    @Column(name = "user_id")
    Integer userId;

    @Column(name = "balance")
    BigDecimal balance;

    @Column(name = "payment_method")
    String paymentMethod;

    @Column(name = "payment_date")
    Date paymentDate;

    @Column(name = "status_payment")
    String statusPayment;

    @Column(name = "currency")
    String currency;

    @Column(name = "full_name")
    String fullName;

    @Column(name = "course_name")
    String courseName;

    @Column(name = "created_at")
    String createdAt;
}
