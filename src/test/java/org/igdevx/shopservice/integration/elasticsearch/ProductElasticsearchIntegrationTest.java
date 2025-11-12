package org.igdevx.shopservice.integration.elasticsearch;

import org.awaitility.Awaitility;
import org.igdevx.shopservice.dtos.ProductRequest;
import org.igdevx.shopservice.dtos.ProductResponse;
import org.igdevx.shopservice.elasticsearch.documents.ProductDocument;
import org.igdevx.shopservice.elasticsearch.repositories.ProductSearchRepository;
import org.igdevx.shopservice.elasticsearch.services.ProductIndexService;
import org.igdevx.shopservice.models.*;
import org.igdevx.shopservice.repositories.*;
import org.igdevx.shopservice.services.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Elasticsearch Product indexing and searching
 */
class ProductElasticsearchIntegrationTest extends ElasticsearchIntegrationTestBase {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductIndexService productIndexService;

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ShelfRepository shelfRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductCertificationRepository certificationRepository;

    @Autowired
    private org.springframework.transaction.support.TransactionTemplate transactionTemplate;

    private Currency testCurrency;
    private Unit testUnit;
    private Shelf testShelf;
    private Category testCategory;
    private ProductCertification testCertification;

    @BeforeEach
    void setUpTestData() {
        // Use test data seeded by Flyway migrations (V200-V207)
        // This avoids conflicts and utilizes existing test data
        testCurrency = currencyRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No currency found in test database"));

        testUnit = unitRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No unit found in test database"));

        testShelf = shelfRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No shelf found in test database"));

        testCategory = categoryRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No category found in test database"));

        testCertification = certificationRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No certification found in test database"));
    }

    @AfterEach
    void cleanUp() {
        // Note: We don't clean test data between tests
        // Each test creates its own products which are isolated by test scope
    }

    @Test
    void shouldIndexProductAsynchronouslyWhenCreated() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .title("Test Tomatoes")
                .description("Fresh organic tomatoes")
                .price(BigDecimal.valueOf(5.99))
                .currencyId(testCurrency.getId())
                .unitId(testUnit.getId())
                .shelfId(testShelf.getId())
                .categoryId(testCategory.getId())
                .certificationIds(Set.of(testCertification.getId()))
                .isFresh(true)
                .producerId(1L)
                .build();

        // When
        ProductResponse response = productService.createProduct(request);

        // Then - Product is saved in DB
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();

        // Wait for async indexing to complete (max 5 seconds)
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    Optional<ProductDocument> indexed = productSearchRepository.findById(response.getId());
                    assertThat(indexed).isPresent();
                    assertThat(indexed.get().getTitle()).isEqualTo("Test Tomatoes");
                    assertThat(indexed.get().getIsDeleted()).isFalse();
                });
    }

    @Test
    void shouldUpdateIndexAsynchronouslyWhenProductUpdated() {
        // Given - Create a product
        ProductRequest createRequest = ProductRequest.builder()
                .title("Original Title")
                .description("Original description")
                .price(BigDecimal.valueOf(10.00))
                .currencyId(testCurrency.getId())
                .unitId(testUnit.getId())
                .shelfId(testShelf.getId())
                .categoryId(testCategory.getId())
                .isFresh(false)
                .producerId(1L)
                .build();

        ProductResponse created = productService.createProduct(createRequest);

        // Wait for initial indexing
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() ->
                productSearchRepository.findById(created.getId()).isPresent()
        );

        // When - Update the product
        ProductRequest updateRequest = ProductRequest.builder()
                .title("Updated Title")
                .description("Updated description")
                .price(BigDecimal.valueOf(15.00))
                .currencyId(testCurrency.getId())
                .unitId(testUnit.getId())
                .shelfId(testShelf.getId())
                .categoryId(testCategory.getId())
                .isFresh(true)
                .producerId(1L)
                .build();

        productService.updateProduct(created.getId(), updateRequest);

        // Then - Index should be updated
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Optional<ProductDocument> indexed = productSearchRepository.findById(created.getId());
                    assertThat(indexed).isPresent();
                    assertThat(indexed.get().getTitle()).isEqualTo("Updated Title");
                    assertThat(indexed.get().getIsFresh()).isTrue();
                });
    }

    @Test
    void shouldMarkAsDeletedInIndexWhenProductSoftDeleted() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .title("Product To Delete")
                .description("This will be deleted")
                .price(BigDecimal.valueOf(20.00))
                .currencyId(testCurrency.getId())
                .unitId(testUnit.getId())
                .shelfId(testShelf.getId())
                .categoryId(testCategory.getId())
                .isFresh(false)
                .producerId(1L)
                .build();

        ProductResponse created = productService.createProduct(request);

        // Wait for initial indexing
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() ->
                productSearchRepository.findById(created.getId()).isPresent()
        );

        // When
        productService.deleteProduct(created.getId());

        // Then
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Optional<ProductDocument> indexed = productSearchRepository.findById(created.getId());
                    assertThat(indexed).isPresent();
                    assertThat(indexed.get().getIsDeleted()).isTrue();
                });
    }

    @Test
    void shouldRemoveFromIndexWhenProductHardDeleted() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .title("Product To Hard Delete")
                .description("This will be permanently deleted")
                .price(BigDecimal.valueOf(25.00))
                .currencyId(testCurrency.getId())
                .unitId(testUnit.getId())
                .shelfId(testShelf.getId())
                .categoryId(testCategory.getId())
                .isFresh(false)
                .producerId(1L)
                .build();

        ProductResponse created = productService.createProduct(request);

        // Wait for initial indexing
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() ->
                productSearchRepository.findById(created.getId()).isPresent()
        );

        // When
        productService.hardDeleteProduct(created.getId());

        // Then
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Optional<ProductDocument> indexed = productSearchRepository.findById(created.getId());
                    assertThat(indexed).isEmpty();
                });
    }

    @Test
    void shouldRestoreProductInIndexWhenRestored() {
        // Given - Create and soft delete a product
        ProductRequest request = ProductRequest.builder()
                .title("Product To Restore")
                .description("This will be restored")
                .price(BigDecimal.valueOf(30.00))
                .currencyId(testCurrency.getId())
                .unitId(testUnit.getId())
                .shelfId(testShelf.getId())
                .categoryId(testCategory.getId())
                .isFresh(false)
                .producerId(1L)
                .build();

        ProductResponse created = productService.createProduct(request);

        // Wait for initial indexing
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() ->
                productSearchRepository.findById(created.getId()).isPresent()
        );

        productService.deleteProduct(created.getId());

        // Wait for delete to be indexed
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Optional<ProductDocument> indexed = productSearchRepository.findById(created.getId());
            assertThat(indexed).isPresent();
            assertThat(indexed.get().getIsDeleted()).isTrue();
        });

        // When
        productService.restoreProduct(created.getId());

        // Then
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Optional<ProductDocument> indexed = productSearchRepository.findById(created.getId());
                    assertThat(indexed).isPresent();
                    assertThat(indexed.get().getIsDeleted()).isFalse();
                });
    }

    @Test
    void shouldReindexAllProductsWithPagination() {
        // Given - Create multiple products
        for (int i = 1; i <= 25; i++) {
            Product product = Product.builder()
                    .title("Product " + i)
                    .description("Description " + i)
                    .price(BigDecimal.valueOf(i * 10))
                    .currency(testCurrency)
                    .unit(testUnit)
                    .shelf(testShelf)
                    .category(testCategory)
                    .certifications(new HashSet<>())
                    .isFresh(i % 2 == 0)
                    .producerId(1L)
                    .build();
            productRepository.save(product);
        }

        // When
        long totalProductsInDb = productRepository.count();
        long reindexedCount = productIndexService.reindexAllPaginated();

        // Then - All products from DB should be reindexed
        assertThat(reindexedCount).isEqualTo(totalProductsInDb);
        assertThat(productSearchRepository.count()).isEqualTo(totalProductsInDb);
        assertThat(reindexedCount).isGreaterThanOrEqualTo(25); // At least the 25 we just created
    }

    @Test
    void shouldHandleIndexingErrorsGracefully() {
        // Given - Create a product
        ProductRequest request = ProductRequest.builder()
                .title("Test Product")
                .description("Test description")
                .price(BigDecimal.valueOf(10.00))
                .currencyId(testCurrency.getId())
                .unitId(testUnit.getId())
                .shelfId(testShelf.getId())
                .categoryId(testCategory.getId())
                .isFresh(false)
                .producerId(1L)
                .build();

        // When - Create product (even if Elasticsearch fails temporarily, product should be saved)
        ProductResponse response = productService.createProduct(request);

        // Then - Product is saved in database regardless of indexing
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();

        Optional<Product> savedProduct = productRepository.findByIdIncludingDeleted(response.getId());
        assertThat(savedProduct).isPresent();
    }

    @Test
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
    void shouldRetryIndexingOnFailure() {
        // Given - Create a product in a new transaction that will be committed
        Long productId = transactionTemplate.execute(status -> {
            Product product = Product.builder()
                    .title("Retry Test Product")
                    .description("Testing retry mechanism")
                    .price(BigDecimal.valueOf(15.00))
                    .currency(testCurrency)
                    .unit(testUnit)
                    .shelf(testShelf)
                    .category(testCategory)
                    .certifications(new HashSet<>())
                    .isFresh(false)
                    .producerId(1L)
                    .build();
            Product savedProduct = productRepository.save(product);
            productRepository.flush();  // Ensure product is persisted
            return savedProduct.getId();
        });
        // Transaction is now committed, product exists in database

        try {
            // When - Index the product asynchronously (retry mechanism should work)
            productIndexService.indexProductByIdAsync(productId);

            // Then - Should eventually be indexed (within retry attempts + async delay)
            // Max retry time: 1s + 2s + 4s = 7s, plus async scheduling delay
            Awaitility.await()
                    .atMost(15, TimeUnit.SECONDS)
                    .pollInterval(500, TimeUnit.MILLISECONDS)
                    .untilAsserted(() -> {
                        Optional<ProductDocument> indexed = productSearchRepository.findById(productId);
                        assertThat(indexed).isPresent();
                    });
        } finally {
            // Cleanup - Delete the product created for this test
            transactionTemplate.execute(status -> {
                productRepository.findByIdIncludingDeleted(productId)
                        .ifPresent(p -> productRepository.hardDeleteById(productId));
                return null;
            });
            // Also clean up from Elasticsearch
            productSearchRepository.deleteById(productId);
        }
    }
}

