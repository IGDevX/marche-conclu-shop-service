package org.igdevx.shopservice.services;

import org.igdevx.shopservice.dtos.ProductCategoryRequest;
import org.igdevx.shopservice.dtos.ProductCategoryResponse;
import org.igdevx.shopservice.exceptions.DuplicateResourceException;
import org.igdevx.shopservice.exceptions.ResourceNotFoundException;
import org.igdevx.shopservice.mappers.ProductCategoryMapper;
import org.igdevx.shopservice.models.ProductCategory;
import org.igdevx.shopservice.repositories.ProductCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductCategory Service Tests")
class ProductCategoryServiceTest {

    @Mock
    private ProductCategoryRepository categoryRepository;

    @Mock
    private ProductCategoryMapper categoryMapper;

    @InjectMocks
    private ProductCategoryService categoryService;

    private ProductCategory category;
    private ProductCategoryRequest categoryRequest;
    private ProductCategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        category = ProductCategory.builder()
                .id(1L)
                .label("Fruits")
                .slug("fruits")
                .build();

        categoryRequest = ProductCategoryRequest.builder()
                .label("Fruits")
                .slug("fruits")
                .build();

        categoryResponse = ProductCategoryResponse.builder()
                .id(1L)
                .label("Fruits")
                .slug("fruits")
                .build();
    }

    @Test
    @DisplayName("Should return all categories")
    void getAllCategories_ShouldReturnAllCategories() {
        // Given
        List<ProductCategory> categories = Arrays.asList(category);
        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // When
        List<ProductCategoryResponse> result = categoryService.getAllCategories();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLabel()).isEqualTo("Fruits");
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return category by ID when exists")
    void getCategoryById_WhenExists_ShouldReturnCategory() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // When
        ProductCategoryResponse result = categoryService.getCategoryById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLabel()).isEqualTo("Fruits");
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when category not found by ID")
    void getCategoryById_WhenNotExists_ShouldThrowException() {
        // Given
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.getCategoryById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ProductCategory not found with id: 999");
        verify(categoryRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should return category by slug when exists")
    void getCategoryBySlug_WhenExists_ShouldReturnCategory() {
        // Given
        when(categoryRepository.findBySlug("fruits")).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // When
        ProductCategoryResponse result = categoryService.getCategoryBySlug("fruits");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo("fruits");
        verify(categoryRepository, times(1)).findBySlug("fruits");
    }

    @Test
    @DisplayName("Should throw exception when category not found by slug")
    void getCategoryBySlug_WhenNotExists_ShouldThrowException() {
        // Given
        when(categoryRepository.findBySlug("unknown")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.getCategoryBySlug("unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ProductCategory not found with slug: unknown");
        verify(categoryRepository, times(1)).findBySlug("unknown");
    }

    @Test
    @DisplayName("Should create category successfully")
    void createCategory_WhenSlugNotExists_ShouldCreateCategory() {
        // Given
        when(categoryRepository.existsBySlug("fruits")).thenReturn(false);
        when(categoryMapper.toEntity(categoryRequest)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // When
        ProductCategoryResponse result = categoryService.createCategory(categoryRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo("fruits");
        verify(categoryRepository, times(1)).existsBySlug("fruits");
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("Should throw exception when creating category with duplicate slug")
    void createCategory_WhenSlugExists_ShouldThrowException() {
        // Given
        when(categoryRepository.existsBySlug("fruits")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.createCategory(categoryRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("ProductCategory already exists with slug: fruits");
        verify(categoryRepository, times(1)).existsBySlug("fruits");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update category successfully when only label changes")
    void updateCategory_WhenExists_ShouldUpdateCategory() {
        // Given
        ProductCategoryRequest updateRequest = ProductCategoryRequest.builder()
                .label("Fruits Updated")
                .slug("fruits")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);
        doNothing().when(categoryMapper).updateEntity(category, updateRequest);

        // When
        ProductCategoryResponse result = categoryService.updateCategory(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, never()).existsBySlug(any()); // Slug unchanged, no check needed
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("Should update category successfully when slug changes to non-existing slug")
    void updateCategory_WhenSlugChangesToNonExisting_ShouldUpdateCategory() {
        // Given
        ProductCategoryRequest updateRequest = ProductCategoryRequest.builder()
                .label("Vegetables")
                .slug("vegetables")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsBySlug("vegetables")).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);
        doNothing().when(categoryMapper).updateEntity(category, updateRequest);

        // When
        ProductCategoryResponse result = categoryService.updateCategory(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsBySlug("vegetables");
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("Should throw exception when updating category with existing slug")
    void updateCategory_WhenSlugChangesToExisting_ShouldThrowException() {
        // Given
        ProductCategoryRequest updateRequest = ProductCategoryRequest.builder()
                .label("Vegetables")
                .slug("vegetables")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsBySlug("vegetables")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.updateCategory(1L, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("ProductCategory already exists with slug: vegetables");
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsBySlug("vegetables");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent category")
    void updateCategory_WhenNotExists_ShouldThrowException() {
        // Given
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.updateCategory(999L, categoryRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ProductCategory not found with id: 999");
        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should soft delete category successfully")
    void deleteCategory_WhenExists_ShouldSoftDeleteCategory() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(ProductCategory.class))).thenReturn(category);

        // When
        categoryService.deleteCategory(1L);

        // Then
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(category);
        assertThat(category.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when soft deleting non-existent category")
    void deleteCategory_WhenNotExists_ShouldThrowException() {
        // Given
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.deleteCategory(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ProductCategory not found with id: 999");
        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should restore deleted category successfully")
    void restoreCategory_WhenDeleted_ShouldRestoreCategory() {
        // Given
        category.softDelete(); // Mark as deleted
        when(categoryRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(ProductCategory.class))).thenReturn(category);

        // When
        categoryService.restoreCategory(1L);

        // Then
        verify(categoryRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(categoryRepository, times(1)).save(category);
        assertThat(category.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when restoring non-deleted category")
    void restoreCategory_WhenNotDeleted_ShouldThrowException() {
        // Given
        when(categoryRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(category));

        // When & Then
        assertThatThrownBy(() -> categoryService.restoreCategory(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ProductCategory with id 1 is not deleted");
        verify(categoryRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when restoring non-existent category")
    void restoreCategory_WhenNotExists_ShouldThrowException() {
        // Given
        when(categoryRepository.findByIdIncludingDeleted(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.restoreCategory(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ProductCategory not found with id: 999");
        verify(categoryRepository, times(1)).findByIdIncludingDeleted(999L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should hard delete category successfully")
    void hardDeleteCategory_WhenExists_ShouldHardDeleteCategory() {
        // Given
        when(categoryRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).hardDeleteById(1L);

        // When
        categoryService.hardDeleteCategory(1L);

        // Then
        verify(categoryRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(categoryRepository, times(1)).hardDeleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when hard deleting non-existent category")
    void hardDeleteCategory_WhenNotExists_ShouldThrowException() {
        // Given
        when(categoryRepository.findByIdIncludingDeleted(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.hardDeleteCategory(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ProductCategory not found with id: 999");
        verify(categoryRepository, times(1)).findByIdIncludingDeleted(999L);
        verify(categoryRepository, never()).hardDeleteById(any());
    }

    @Test
    @DisplayName("Should return all deleted categories")
    void getAllDeletedCategories_ShouldReturnDeletedCategories() {
        // Given
        category.softDelete();
        List<ProductCategory> deletedCategories = Arrays.asList(category);
        when(categoryRepository.findAllDeleted()).thenReturn(deletedCategories);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // When
        List<ProductCategoryResponse> result = categoryService.getAllDeletedCategories();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLabel()).isEqualTo("Fruits");
        verify(categoryRepository, times(1)).findAllDeleted();
    }
}
