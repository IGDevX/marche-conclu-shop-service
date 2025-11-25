package org.igdevx.shopservice.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_certification")
// Unique constraint enforced by partial index in database: uq_certification_label_not_deleted
// Index applies only where is_deleted = FALSE to allow unlimited soft deletes
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCertification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String label;
}
