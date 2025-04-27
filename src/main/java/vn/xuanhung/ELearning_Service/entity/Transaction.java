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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_transaction")
public class Transaction extends  AbstractMappedEntity{
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

    @Column(name = "description")
    String description;

    @Column(name = "payment_method")
    String paymentMethod;

    @Column(name = "payment_date")
    Date paymentDate;

    @Column(name = "status_payment")
    String statusPayment;

    @Column(name = "currency")
    String currency;
}
