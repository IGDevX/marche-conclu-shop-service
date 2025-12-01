package org.igdevx.shopservice.repositories;

import org.igdevx.shopservice.models.ProductCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCertificationRepository extends JpaRepository<ProductCertification, Long> {
    
    @Query("SELECT pc FROM ProductCertification pc WHERE pc.label = :label AND pc.isDeleted = false")
    Optional<ProductCertification> findByLabel(@Param("label") String label);

    @Query("SELECT COUNT(pc) > 0 FROM ProductCertification pc WHERE pc.label = :label AND pc.isDeleted = false")
    boolean existsByLabel(@Param("label") String label);

    @Query("SELECT pc FROM ProductCertification pc WHERE pc.isDeleted = false")
    List<ProductCertification> findAll();

    @Query("SELECT pc FROM ProductCertification pc WHERE pc.id = :id AND pc.isDeleted = false")
    Optional<ProductCertification> findById(@Param("id") Long id);

    @Query("SELECT pc FROM ProductCertification pc WHERE pc.isDeleted = true")
    List<ProductCertification> findAllDeleted();

    @Query("SELECT pc FROM ProductCertification pc WHERE pc.id = :id")
    Optional<ProductCertification> findByIdIncludingDeleted(@Param("id") Long id);

    @Modifying
    @Query("UPDATE ProductCertification pc SET pc.isDeleted = true WHERE pc.id = :id")
    void softDeleteById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE ProductCertification pc SET pc.isDeleted = false WHERE pc.id = :id")
    void restoreById(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM ProductCertification pc WHERE pc.id = :id")
    void hardDeleteById(@Param("id") Long id);
}
