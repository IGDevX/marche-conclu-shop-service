package org.igdevx.shopservice.models;

import jakarta.persistence.*;
import lombok.*;

/**
 * Category entity representing standardized product categories.
 * Unlike Shelves (which are producer-specific), Categories are global and shared across all producers.
 * This allows restaurant owners to filter products consistently regardless of the producer.
 */
@Entity
@Table(name = "category")
// Unique constraints enforced by partial indexes in database:
// - uq_category_name_not_deleted (name WHERE is_deleted = FALSE)
// - uq_category_slug_not_deleted (slug WHERE is_deleted = FALSE)
// Indexes apply only to active categories to allow unlimited soft deletes
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "display_order")
    private Integer displayOrder;
}

