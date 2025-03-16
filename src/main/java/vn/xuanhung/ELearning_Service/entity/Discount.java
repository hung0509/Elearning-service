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
@Table(name = "d_discount")
public class Discount extends AbstractMappedEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_id")
    Integer id;

    @Column(name = "discount_code")
    String discountCode;

    @Column(name = "discount_description")
    String discountDescription;

    @Column(name = "discount_rate")
    BigDecimal discountRate;

    @Column(name = "is_active")
    String isActive;

    @Column(name = "expired_date")
    Date expiredDate;
}
