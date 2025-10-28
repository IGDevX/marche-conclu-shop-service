package org.igdevx.shopservice.mappers;

import org.igdevx.shopservice.dtos.ProductCategoryRequest;
import org.igdevx.shopservice.dtos.ProductCategoryResponse;
import org.igdevx.shopservice.models.ProductCategory;
import org.springframework.stereotype.Component;

@Component
public class ProductCategoryMapper {

    public ProductCategoryResponse toResponse(ProductCategory category) {
        return ProductCategoryResponse.builder()
                .id(category.getId())
                .label(category.getLabel())
                .slug(category.getSlug())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .isDeleted(category.getIsDeleted())
                .build();
    }

    public ProductCategory toEntity(ProductCategoryRequest request) {
        return ProductCategory.builder()
                .label(request.getLabel())
                .slug(request.getSlug())
                .build();
    }

    public void updateEntity(ProductCategory category, ProductCategoryRequest request) {
        category.setLabel(request.getLabel());
        category.setSlug(request.getSlug());
    }
}
