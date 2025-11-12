package org.igdevx.shopservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for category data")
public class CategoryResponse {

    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Schema(description = "Category name", example = "Fruits")
    private String name;

    @Schema(description = "URL-friendly slug", example = "fruits")
    private String slug;

    @Schema(description = "Category description", example = "Fresh fruits including apples, oranges, bananas, berries, etc.")
    private String description;

    @Schema(description = "Display order", example = "1")
    private Integer displayOrder;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Soft delete flag")
    private Boolean isDeleted;
}

