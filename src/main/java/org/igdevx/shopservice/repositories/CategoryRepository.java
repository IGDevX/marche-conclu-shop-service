package org.igdevx.shopservice.repositories;

import org.igdevx.shopservice.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find by name (only non-deleted)
    @Query("SELECT c FROM Category c WHERE c.name = :name AND c.isDeleted = false")
    Optional<Category> findByName(@Param("name") String name);

    // Find by slug (only non-deleted)
    @Query("SELECT c FROM Category c WHERE c.slug = :slug AND c.isDeleted = false")
    Optional<Category> findBySlug(@Param("slug") String slug);

    // Check if name exists (only non-deleted)
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.name = :name AND c.isDeleted = false")
    boolean existsByName(@Param("name") String name);

    // Check if slug exists (only non-deleted)
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.slug = :slug AND c.isDeleted = false")
    boolean existsBySlug(@Param("slug") String slug);

    // Find all non-deleted categories ordered by display order
    @Query("SELECT c FROM Category c WHERE c.isDeleted = false ORDER BY c.displayOrder ASC, c.name ASC")
    List<Category> findAll();

    // Find by ID (only non-deleted)
    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.isDeleted = false")
    Optional<Category> findById(@Param("id") Long id);

    // Find all deleted categories
    @Query("SELECT c FROM Category c WHERE c.isDeleted = true")
    List<Category> findAllDeleted();

    // Find category by ID including deleted ones
    @Query("SELECT c FROM Category c WHERE c.id = :id")
    Optional<Category> findByIdIncludingDeleted(@Param("id") Long id);

    // Soft delete by ID
    @Modifying
    @Query("UPDATE Category c SET c.isDeleted = true WHERE c.id = :id")
    void softDeleteById(@Param("id") Long id);

    // Restore by ID
    @Modifying
    @Query("UPDATE Category c SET c.isDeleted = false WHERE c.id = :id")
    void restoreById(@Param("id") Long id);

    // Hard delete by ID
    @Modifying
    @Query("DELETE FROM Category c WHERE c.id = :id")
    void hardDeleteById(@Param("id") Long id);
}

