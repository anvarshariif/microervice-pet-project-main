package uz.uzinfocom.notification.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrdersDto implements Serializable {

  private Long id;
  private Long userId;
  private Long productId;
  private Integer quantity;
  private BigDecimal totalPrice;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
