package org.igdevx.shopservice.repositories;

import org.igdevx.shopservice.models.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    // Find all non-deleted currencies
    @Query("SELECT c FROM Currency c WHERE c.isDeleted = false")
    List<Currency> findAll();

    // Find by ID (non-deleted only)
    @Query("SELECT c FROM Currency c WHERE c.id = :id AND c.isDeleted = false")
    Optional<Currency> findById(@Param("id") Long id);

    // Find by code (non-deleted only)
    @Query("SELECT c FROM Currency c WHERE c.code = :code AND c.isDeleted = false")
    Optional<Currency> findByCode(@Param("code") String code);

    // Check if code exists (non-deleted only)
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Currency c WHERE c.code = :code AND c.isDeleted = false")
    boolean existsByCode(@Param("code") String code);

    // Find by ID including deleted
    @Query("SELECT c FROM Currency c WHERE c.id = :id")
    Optional<Currency> findByIdIncludingDeleted(@Param("id") Long id);

    // Find all deleted currencies
    @Query("SELECT c FROM Currency c WHERE c.isDeleted = true")
    List<Currency> findAllDeleted();

    // Soft delete by ID
    @Modifying
    @Query("UPDATE Currency c SET c.isDeleted = true WHERE c.id = :id")
    void softDeleteById(@Param("id") Long id);

    // Restore by ID
    @Modifying
    @Query("UPDATE Currency c SET c.isDeleted = false WHERE c.id = :id")
    void restoreById(@Param("id") Long id);

    // Hard delete by ID
    @Modifying
    @Query("DELETE FROM Currency c WHERE c.id = :id")
    void hardDeleteById(@Param("id") Long id);
}
