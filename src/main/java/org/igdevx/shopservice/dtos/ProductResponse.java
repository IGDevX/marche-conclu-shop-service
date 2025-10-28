package org.igdevx.shopservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    
    private CurrencyResponse currency;
    private UnitResponse unit;
    private ProductCategoryResponse category;
    private Set<ProductCertificationResponse> certifications;
    
    private String imageUrl;
    private String imageKey;
    private String imageThumbnailUrl;
    
    private Boolean isFresh;
    private Boolean isAvailable;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
