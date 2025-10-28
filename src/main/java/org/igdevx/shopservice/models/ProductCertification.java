package org.igdevx.shopservice.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_certification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCertification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String label;
}
