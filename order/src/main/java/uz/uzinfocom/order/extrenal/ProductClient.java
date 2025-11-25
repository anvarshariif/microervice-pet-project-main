package uz.uzinfocom.order.extrenal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uz.uzinfocom.order.extrenal.dto.ProductResponseDto;

@FeignClient("product")
public interface ProductClient {

  @GetMapping("/api/products/{id}")
  ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id);

}
