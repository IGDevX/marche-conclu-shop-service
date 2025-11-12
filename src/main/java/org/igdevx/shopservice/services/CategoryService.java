package org.igdevx.shopservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.igdevx.shopservice.dtos.CategoryRequest;
import org.igdevx.shopservice.dtos.CategoryResponse;
import org.igdevx.shopservice.exceptions.DuplicateResourceException;
import org.igdevx.shopservice.exceptions.ResourceNotFoundException;
import org.igdevx.shopservice.mappers.CategoryMapper;
import org.igdevx.shopservice.models.Category;
import org.igdevx.shopservice.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        log.debug("Fetching all categories");
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        log.debug("Fetching category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return categoryMapper.toResponse(category);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        log.debug("Fetching category with slug: {}", slug);
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.debug("Creating new category with name: {}", request.getName());

        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category already exists with name: " + request.getName());
        }

        if (request.getSlug() != null && categoryRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Category already exists with slug: " + request.getSlug());
        }

        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with id: {}", savedCategory.getId());

        return categoryMapper.toResponse(savedCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.debug("Updating category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Check if name is being changed and if new name already exists
        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category already exists with name: " + request.getName());
        }

        // Check if slug is being changed and if new slug already exists
        if (request.getSlug() != null && !request.getSlug().equals(category.getSlug())
                && categoryRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Category already exists with slug: " + request.getSlug());
        }

        categoryMapper.updateEntity(category, request);
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully with id: {}", updatedCategory.getId());

        return categoryMapper.toResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.debug("Soft deleting category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        category.softDelete();
        categoryRepository.save(category);
        log.info("Category soft deleted successfully with id: {}", id);
    }

    @Transactional
    public void restoreCategory(Long id) {
        log.debug("Restoring category with id: {}", id);

        Category category = categoryRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getIsDeleted()) {
            throw new IllegalStateException("Category with id " + id + " is not deleted");
        }

        category.restore();
        categoryRepository.save(category);
        log.info("Category restored successfully with id: {}", id);
    }

    @Transactional
    public void hardDeleteCategory(Long id) {
        log.debug("Hard deleting category with id: {}", id);

        // Verify category exists (including deleted ones)
        categoryRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        categoryRepository.hardDeleteById(id);
        log.info("Category hard deleted successfully with id: {}", id);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllDeletedCategories() {
        log.debug("Fetching all deleted categories");
        return categoryRepository.findAllDeleted()
                .stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }
}

