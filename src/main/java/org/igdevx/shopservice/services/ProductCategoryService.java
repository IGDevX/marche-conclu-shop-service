package org.igdevx.shopservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.igdevx.shopservice.dtos.ProductCategoryRequest;
import org.igdevx.shopservice.dtos.ProductCategoryResponse;
import org.igdevx.shopservice.exceptions.DuplicateResourceException;
import org.igdevx.shopservice.exceptions.ResourceNotFoundException;
import org.igdevx.shopservice.mappers.ProductCategoryMapper;
import org.igdevx.shopservice.models.ProductCategory;
import org.igdevx.shopservice.repositories.ProductCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductCategoryMapper productCategoryMapper;

    @Transactional(readOnly = true)
    public List<ProductCategoryResponse> getAllCategories() {
        log.debug("Fetching all product categories");
        return productCategoryRepository.findAll()
                .stream()
                .map(productCategoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductCategoryResponse getCategoryById(Long id) {
        log.debug("Fetching product category with id: {}", id);
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with id: " + id));
        return productCategoryMapper.toResponse(category);
    }

    @Transactional(readOnly = true)
    public ProductCategoryResponse getCategoryBySlug(String slug) {
        log.debug("Fetching product category with slug: {}", slug);
        ProductCategory category = productCategoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with slug: " + slug));
        return productCategoryMapper.toResponse(category);
    }

    @Transactional
    public ProductCategoryResponse createCategory(ProductCategoryRequest request) {
        log.debug("Creating new product category with label: {}", request.getLabel());
        
        if (productCategoryRepository.existsByLabel(request.getLabel())) {
            throw new DuplicateResourceException("Product category already exists with label: " + request.getLabel());
        }

        if (request.getSlug() != null && productCategoryRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Product category already exists with slug: " + request.getSlug());
        }

        ProductCategory category = productCategoryMapper.toEntity(request);
        ProductCategory savedCategory = productCategoryRepository.save(category);
        log.info("Product category created successfully with id: {}", savedCategory.getId());
        
        return productCategoryMapper.toResponse(savedCategory);
    }

    @Transactional
    public ProductCategoryResponse updateCategory(Long id, ProductCategoryRequest request) {
        log.debug("Updating product category with id: {}", id);
        
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with id: " + id));

        // Check if label is being changed and if new label already exists
        if (!category.getLabel().equals(request.getLabel()) && productCategoryRepository.existsByLabel(request.getLabel())) {
            throw new DuplicateResourceException("Product category already exists with label: " + request.getLabel());
        }

        // Check if slug is being changed and if new slug already exists
        if (request.getSlug() != null && !request.getSlug().equals(category.getSlug()) 
                && productCategoryRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Product category already exists with slug: " + request.getSlug());
        }

        productCategoryMapper.updateEntity(category, request);
        ProductCategory updatedCategory = productCategoryRepository.save(category);
        log.info("Product category updated successfully with id: {}", updatedCategory.getId());
        
        return productCategoryMapper.toResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.debug("Soft deleting product category with id: {}", id);
        
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with id: " + id));

        category.softDelete();
        productCategoryRepository.save(category);
        log.info("Product category soft deleted successfully with id: {}", id);
    }

    @Transactional
    public void restoreCategory(Long id) {
        log.debug("Restoring product category with id: {}", id);
        
        ProductCategory category = productCategoryRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with id: " + id));

        if (!category.getIsDeleted()) {
            throw new IllegalStateException("Product category with id " + id + " is not deleted");
        }

        category.restore();
        productCategoryRepository.save(category);
        log.info("Product category restored successfully with id: {}", id);
    }

    @Transactional
    public void hardDeleteCategory(Long id) {
        log.debug("Hard deleting product category with id: {}", id);
        
        // Verify category exists (including deleted ones)
        productCategoryRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with id: " + id));

        productCategoryRepository.hardDeleteById(id);
        log.info("Product category hard deleted successfully with id: {}", id);
    }

    @Transactional(readOnly = true)
    public List<ProductCategoryResponse> getAllDeletedCategories() {
        log.debug("Fetching all deleted product categories");
        return productCategoryRepository.findAllDeleted()
                .stream()
                .map(productCategoryMapper::toResponse)
                .collect(Collectors.toList());
    }
}
