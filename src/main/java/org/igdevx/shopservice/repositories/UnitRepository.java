package org.igdevx.shopservice.repositories;

import org.igdevx.shopservice.models.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    
    // Find by code (only non-deleted)
    @Query("SELECT u FROM Unit u WHERE u.code = :code AND u.isDeleted = false")
    Optional<Unit> findByCode(@Param("code") String code);

    // Check if code exists (only non-deleted)
    @Query("SELECT COUNT(u) > 0 FROM Unit u WHERE u.code = :code AND u.isDeleted = false")
    boolean existsByCode(@Param("code") String code);

    // Find all non-deleted units
    @Query("SELECT u FROM Unit u WHERE u.isDeleted = false")
    List<Unit> findAll();

    // Find by ID (only non-deleted)
    @Query("SELECT u FROM Unit u WHERE u.id = :id AND u.isDeleted = false")
    Optional<Unit> findById(@Param("id") Long id);

    // Find all deleted units
    @Query("SELECT u FROM Unit u WHERE u.isDeleted = true")
    List<Unit> findAllDeleted();

    // Find unit by ID including deleted ones
    @Query("SELECT u FROM Unit u WHERE u.id = :id")
    Optional<Unit> findByIdIncludingDeleted(@Param("id") Long id);

    // Soft delete by ID
    @Modifying
    @Query("UPDATE Unit u SET u.isDeleted = true WHERE u.id = :id")
    void softDeleteById(@Param("id") Long id);

    // Restore by ID
    @Modifying
    @Query("UPDATE Unit u SET u.isDeleted = false WHERE u.id = :id")
    void restoreById(@Param("id") Long id);

    // Hard delete by ID
    @Modifying
    @Query("DELETE FROM Unit u WHERE u.id = :id")
    void hardDeleteById(@Param("id") Long id);
}
