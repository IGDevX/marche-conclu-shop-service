package org.igdevx.shopservice.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shelf",
       uniqueConstraints = {
           @UniqueConstraint(name = "uq_shelf_producer_label", 
                           columnNames = {"producer_id", "label"})
       })
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
