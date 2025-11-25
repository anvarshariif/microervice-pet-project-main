package uz.uzinfocom.order;

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
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class OrdersEntity {
  private static final String SEQUENCE_NAME = "orders_seq";
  private static final String GENERATOR_NAME = "orders_gen";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
  @SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private Long productId;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private BigDecimal totalPrice;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedAt;

}

