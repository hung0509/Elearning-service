package vn.xuanhung.ELearning_Service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Entity
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_certificate")
public class Certificate extends AbstractMappedEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    Integer id;

    @Column(name = "certificate_name")
    String certificateName;

    @Column(name = "description")
    String description;

    @Column(name = "validity_period")
    BigDecimal validityPeriod;

    @Column(name = "certificate_level")
    String certificateLevel;
}
