package uz.uzinfocom.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

  Page<ProductEntity> findByCategory(String category, Pageable pageable);

  @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true")
  Page<ProductEntity> findAllActiveProducts(Pageable pageable);

  // Category va active status bo'yicha
  @Query("SELECT p FROM ProductEntity p WHERE p.category = :category AND p.isActive = true")
  Page<ProductEntity> findActiveByCategoryProducts(@Param("category") String category, Pageable pageable);

  // Price range bo'yicha
  @Query("SELECT p FROM ProductEntity p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isActive = true")
  List<ProductEntity> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
      @Param("maxPrice") BigDecimal maxPrice);

  // Low stock products
  @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true")
  List<ProductEntity> findLowStockProducts(@Param("threshold") Integer threshold);

  // Search by name (LIKE query)
  @Query("SELECT p FROM ProductEntity p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.isActive = true")
  Page<ProductEntity> searchByName(@Param("name") String name, Pageable pageable);

  // Count by category
  @Query("SELECT COUNT(p) FROM ProductEntity p WHERE p.category = :category AND p.isActive = true")
  Long countByCategory(@Param("category") String category);

  // Find by ID and active
  @Query("SELECT p FROM ProductEntity p WHERE p.id = :id AND p.isActive = true")
  Optional<ProductEntity> findByIdAndActive(@Param("id") Long id);
}
