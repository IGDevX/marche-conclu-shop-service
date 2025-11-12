package org.igdevx.shopservice.mappers;

import org.igdevx.shopservice.dtos.CategoryRequest;
import org.igdevx.shopservice.dtos.CategoryResponse;
import org.igdevx.shopservice.models.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .displayOrder(category.getDisplayOrder())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .isDeleted(category.getIsDeleted())
                .build();
    }

    public Category toEntity(CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder())
                .build();
    }

    public void updateEntity(Category category, CategoryRequest request) {
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());
        category.setDisplayOrder(request.getDisplayOrder());
    }
}

