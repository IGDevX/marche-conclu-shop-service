package org.igdevx.shopservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.igdevx.shopservice.dtos.ProductRequest;
import org.igdevx.shopservice.dtos.ProductResponse;
import org.igdevx.shopservice.elasticsearch.services.ProductIndexService;
import org.igdevx.shopservice.exceptions.ResourceNotFoundException;
import org.igdevx.shopservice.mappers.ProductMapper;
import org.igdevx.shopservice.models.*;
import org.igdevx.shopservice.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ShelfRepository shelfRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCertificationRepository certificationRepository;
    private final ProductMapper productMapper;
    private final ProductIndexService productIndexService;

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
    public List<ProductResponse> getAllFreshProducts() {
        log.debug("Fetching all fresh products");
        return productRepository.findAllFresh()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByShelf(Long shelfId) {
        log.debug("Fetching products for shelf: {}", shelfId);
        return productRepository.findByShelfId(shelfId)
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

        Shelf shelf = shelfRepository.findById(request.getShelfId())
                .orElseThrow(() -> new ResourceNotFoundException("Shelf not found with id: " + request.getShelfId()));
        Category category = categoryRepository.findById(request.getCategoryId())
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
                .category(category)
                .price(request.getPrice())
                .currency(currency)
                .unit(unit)
                .shelf(shelf)
                .certifications(certifications)
                .isFresh(request.getIsFresh() != null ? request.getIsFresh() : false)
                .producerId(request.getProducerId())
                .build();

        Product savedProduct = productRepository.save(product);

        // Index in Elasticsearch asynchronously (non-blocking)
        productIndexService.indexProductAsync(savedProduct);

        log.info("Product created with id: {}", savedProduct.getId());
        return productMapper.toResponse(savedProduct);
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

        if (!product.getShelf().getId().equals(request.getShelfId())) {
        if (!product.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

            Shelf shelf = shelfRepository.findById(request.getShelfId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shelf not found with id: " + request.getShelfId()));
            product.setShelf(shelf);
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

        // Update Elasticsearch index asynchronously
        productIndexService.indexProductAsync(updatedProduct);

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

        // Update Elasticsearch index to reflect deleted status asynchronously
        productIndexService.indexProductAsync(product);

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

        // Update Elasticsearch index to reflect restored status asynchronously
        productIndexService.indexProductAsync(product);

        log.info("Product restored with id: {}", id);
    }

    @Transactional
    public void hardDeleteProduct(Long id) {
        log.debug("Hard deleting product with id: {}", id);

        Product product = productRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));


        productRepository.hardDeleteById(id);

        // Delete from Elasticsearch index asynchronously
        productIndexService.deleteFromIndexAsync(id);

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

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByProducerId(Long producerId) {
        log.debug("Fetching products for producer: {}", producerId);
        return productRepository.findByProducerId(producerId)
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByProducerIdAndShelfId(Long producerId, Long shelfId) {
        log.debug("Fetching products for producer: {} and shelf: {}", producerId, shelfId);
        return productRepository.findByProducerIdAndShelfId(producerId, shelfId)
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        log.debug("Fetching products for category: {}", categoryId);
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategoryIds(List<Long> categoryIds) {
        log.debug("Fetching products for categories: {}", categoryIds);
        if (categoryIds == null || categoryIds.isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByCategoryIdIn(categoryIds)
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }
}
