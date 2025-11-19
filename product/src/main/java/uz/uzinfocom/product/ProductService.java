package uz.uzinfocom.product;

import java.io.InputStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.web.multipart.MultipartFile;
import uz.uzinfocom.product.dto.ProductRequestDto;
import uz.uzinfocom.product.dto.ProductResponseDto;

public interface ProductService {

  ProductResponseDto createProduct(ProductRequestDto requestDto);

  ProductResponseDto getProductById(Long id);

  Page<ProductResponseDto> getAllProducts(Pageable pageable);

  Page<ProductResponseDto> getProductsByCategory(String category, Pageable pageable);

  ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto);

  void deleteProduct(Long id);

  ProductResponseDto uploadProductImage(Long productId, MultipartFile file);

  InputStream getProductImage(Long productId);

  void deleteProductImage(Long productId);
}
