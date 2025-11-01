package org.igdevx.shopservice.repositories;

import org.igdevx.shopservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find all non-deleted products
    @Query("SELECT p FROM Product p WHERE p.isDeleted = false")
    List<Product> findAll();

    // Find by ID (non-deleted only)
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.currency LEFT JOIN FETCH p.unit " +
           "LEFT JOIN FETCH p.shelf LEFT JOIN FETCH p.certifications " +
           "WHERE p.id = :id AND p.isDeleted = false")
    Optional<Product> findById(@Param("id") Long id);

    // Find all available products
    @Query("SELECT p FROM Product p WHERE p.isDeleted = false AND p.isAvailable = true")
    List<Product> findAllAvailable();

    // Find all fresh products
    @Query("SELECT p FROM Product p WHERE p.isDeleted = false AND p.isFresh = true")
    List<Product> findAllFresh();

    // Find by shelf
    @Query("SELECT p FROM Product p WHERE p.shelf.id = :shelfId AND p.isDeleted = false")
    List<Product> findByShelfId(@Param("shelfId") Long shelfId);

    // Search by title (case-insensitive)
    @Query("SELECT p FROM Product p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND p.isDeleted = false")
    List<Product> searchByTitle(@Param("searchTerm") String searchTerm);

    // Find by ID including deleted
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.currency LEFT JOIN FETCH p.unit " +
           "LEFT JOIN FETCH p.shelf LEFT JOIN FETCH p.certifications " +
           "WHERE p.id = :id")
    Optional<Product> findByIdIncludingDeleted(@Param("id") Long id);

    // Find all deleted products
    @Query("SELECT p FROM Product p WHERE p.isDeleted = true")
    List<Product> findAllDeleted();

    // Find by producer ID (non-deleted only)
    @Query("SELECT p FROM Product p WHERE p.producerId = :producerId AND p.isDeleted = false")
    List<Product> findByProducerId(@Param("producerId") Long producerId);

    // Find available products by producer ID
    @Query("SELECT p FROM Product p WHERE p.producerId = :producerId AND p.isDeleted = false AND p.isAvailable = true")
    List<Product> findByProducerIdAndIsAvailable(@Param("producerId") Long producerId);

    // Find products by producer ID and shelf ID
    @Query("SELECT p FROM Product p WHERE p.producerId = :producerId AND p.shelf.id = :shelfId AND p.isDeleted = false")
    List<Product> findByProducerIdAndShelfId(@Param("producerId") Long producerId, @Param("shelfId") Long shelfId);

    // Soft delete by ID
    @Modifying
    @Query("UPDATE Product p SET p.isDeleted = true WHERE p.id = :id")
    void softDeleteById(@Param("id") Long id);

    // Restore by ID
    @Modifying
    @Query("UPDATE Product p SET p.isDeleted = false WHERE p.id = :id")
    void restoreById(@Param("id") Long id);

    // Hard delete by ID
    @Modifying
    @Query("DELETE FROM Product p WHERE p.id = :id")
    void hardDeleteById(@Param("id") Long id);
}
