package org.igdevx.shopservice.repositories;

import org.igdevx.shopservice.models.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {
    
    // Find by producer and label (only non-deleted)
    @Query("SELECT s FROM Shelf s WHERE s.producerId = :producerId AND s.label = :label AND s.isDeleted = false")
    Optional<Shelf> findByProducerIdAndLabel(@Param("producerId") Long producerId, @Param("label") String label);

    // Check if label exists for a producer (only non-deleted)
    @Query("SELECT COUNT(s) > 0 FROM Shelf s WHERE s.producerId = :producerId AND s.label = :label AND s.isDeleted = false")
    boolean existsByProducerIdAndLabel(@Param("producerId") Long producerId, @Param("label") String label);

    // Find all non-deleted shelves
    @Query("SELECT s FROM Shelf s WHERE s.isDeleted = false")
    List<Shelf> findAll();

    // Find by ID (only non-deleted)
    @Query("SELECT s FROM Shelf s WHERE s.id = :id AND s.isDeleted = false")
    Optional<Shelf> findById(@Param("id") Long id);

    // Find all deleted shelves
    @Query("SELECT s FROM Shelf s WHERE s.isDeleted = true")
    List<Shelf> findAllDeleted();

    // Find shelf by ID including deleted ones
    @Query("SELECT s FROM Shelf s WHERE s.id = :id")
    Optional<Shelf> findByIdIncludingDeleted(@Param("id") Long id);

    // Find by producer ID (non-deleted only)
    @Query("SELECT s FROM Shelf s WHERE s.producerId = :producerId AND s.isDeleted = false")
    List<Shelf> findByProducerId(@Param("producerId") Long producerId);

    // Soft delete by ID
    @Modifying
    @Query("UPDATE Shelf s SET s.isDeleted = true WHERE s.id = :id")
    void softDeleteById(@Param("id") Long id);

    // Restore by ID
    @Modifying
    @Query("UPDATE Shelf s SET s.isDeleted = false WHERE s.id = :id")
    void restoreById(@Param("id") Long id);

    // Hard delete by ID
    @Modifying
    @Query("DELETE FROM Shelf s WHERE s.id = :id")
    void hardDeleteById(@Param("id") Long id);
}
