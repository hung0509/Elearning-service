package vn.xuanhung.ELearning_Service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.joda.time.DateTime;

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

    DateTime expireDate;
}
