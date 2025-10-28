package org.igdevx.shopservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.igdevx.shopservice.dtos.ProductRequest;
import org.igdevx.shopservice.dtos.ProductResponse;
import org.igdevx.shopservice.exceptions.ResourceNotFoundException;
import org.igdevx.shopservice.mappers.ProductMapper;
import org.igdevx.shopservice.models.*;
import org.igdevx.shopservice.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CurrencyRepository currencyRepository;
    private final UnitRepository unitRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductCertificationRepository certificationRepository;
    private final ProductMapper productMapper;
    private final ImageStorageService imageStorageService;

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.debug("Fetching all products");
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.debug("Fetching product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllAvailableProducts() {
        log.debug("Fetching all available products");
        return productRepository.findAllAvailable()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllFreshProducts() {
        log.debug("Fetching all fresh products");
        return productRepository.findAllFresh()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        log.debug("Fetching products for category: {}", categoryId);
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String searchTerm) {
        log.debug("Searching products with term: {}", searchTerm);
        return productRepository.searchByTitle(searchTerm)
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.debug("Creating new product: {}", request.getTitle());

        // Fetch and validate related entities
        Currency currency = currencyRepository.findById(request.getCurrencyId())
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + request.getCurrencyId()));

        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + request.getUnitId()));

        ProductCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        // Fetch certifications if provided
        Set<ProductCertification> certifications = new HashSet<>();
        if (request.getCertificationIds() != null && !request.getCertificationIds().isEmpty()) {
            certifications = request.getCertificationIds().stream()
                    .map(id -> certificationRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Certification not found with id: " + id)))
                    .collect(Collectors.toSet());
        }

        // Build product
        Product product = Product.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .currency(currency)
                .unit(unit)
                .category(category)
                .certifications(certifications)
                .isFresh(request.getIsFresh() != null ? request.getIsFresh() : false)
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product created with id: {}", savedProduct.getId());
        return productMapper.toResponse(savedProduct);
    }

    @Transactional
    public ProductResponse uploadProductImage(Long productId, MultipartFile imageFile) throws IOException {
        log.debug("Uploading image for product: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Delete old image if exists
        if (product.getImageKey() != null) {
            imageStorageService.deleteImage(product.getImageKey());
        }

        // Upload new image
        String imageKey = imageStorageService.uploadImage(imageFile);
        String imageUrl = imageStorageService.getImageUrl(imageKey);

        product.setImageKey(imageKey);
        product.setImageUrl(imageUrl);

        Product updatedProduct = productRepository.save(product);
        log.info("Image uploaded for product: {}", productId);
        return productMapper.toResponse(updatedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.debug("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Update basic fields
        productMapper.updateBasicFields(product, request);

        // Update related entities if changed
        if (!product.getCurrency().getId().equals(request.getCurrencyId())) {
            Currency currency = currencyRepository.findById(request.getCurrencyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + request.getCurrencyId()));
            product.setCurrency(currency);
        }

        if (!product.getUnit().getId().equals(request.getUnitId())) {
            Unit unit = unitRepository.findById(request.getUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + request.getUnitId()));
            product.setUnit(unit);
        }

        if (!product.getCategory().getId().equals(request.getCategoryId())) {
            ProductCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

        // Update certifications
        if (request.getCertificationIds() != null) {
            Set<ProductCertification> certifications = request.getCertificationIds().stream()
                    .map(certId -> certificationRepository.findById(certId)
                            .orElseThrow(() -> new ResourceNotFoundException("Certification not found with id: " + certId)))
                    .collect(Collectors.toSet());
            product.setCertifications(certifications);
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated with id: {}", id);
        return productMapper.toResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.debug("Soft deleting product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.softDelete();
        productRepository.save(product);
        log.info("Product soft deleted with id: {}", id);
    }

    @Transactional
    public void restoreProduct(Long id) {
        log.debug("Restoring product with id: {}", id);

        Product product = productRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!product.getIsDeleted()) {
            throw new IllegalStateException("Product with id " + id + " is not deleted");
        }

        product.restore();
        productRepository.save(product);
        log.info("Product restored with id: {}", id);
    }

    @Transactional
    public void hardDeleteProduct(Long id) {
        log.debug("Hard deleting product with id: {}", id);

        Product product = productRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Delete image if exists
        if (product.getImageKey() != null) {
            imageStorageService.deleteImage(product.getImageKey());
        }

        productRepository.hardDeleteById(id);
        log.info("Product hard deleted with id: {}", id);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllDeletedProducts() {
        log.debug("Fetching all deleted products");
        return productRepository.findAllDeleted()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }
}
