package org.igdevx.shopservice.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id", nullable = false)
    private Shelf shelf;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_certification_link",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "certification_id")
    )
    @Builder.Default
    private Set<ProductCertification> certifications = new HashSet<>();

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 255)
    private String imageKey;

    @Column(length = 500)
    private String imageThumbnailUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isFresh = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    /**
     * Producer identifier - references a user from the user-service microservice.
     * Temporary field until user-service integration is complete.
     * Once the gateway is implemented, this will be validated against the user-service.
     */
    @Column(name = "producer_id", nullable = false)
    private Long producerId;
}
