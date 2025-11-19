package uz.uzinfocom.product.dto;

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
public class ProductResponseDto {
  private Long id;
  private String name;
  private BigDecimal price;
  private String category;
  private String description;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Boolean isActive;
  private String imageName;
  private String imageUrl;
  private String imageContentType;
}
