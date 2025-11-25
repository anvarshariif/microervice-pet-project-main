package uz.uzinfocom.product;

import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.uzinfocom.product.dto.ProductRequestDto;
import uz.uzinfocom.product.dto.ProductResponseDto;
import uz.uzinfocom.product.minio.MinioService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

  private final ProductRepository productRepository;
  private final MinioService minioService;

  private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
  private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
      "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
  );

  @Override
  @Transactional
  public ProductResponseDto createProduct(ProductRequestDto requestDto) {
    log.info("Creating new product: {}", requestDto.getName());

    ProductEntity product = new ProductEntity();
    product.setName(requestDto.getName());
    product.setPrice(requestDto.getPrice());
    product.setCategory(requestDto.getCategory());
    product.setDescription(requestDto.getDescription());
    product.setIsActive(true);

    ProductEntity savedProduct = productRepository.save(product);
    log.info("Product created successfully with ID: {}", savedProduct.getId());

    return mapToResponseDto(savedProduct);
  }

  @Cacheable(value = "products", key = "#id")
  @Override
  public ProductResponseDto getProductById(Long id) {
    log.info("Fetching product with ID: {}", id);
    ProductEntity product = productRepository.findByIdAndActive(id)
        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

    return mapToResponseDto(product);
  }

  @Override
  public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
    log.info("Fetching all active products with pagination: {}", pageable);

    Page<ProductEntity> products = productRepository.findAllActiveProducts(pageable);

    return products.map(this::mapToResponseDto);
  }

  @Override
  public Page<ProductResponseDto> getProductsByCategory(String category, Pageable pageable) {
    log.info("Fetching products by category: {} with pagination: {}", category, pageable);

    Page<ProductEntity> products = productRepository.findActiveByCategoryProducts(category, pageable);

    return products.map(this::mapToResponseDto);
  }

  @CacheEvict(value = "products", key = "#id")
  @Override
  @Transactional
  public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
    log.info("Updating product with ID: {}", id);

    ProductEntity product = productRepository.findByIdAndActive(id)
        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

    product.setName(requestDto.getName());
    product.setPrice(requestDto.getPrice());
    product.setCategory(requestDto.getCategory());
    product.setDescription(requestDto.getDescription());

    ProductEntity updatedProduct = productRepository.save(product);
    log.info("Product updated successfully with ID: {}", updatedProduct.getId());

    return mapToResponseDto(updatedProduct);
  }

  @CacheEvict(value = "products", key = "#id")
  @Override
  @Transactional
  public void deleteProduct(Long id) {
    log.info("Deleting product with ID: {}", id);

    ProductEntity product = productRepository.findByIdAndActive(id)
        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

    // Soft delete
    product.setIsActive(false);
    productRepository.save(product);

    log.info("Product deleted successfully with ID: {}", id);
  }

  @CacheEvict(value = "products", key = "#productId")
  @Override
  @Transactional
  public ProductResponseDto uploadProductImage(Long productId, MultipartFile file) {
    log.info("Uploading image for product ID: {}", productId);

    // Product mavjudligini tekshirish
    ProductEntity productEntity = productRepository.findByIdAndActive(productId)
        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

    // Fayl validatsiyasi
    validateImageFile(file);

    // Eski rasmni o'chirish (agar mavjud bo'lsa)
    if (productEntity.getImageName() != null) {
      try {
        minioService.deleteFile(productEntity.getImageName());
      } catch (Exception e) {
        log.warn("Could not delete old image: {}", productEntity.getImageName(), e);
      }
    }

    // Yangi fayl nomini generatsiya qilish
    String originalFilename = file.getOriginalFilename();
    String fileExtension = originalFilename != null ?
        originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
    String objectName = "products/" + productId + "/" + UUID.randomUUID() + fileExtension;

    // MinIO'ga yuklash
    minioService.uploadFile(file, objectName);

    // Product'ni yangilash
    productEntity.setImageName(objectName);
    productEntity.setImageUrl(minioService.getFileUrl(objectName));
    productEntity.setImageContentType(file.getContentType());

    ProductEntity updatedProduct = productRepository.save(productEntity);
    log.info("Image uploaded successfully for product ID: {}", productId);

    return mapToResponseDto(updatedProduct);
  }

  @Override
  public InputStream getProductImage(Long productId) {
    log.info("Getting image for product ID: {}", productId);

    ProductEntity productEntity = productRepository.findByIdAndActive(productId)
        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

    if (productEntity.getImageName() == null) {
      throw new RuntimeException("Product has no image");
    }

    return minioService.getFile(productEntity.getImageName());
  }

  @CacheEvict(value = "products", key = "#productId")
  @Override
  @Transactional
  public void deleteProductImage(Long productId) {
    log.info("Deleting image for product ID: {}", productId);

    ProductEntity productEntity = productRepository.findByIdAndActive(productId)
        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

    if (productEntity.getImageName() == null) {
      throw new RuntimeException("Product has no image to delete");
    }

    // MinIO'dan o'chirish
    minioService.deleteFile(productEntity.getImageName());

    // Product'dan o'chirish
    productEntity.setImageName(null);
    productEntity.setImageUrl(null);
    productEntity.setImageContentType(null);

    productRepository.save(productEntity);
    log.info("Image deleted successfully for product ID: {}", productId);
  }

  /**
   * Rasm faylini validatsiya qilish
   */
  private void validateImageFile(MultipartFile file) {
    // Fayl bo'sh emasligini tekshirish
    if (file.isEmpty()) {
      throw new RuntimeException("File is empty");
    }

    // Fayl hajmini tekshirish
    if (file.getSize() > MAX_FILE_SIZE) {
      throw new RuntimeException("File size exceeds maximum limit of 5MB");
    }

    // Fayl turini tekshirish
    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
      throw new RuntimeException("Invalid file type. Only JPEG, PNG, GIF, and WebP images are allowed");
    }
  }

  // Helper method
  private ProductResponseDto mapToResponseDto(ProductEntity product) {
    ProductResponseDto dto = new ProductResponseDto();
    dto.setId(product.getId());
    dto.setName(product.getName());
    dto.setPrice(product.getPrice());
    dto.setCategory(product.getCategory());
    dto.setDescription(product.getDescription());
    dto.setImageName(product.getImageName());
    dto.setImageUrl(product.getImageUrl());
    dto.setImageContentType(product.getImageContentType());
    dto.setCreatedAt(product.getCreatedAt());
    dto.setUpdatedAt(product.getUpdatedAt());
    dto.setIsActive(product.getIsActive());
    return dto;
  }
}
