package uz.uzinfocom.product.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDto {
  private String category;
  private String name;
  private String description;
  private BigDecimal price;
}
