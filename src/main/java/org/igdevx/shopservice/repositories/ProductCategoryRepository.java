package org.igdevx.shopservice.repositories;

import org.igdevx.shopservice.models.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    
    // Find by label (only non-deleted)
    @Query("SELECT pc FROM ProductCategory pc WHERE pc.label = :label AND pc.isDeleted = false")
    Optional<ProductCategory> findByLabel(@Param("label") String label);

    // Find by slug (only non-deleted)
    @Query("SELECT pc FROM ProductCategory pc WHERE pc.slug = :slug AND pc.isDeleted = false")
    Optional<ProductCategory> findBySlug(@Param("slug") String slug);

    // Check if label exists (only non-deleted)
    @Query("SELECT COUNT(pc) > 0 FROM ProductCategory pc WHERE pc.label = :label AND pc.isDeleted = false")
    boolean existsByLabel(@Param("label") String label);

    // Check if slug exists (only non-deleted)
    @Query("SELECT COUNT(pc) > 0 FROM ProductCategory pc WHERE pc.slug = :slug AND pc.isDeleted = false")
    boolean existsBySlug(@Param("slug") String slug);

    // Find all non-deleted categories
    @Query("SELECT pc FROM ProductCategory pc WHERE pc.isDeleted = false")
    List<ProductCategory> findAll();

    // Find by ID (only non-deleted)
    @Query("SELECT pc FROM ProductCategory pc WHERE pc.id = :id AND pc.isDeleted = false")
    Optional<ProductCategory> findById(@Param("id") Long id);

    // Find all deleted categories
    @Query("SELECT pc FROM ProductCategory pc WHERE pc.isDeleted = true")
    List<ProductCategory> findAllDeleted();

    // Find category by ID including deleted ones
    @Query("SELECT pc FROM ProductCategory pc WHERE pc.id = :id")
    Optional<ProductCategory> findByIdIncludingDeleted(@Param("id") Long id);

    // Soft delete by ID
    @Modifying
    @Query("UPDATE ProductCategory pc SET pc.isDeleted = true WHERE pc.id = :id")
    void softDeleteById(@Param("id") Long id);

    // Restore by ID
    @Modifying
    @Query("UPDATE ProductCategory pc SET pc.isDeleted = false WHERE pc.id = :id")
    void restoreById(@Param("id") Long id);

    // Hard delete by ID
    @Modifying
    @Query("DELETE FROM ProductCategory pc WHERE pc.id = :id")
    void hardDeleteById(@Param("id") Long id);
}
