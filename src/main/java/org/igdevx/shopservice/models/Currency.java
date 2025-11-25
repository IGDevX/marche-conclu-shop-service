package org.igdevx.shopservice.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "currency")
// Unique constraint enforced by partial index in database: uq_currency_code_not_deleted
// Index applies only where is_deleted = FALSE to allow unlimited soft deletes
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Currency extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 3)
    private String code;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(name = "usd_exchange_rate", nullable = false, precision = 20, scale = 10)
    private BigDecimal usdExchangeRate;
}
