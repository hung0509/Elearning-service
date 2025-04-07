package vn.xuanhung.ELearning_Service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.joda.time.DateTime;

import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_invalidated_token")
public class InvalidatedToken {
    @Id
    String id;

    @Column(name = "expire_date")
    Date expireDate;
}
