package org.igdevx.shopservice.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shelf")
// Unique constraint enforced by partial index in database: uq_shelf_producer_label_not_deleted
// Index applies only where is_deleted = FALSE to allow unlimited soft deletes
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shelf extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String label;

    /**
     * Producer identifier - references a user from the user-service microservice.
     * Temporary field until user-service integration is complete.
     * Once the gateway is implemented, this will be validated against the user-service.
     */
    @Column(name = "producer_id", nullable = false)
    private Long producerId;
}
