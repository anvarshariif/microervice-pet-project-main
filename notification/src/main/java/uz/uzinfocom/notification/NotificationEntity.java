package uz.uzinfocom.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class NotificationEntity {

  private static final String SEQUENCE_NAME = "notifications_seq";
  private static final String GENERATOR_NAME = "notifications_gen";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
  @SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(nullable = false)
  private Long userId;


  @Column(nullable = false)
  private Long orderId;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  public NotificationEntity(Long userId, Long orderId) {
    this.userId = userId;
    this.orderId = orderId;
  }
}
