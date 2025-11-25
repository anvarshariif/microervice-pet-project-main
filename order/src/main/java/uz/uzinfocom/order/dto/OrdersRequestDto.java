package uz.uzinfocom.order.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrdersRequestDto {
  private Long userId;
  private Long productId;
  private Integer quantity;
  private BigDecimal totalPrice;
}
