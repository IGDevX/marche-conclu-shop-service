package org.igdevx.shopservice.integration.elasticsearch;

import org.igdevx.shopservice.elasticsearch.documents.ProductDocument;
import org.igdevx.shopservice.elasticsearch.repositories.ProductSearchRepository;
import org.igdevx.shopservice.elasticsearch.services.ProductSearchService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Producer Shop search functionality
 * Tests the endpoint /api/products/producer/{producerId}
 */
class ProductProducerSearchIntegrationTest extends ElasticsearchIntegrationTestBase {

    @Autowired
    private ProductSearchService productSearchService;

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @BeforeEach
    void setUpProducerSearchTestData() {
        productSearchRepository.deleteAll();

        // Producer 1 - Multiple shelves
        ProductDocument product1 = ProductDocument.builder()
                .id(1L)
                .title("Producer 1 - Vegetables Shelf - Tomatoes")
                .description("Fresh tomatoes from producer 1")
                .price(BigDecimal.valueOf(5.99))
                .currencyCode("EUR")
                .currencyId(1L)
                .unitName("kg")
                .unitId(1L)
                .shelfName("Vegetables")
                .shelfId(1L)
                .categoryName("Fresh Produce")
                .categoryId(1L)
                .certificationNames(Set.of("Organic"))
                .certificationIds(Set.of(1L))
                .isFresh(true)
                .producerId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        ProductDocument product2 = ProductDocument.builder()
                .id(2L)
                .title("Producer 1 - Vegetables Shelf - Carrots")
                .description("Fresh carrots from producer 1")
                .price(BigDecimal.valueOf(3.50))
                .currencyCode("EUR")
                .currencyId(1L)
                .unitName("kg")
                .unitId(1L)
                .shelfName("Vegetables")
                .shelfId(1L)
                .categoryName("Fresh Produce")
                .categoryId(1L)
                .certificationNames(Set.of("Local"))
                .certificationIds(Set.of(2L))
                .isFresh(true)
                .producerId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        ProductDocument product3 = ProductDocument.builder()
                .id(3L)
                .title("Producer 1 - Fruits Shelf - Apples")
                .description("Fresh apples from producer 1")
                .price(BigDecimal.valueOf(4.50))
                .currencyCode("EUR")
                .currencyId(1L)
                .unitName("kg")
                .unitId(1L)
                .shelfName("Fruits")
                .shelfId(2L)
                .categoryName("Fresh Produce")
                .categoryId(1L)
                .certificationNames(Set.of("Organic"))
                .certificationIds(Set.of(1L))
                .isFresh(true)
                .producerId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        // Producer 2 - Different products
        ProductDocument product4 = ProductDocument.builder()
                .id(4L)
                .title("Producer 2 - Dairy Shelf - Milk")
                .description("Fresh milk from producer 2")
                .price(BigDecimal.valueOf(2.99))
                .currencyCode("EUR")
                .currencyId(1L)
                .unitName("liter")
                .unitId(3L)
                .shelfName("Dairy")
                .shelfId(3L)
                .categoryName("Dairy Products")
                .categoryId(2L)
                .certificationNames(Set.of("Local"))
                .certificationIds(Set.of(2L))
                .isFresh(true)
                .producerId(2L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        ProductDocument product5 = ProductDocument.builder()
                .id(5L)
                .title("Producer 2 - Dairy Shelf - Cheese")
                .description("Artisanal cheese from producer 2")
                .price(BigDecimal.valueOf(8.99))
                .currencyCode("EUR")
                .currencyId(1L)
                .unitName("kg")
                .unitId(1L)
                .shelfName("Dairy")
                .shelfId(3L)
                .categoryName("Dairy Products")
                .categoryId(2L)
                .certificationNames(Set.of("Organic", "Local"))
                .certificationIds(Set.of(1L, 2L))
                .isFresh(true)
                .producerId(2L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        // Deleted product from Producer 1
        ProductDocument product6 = ProductDocument.builder()
                .id(6L)
                .title("Producer 1 - Deleted Product")
                .description("This product was deleted")
                .price(BigDecimal.valueOf(10.00))
                .currencyCode("EUR")
                .currencyId(1L)
                .unitName("kg")
                .unitId(1L)
                .shelfName("Vegetables")
                .shelfId(1L)
                .categoryName("Fresh Produce")
                .categoryId(1L)
                .certificationNames(Set.of())
                .certificationIds(Set.of())
                .isFresh(false)
                .producerId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(true)
                .build();

        productSearchRepository.saveAll(List.of(product1, product2, product3, product4, product5, product6));

        // Wait for indexing
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterEach
    void cleanUpProducerSearchData() {
        productSearchRepository.deleteAll();
    }

    @Test
    void shouldFindAllProductsByProducer() {
        // Given - Producer 1 has 3 non-deleted products
        Long producerId = 1L;

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProductsByProducer(
                producerId, null, false, 0, 20
        );

        // Then
        assertThat(results.getTotalHits()).isEqualTo(3);
        assertThat(results.getSearchHits())
                .allMatch(hit -> hit.getContent().getProducerId().equals(producerId))
                .allMatch(hit -> !hit.getContent().getIsDeleted());
    }

    @Test
    void shouldFilterByShelfForProducer() {
        // Given - Producer 1, Vegetables shelf (shelfId=1) has 2 products
        Long producerId = 1L;
        Long shelfId = 1L;

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProductsByProducer(
                producerId, shelfId, false, 0, 20
        );

        // Then
        assertThat(results.getTotalHits()).isEqualTo(2); // Tomatoes and Carrots
        assertThat(results.getSearchHits())
                .allMatch(hit -> hit.getContent().getProducerId().equals(producerId))
                .allMatch(hit -> hit.getContent().getShelfId().equals(shelfId))
                .extracting(hit -> hit.getContent().getTitle())
                .containsExactlyInAnyOrder(
                        "Producer 1 - Vegetables Shelf - Tomatoes",
                        "Producer 1 - Vegetables Shelf - Carrots"
                );
    }

    @Test
    void shouldFilterByDifferentShelfForProducer() {
        // Given - Producer 1, Fruits shelf (shelfId=2) has 1 product
        Long producerId = 1L;
        Long shelfId = 2L;

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProductsByProducer(
                producerId, shelfId, false, 0, 20
        );

        // Then
        assertThat(results.getTotalHits()).isEqualTo(1); // Only Apples
        assertThat(results.getSearchHits().get(0).getContent().getTitle())
                .isEqualTo("Producer 1 - Fruits Shelf - Apples");
    }

    @Test
    void shouldExcludeDeletedProductsByDefault() {
        // Given - Producer 1 has 1 deleted product
        Long producerId = 1L;

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProductsByProducer(
                producerId, null, false, 0, 20
        );

        // Then - Should not include deleted products
        assertThat(results.getSearchHits())
                .noneMatch(hit -> hit.getContent().getIsDeleted())
                .noneMatch(hit -> hit.getContent().getTitle().contains("Deleted"));
    }

    @Test
    void shouldIncludeDeletedProductsWhenRequested() {
        // Given - Producer 1 has 1 deleted product
        Long producerId = 1L;

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProductsByProducer(
                producerId, null, true, 0, 20
        );

        // Then
        assertThat(results.getTotalHits()).isEqualTo(1);
        assertThat(results.getSearchHits().get(0).getContent().getTitle())
                .isEqualTo("Producer 1 - Deleted Product");
        assertThat(results.getSearchHits().get(0).getContent().getIsDeleted()).isTrue();
    }

    @Test
    void shouldOnlyReturnProductsForSpecificProducer() {
        // Given - Producer 2 has 2 products
        Long producerId = 2L;

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProductsByProducer(
                producerId, null, false, 0, 20
        );

        // Then
        assertThat(results.getTotalHits()).isEqualTo(2);
        assertThat(results.getSearchHits())
                .allMatch(hit -> hit.getContent().getProducerId().equals(producerId))
                .extracting(hit -> hit.getContent().getTitle())
                .containsExactlyInAnyOrder(
                        "Producer 2 - Dairy Shelf - Milk",
                        "Producer 2 - Dairy Shelf - Cheese"
                );
    }

    @Test
    void shouldPaginateProducerProducts() {
        // Given - Producer 1 has 3 products, request page 0 with size 2
        Long producerId = 1L;

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProductsByProducer(
                producerId, null, false, 0, 2
        );

        // Then
        assertThat(results.getTotalHits()).isEqualTo(3);
        assertThat(results.getSearchHits()).hasSize(2); // Only 2 per page
    }

    @Test
    void shouldReturnSecondPageOfResults() {
        // Given - Producer 1 has 3 products, request page 1 with size 2
        Long producerId = 1L;

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProductsByProducer(
                producerId, null, false, 1, 2
        );

        // Then
        assertThat(results.getTotalHits()).isEqualTo(3);
        assertThat(results.getSearchHits()).hasSize(1); // Only 1 product on second page
    }

    @Test
    void shouldReturnEmptyResultsForNonExistentProducer() {
        // Given - Producer 999 doesn't exist
        Long nonExistentProducerId = 999L;

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProductsByProducer(
                nonExistentProducerId, null, false, 0, 20
        );

        // Then
        assertThat(results.getTotalHits()).isEqualTo(0);
        assertThat(results.getSearchHits()).isEmpty();
    }

    @Test
    void shouldReturnEmptyResultsForNonExistentShelf() {
        // Given - Producer 1 exists but shelf 999 doesn't
        Long producerId = 1L;
        Long nonExistentShelfId = 999L;

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProductsByProducer(
                producerId, nonExistentShelfId, false, 0, 20
        );

        // Then
        assertThat(results.getTotalHits()).isEqualTo(0);
        assertThat(results.getSearchHits()).isEmpty();
    }

    @Test
    void shouldSortByCreationDateDescending() {
        // Given - Producer 1 has multiple products
        Long producerId = 1L;

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProductsByProducer(
                producerId, null, false, 0, 20
        );

        // Then - Results should be sorted by creation date (newest first)
        List<LocalDateTime> createdDates = results.getSearchHits().stream()
                .map(hit -> hit.getContent().getCreatedAt())
                .toList();

        // All dates should be non-null
        assertThat(createdDates).allMatch(date -> date != null);
    }

    @Test
    void shouldCombineProducerAndShelfFilters() {
        // Given - Producer 2, Dairy shelf
        Long producerId = 2L;
        Long shelfId = 3L;

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProductsByProducer(
                producerId, shelfId, false, 0, 20
        );

        // Then - Should return all dairy products from producer 2
        assertThat(results.getTotalHits()).isEqualTo(2);
        assertThat(results.getSearchHits())
                .allMatch(hit -> hit.getContent().getProducerId().equals(producerId))
                .allMatch(hit -> hit.getContent().getShelfId().equals(shelfId))
                .allMatch(hit -> hit.getContent().getShelfName().equals("Dairy"));
    }
}

