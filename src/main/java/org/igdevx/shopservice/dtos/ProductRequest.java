package org.igdevx.shopservice.dtos;

import jakarta.validation.constraints.*;
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
public class ProductRequest {

    @NotBlank(message = "Product title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Currency is required")
    private Long currencyId;

    @NotNull(message = "Unit is required")
    private Long unitId;

    @NotNull(message = "Shelf is required")
    private Long shelfId;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private Set<Long> certificationIds;

    private Boolean isFresh;

    private java.util.UUID mainImageId;

    private String mainImageUrl;

    @NotNull(message = "Producer ID is required")
    private Long producerId;
}
