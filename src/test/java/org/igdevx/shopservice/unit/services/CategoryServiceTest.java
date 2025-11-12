package org.igdevx.shopservice.unit.services;

import org.igdevx.shopservice.UnitTest;
import org.igdevx.shopservice.dtos.CategoryRequest;
import org.igdevx.shopservice.dtos.CategoryResponse;
import org.igdevx.shopservice.exceptions.DuplicateResourceException;
import org.igdevx.shopservice.exceptions.ResourceNotFoundException;
import org.igdevx.shopservice.mappers.CategoryMapper;
import org.igdevx.shopservice.models.Category;
import org.igdevx.shopservice.repositories.CategoryRepository;
import org.igdevx.shopservice.services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@UnitTest

@ExtendWith(MockitoExtension.class)
@DisplayName("Category Service Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryRequest categoryRequest;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Fruits")
                .slug("fruits")
                .description("Fresh fruits including apples, oranges, bananas, berries, etc.")
                .displayOrder(1)
                .build();

        categoryRequest = CategoryRequest.builder()
                .name("Fruits")
                .slug("fruits")
                .description("Fresh fruits including apples, oranges, bananas, berries, etc.")
                .displayOrder(1)
                .build();

        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Fruits")
                .slug("fruits")
                .description("Fresh fruits including apples, oranges, bananas, berries, etc.")
                .displayOrder(1)
                .build();
    }

    @Test
    @DisplayName("Should return all categories")
    void getAllCategories_ShouldReturnAllCategories() {
        // Given
        List<Category> categories = Collections.singletonList(category);
        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // When
        List<CategoryResponse> result = categoryService.getAllCategories();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Fruits");
        assertThat(result.get(0).getSlug()).isEqualTo("fruits");
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return category by ID when exists")
    void getCategoryById_WhenExists_ShouldReturnCategory() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // When
        CategoryResponse result = categoryService.getCategoryById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Fruits");
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
                .hasMessageContaining("Category not found with id: 999");
        verify(categoryRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should return category by slug when exists")
    void getCategoryBySlug_WhenExists_ShouldReturnCategory() {
        // Given
        when(categoryRepository.findBySlug("fruits")).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // When
        CategoryResponse result = categoryService.getCategoryBySlug("fruits");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo("fruits");
        assertThat(result.getName()).isEqualTo("Fruits");
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
                .hasMessageContaining("Category not found with slug: unknown");
        verify(categoryRepository, times(1)).findBySlug("unknown");
    }

    @Test
    @DisplayName("Should create category successfully")
    void createCategory_WhenNameAndSlugNotExist_ShouldCreateCategory() {
        // Given
        when(categoryRepository.existsByName("Fruits")).thenReturn(false);
        when(categoryRepository.existsBySlug("fruits")).thenReturn(false);
        when(categoryMapper.toEntity(categoryRequest)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // When
        CategoryResponse result = categoryService.createCategory(categoryRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Fruits");
        assertThat(result.getSlug()).isEqualTo("fruits");
        verify(categoryRepository, times(1)).existsByName("Fruits");
        verify(categoryRepository, times(1)).existsBySlug("fruits");
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("Should throw exception when creating category with duplicate name")
    void createCategory_WhenNameExists_ShouldThrowException() {
        // Given
        when(categoryRepository.existsByName("Fruits")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.createCategory(categoryRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Category already exists with name: Fruits");
        verify(categoryRepository, times(1)).existsByName("Fruits");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating category with duplicate slug")
    void createCategory_WhenSlugExists_ShouldThrowException() {
        // Given
        when(categoryRepository.existsByName("Fruits")).thenReturn(false);
        when(categoryRepository.existsBySlug("fruits")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.createCategory(categoryRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Category already exists with slug: fruits");
        verify(categoryRepository, times(1)).existsByName("Fruits");
        verify(categoryRepository, times(1)).existsBySlug("fruits");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update category successfully")
    void updateCategory_WhenExists_ShouldUpdateCategory() {
        // Given
        CategoryRequest updateRequest = CategoryRequest.builder()
                .name("Fruits Updated")
                .slug("fruits-updated")
                .description("Updated description")
                .displayOrder(2)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Fruits Updated")).thenReturn(false);
        when(categoryRepository.existsBySlug("fruits-updated")).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);
        doNothing().when(categoryMapper).updateEntity(category, updateRequest);

        // When
        CategoryResponse result = categoryService.updateCategory(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsByName("Fruits Updated");
        verify(categoryRepository, times(1)).existsBySlug("fruits-updated");
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("Should update category successfully when name doesn't change")
    void updateCategory_WhenNameNotChanged_ShouldNotCheckNameExistence() {
        // Given
        CategoryRequest updateRequest = CategoryRequest.builder()
                .name("Fruits")  // Same name
                .slug("fruits-new")
                .description("Updated description")
                .displayOrder(2)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsBySlug("fruits-new")).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);
        doNothing().when(categoryMapper).updateEntity(category, updateRequest);

        // When
        CategoryResponse result = categoryService.updateCategory(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, never()).existsByName(any());  // Name didn't change
        verify(categoryRepository, times(1)).existsBySlug("fruits-new");
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("Should throw exception when updating category with existing name")
    void updateCategory_WhenNameChangesToExisting_ShouldThrowException() {
        // Given
        CategoryRequest updateRequest = CategoryRequest.builder()
                .name("Vegetables")  // Different name that exists
                .slug("fruits")
                .description("Updated description")
                .displayOrder(2)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Vegetables")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.updateCategory(1L, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Category already exists with name: Vegetables");
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsByName("Vegetables");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating category with existing slug")
    void updateCategory_WhenSlugChangesToExisting_ShouldThrowException() {
        // Given
        CategoryRequest updateRequest = CategoryRequest.builder()
                .name("Fruits")
                .slug("vegetables")  // Different slug that exists
                .description("Updated description")
                .displayOrder(2)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsBySlug("vegetables")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.updateCategory(1L, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Category already exists with slug: vegetables");
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
                .hasMessageContaining("Category not found with id: 999");
        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should soft delete category successfully")
    void deleteCategory_WhenExists_ShouldSoftDeleteCategory() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

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
                .hasMessageContaining("Category not found with id: 999");
        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should restore deleted category successfully")
    void restoreCategory_WhenDeleted_ShouldRestoreCategory() {
        // Given
        category.softDelete(); // Mark as deleted
        when(categoryRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

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
                .hasMessageContaining("Category with id 1 is not deleted");
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
                .hasMessageContaining("Category not found with id: 999");
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
                .hasMessageContaining("Category not found with id: 999");
        verify(categoryRepository, times(1)).findByIdIncludingDeleted(999L);
        verify(categoryRepository, never()).hardDeleteById(any());
    }

    @Test
    @DisplayName("Should return all deleted categories")
    void getAllDeletedCategories_ShouldReturnDeletedCategories() {
        // Given
        category.softDelete();
        List<Category> deletedCategories = Collections.singletonList(category);
        when(categoryRepository.findAllDeleted()).thenReturn(deletedCategories);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // When
        List<CategoryResponse> result = categoryService.getAllDeletedCategories();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Fruits");
        verify(categoryRepository, times(1)).findAllDeleted();
    }

    @Test
    @DisplayName("Should return empty list when no categories exist")
    void getAllCategories_WhenNoCategories_ShouldReturnEmptyList() {
        // Given
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<CategoryResponse> result = categoryService.getAllCategories();

        // Then
        assertThat(result).isEmpty();
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no deleted categories exist")
    void getAllDeletedCategories_WhenNoDeletedCategories_ShouldReturnEmptyList() {
        // Given
        when(categoryRepository.findAllDeleted()).thenReturn(Collections.emptyList());

        // When
        List<CategoryResponse> result = categoryService.getAllDeletedCategories();

        // Then
        assertThat(result).isEmpty();
        verify(categoryRepository, times(1)).findAllDeleted();
    }
}
