package org.igdevx.shopservice.elasticsearch.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.igdevx.shopservice.elasticsearch.documents.ProductDocument;
import org.igdevx.shopservice.elasticsearch.mappers.ProductDocumentMapper;
import org.igdevx.shopservice.elasticsearch.repositories.ProductSearchRepository;
import org.igdevx.shopservice.exceptions.ResourceNotFoundException;
import org.igdevx.shopservice.models.Product;
import org.igdevx.shopservice.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductIndexService {

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;
    private final ProductDocumentMapper documentMapper;
    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * Index a single product
     */
    public void indexProduct(Product product) {
        log.debug("Indexing product with id: {}", product.getId());
        ProductDocument document = documentMapper.toDocument(product);
        productSearchRepository.save(document);
        log.info("Successfully indexed product with id: {}", product.getId());
    }

    /**
     * Index a product by ID
     */
    @Transactional(readOnly = true)
    public void indexProductById(Long productId) {
        log.debug("Indexing product by id: {}", productId);
        Product product = productRepository.findByIdIncludingDeleted(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        indexProduct(product);
    }

    /**
     * Delete a product from index
     */
    public void deleteFromIndex(Long productId) {
        log.debug("Deleting product from index with id: {}", productId);
        productSearchRepository.deleteById(productId);
        log.info("Successfully deleted product from index with id: {}", productId);
    }

    /**
     * Reindex all products
     */
    @Transactional(readOnly = true)
    public void reindexAll() {
        log.info("Starting full reindexation of all products");

        // Clear existing index
        productSearchRepository.deleteAll();
        log.debug("Cleared existing index");

        // Get all products (including deleted for complete sync)
        List<Product> products = productRepository.findAllIncludingDeleted();
        log.debug("Found {} products to index", products.size());

        // Index all products
        List<ProductDocument> documents = products.stream()
                .map(documentMapper::toDocument)
                .collect(Collectors.toList());

        productSearchRepository.saveAll(documents);

        log.info("Successfully reindexed {} products", products.size());
    }

    /**
     * Clear all documents from index
     */
    public void clearIndex() {
        log.info("Clearing all products from index");
        productSearchRepository.deleteAll();
        log.info("Successfully cleared index");
    }

    /**
     * Recreate the Elasticsearch index with updated mapping
     * This should be called when the ProductDocument structure changes
     */
    public void recreateIndex() {
        log.info("Recreating Elasticsearch index with new mapping");

        var indexOps = elasticsearchOperations.indexOps(ProductDocument.class);

        // Delete existing index if it exists
        if (indexOps.exists()) {
            log.debug("Deleting existing index");
            indexOps.delete();
        }

        // Create new index
        log.debug("Creating new index");
        indexOps.create();

        // Put mapping
        log.debug("Applying new mapping");
        indexOps.putMapping(indexOps.createMapping());

        log.info("Successfully recreated index with new mapping");
    }

    /**
     * Check if index exists and has documents
     */
    public long getIndexedProductsCount() {
        return productSearchRepository.count();
    }

    // ==================== ASYNC METHODS ====================

    /**
     * Index a single product asynchronously with retry
     */
    @Async("elasticsearchTaskExecutor")
    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 5000)
    )
    public CompletableFuture<Void> indexProductAsync(Product product) {
        try {
            log.debug("Async indexing product with id: {}", product.getId());
            indexProduct(product);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Failed to index product {} after retries", product.getId(), e);
            // TODO: Send to Dead Letter Queue or error monitoring system
            throw e;
        }
    }

    /**
     * Index a product by ID asynchronously
     */
    @Async("elasticsearchTaskExecutor")
    @Transactional(readOnly = true)
    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 5000)
    )
    public CompletableFuture<Void> indexProductByIdAsync(Long productId) {
        try {
            log.debug("Async indexing product by id: {}", productId);
            indexProductById(productId);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Failed to async index product {} after retries", productId, e);
            throw e;
        }
    }

    /**
     * Delete a product from index asynchronously
     */
    @Async("elasticsearchTaskExecutor")
    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 5000)
    )
    public CompletableFuture<Void> deleteFromIndexAsync(Long productId) {
        try {
            log.debug("Async deleting product from index with id: {}", productId);
            deleteFromIndex(productId);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Failed to async delete product {} from index after retries", productId, e);
            throw e;
        }
    }

    /**
     * Reindex all products with pagination (memory safe)
     */
    @Transactional(readOnly = true)
    public long reindexAllPaginated() {
        log.info("Starting paginated reindexation of all products");

        // Clear existing index
        productSearchRepository.deleteAll();
        log.debug("Cleared existing index");

        int pageSize = 1000;
        int page = 0;
        long totalIndexed = 0;
        long totalCount = productRepository.count();

        log.info("Total products to index: {}", totalCount);

        while (true) {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Product> productsPage = productRepository.findAllIncludingDeletedPaginated(pageable);

            if (productsPage.isEmpty()) {
                break;
            }

            List<ProductDocument> documents = productsPage.getContent().stream()
                    .map(documentMapper::toDocument)
                    .collect(Collectors.toList());

            try {
                productSearchRepository.saveAll(documents);
                totalIndexed += documents.size();
                log.info("Indexed batch {}/{} - Progress: {}/{} products ({}%)",
                        page + 1,
                        productsPage.getTotalPages(),
                        totalIndexed,
                        totalCount,
                        (totalIndexed * 100) / totalCount);
            } catch (Exception e) {
                log.error("Failed to index batch {} with {} products", page, documents.size(), e);
                // Continue with next batch instead of failing completely
            }

            page++;

            if (!productsPage.hasNext()) {
                break;
            }
        }

        log.info("Successfully reindexed {} products out of {}", totalIndexed, totalCount);
        return totalIndexed;
    }
}

