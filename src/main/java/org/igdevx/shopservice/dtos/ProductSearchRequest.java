package org.igdevx.shopservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {

    // Full-text search
    private String q;

    // Filters
    private Set<Long> categoryIds;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private Long currencyId;
    private Boolean fresh;
    private Set<Long> certificationIds;
    private Boolean onlyDeleted;

    // Sorting
    private String sort; // e.g., "price_asc", "price_desc", "date_asc", "date_desc"

    // Pagination
    private Integer page;
    private Integer size;
}

