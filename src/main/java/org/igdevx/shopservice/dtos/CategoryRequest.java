package org.igdevx.shopservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating or updating a category")
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    @Schema(description = "Category name", example = "Fruits")
    private String name;

    @NotBlank(message = "Category slug is required")
    @Size(min = 2, max = 100, message = "Category slug must be between 2 and 100 characters")
    @Schema(description = "URL-friendly slug", example = "fruits")
    private String slug;

    @Schema(description = "Category description with examples", example = "Fresh fruits including apples, oranges, bananas, berries, etc.")
    private String description;

    @Schema(description = "Display order for sorting", example = "1")
    private Integer displayOrder;
}
