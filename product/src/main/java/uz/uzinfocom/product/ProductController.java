package uz.uzinfocom.product;

import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uz.uzinfocom.product.dto.ProductRequestDto;
import uz.uzinfocom.product.dto.ProductResponseDto;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

  private final ProductService productService;

  @PostMapping
  public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto requestDto) {
    log.info("REST request to create Product: {}", requestDto);
    ProductResponseDto response = productService.createProduct(requestDto);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
    log.info("REST request to get Product by ID: {}", id);
    ProductResponseDto response = productService.getProductById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "ASC") String direction) {

    log.info("REST request to get all Products - page: {}, size: {}, sortBy: {}, direction: {}",
        page, size, sortBy, direction);

    Sort.Direction sortDirection = Sort.Direction.fromString(direction);
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

    Page<ProductResponseDto> response = productService.getAllProducts(pageable);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/category/{category}")
  public ResponseEntity<Page<ProductResponseDto>> getProductsByCategory(
      @PathVariable String category,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    log.info("REST request to get Products by category: {}", category);

    Pageable pageable = PageRequest.of(page, size);
    Page<ProductResponseDto> response = productService.getProductsByCategory(category, pageable);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponseDto> updateProduct(
      @PathVariable Long id,
      @RequestBody ProductRequestDto requestDto) {

    log.info("REST request to update Product with ID: {}", id);
    ProductResponseDto response = productService.updateProduct(id, requestDto);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    log.info("REST request to delete Product with ID: {}", id);
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ProductResponseDto> uploadProductImage(
      @PathVariable Long id,
      @RequestParam("file") MultipartFile file) {

    log.info("REST request to upload image for Product ID: {}", id);
    ProductResponseDto response = productService.uploadProductImage(id, file);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}/image")
  public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {
    log.info("REST request to get image for Product ID: {}", id);

    try {
      InputStream imageStream = productService.getProductImage(id);
      byte[] imageBytes = IOUtils.toByteArray(imageStream);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.IMAGE_JPEG); // Yoki dynamic ravishda
      headers.setContentLength(imageBytes.length);

      return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    } catch (Exception e) {
      log.error("Error retrieving image for product ID: {}", id, e);
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}/image")
  public ResponseEntity<Void> deleteProductImage(@PathVariable Long id) {
    log.info("REST request to delete image for Product ID: {}", id);
    productService.deleteProductImage(id);
    return ResponseEntity.noContent().build();
  }
}
