package org.igdevx.shopservice.integration.elasticsearch;

import org.igdevx.shopservice.dtos.ProductSearchRequest;
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
 * Integration tests for ProductSearchService
 */
class ProductSearchServiceIntegrationTest extends ElasticsearchIntegrationTestBase {

    @Autowired
    private ProductSearchService productSearchService;

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @BeforeEach
    void setUpSearchTestData() {
        productSearchRepository.deleteAll();

        // Create test products
        ProductDocument product1 = ProductDocument.builder()
                .id(1L)
                .title("Fresh Organic Tomatoes")
                .description("Delicious red tomatoes")
                .price(BigDecimal.valueOf(5.99))
                .currencyCode("EUR")
                .currencyId(1L)
                .unitName("kg")
                .unitId(1L)
                .shelfName("Vegetables")
                .shelfId(1L)
                .categoryName("Fresh Produce")
                .categoryId(1L)
                .certificationNames(Set.of("Organic", "Local"))
                .certificationIds(Set.of(1L, 2L))
                .isFresh(true)
                .producerId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        ProductDocument product2 = ProductDocument.builder()
                .id(2L)
                .title("Carrots Bundle")
                .description("Fresh carrots from local farm")
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
                .title("Canned Tomatoes")
                .description("Premium canned tomatoes")
                .price(BigDecimal.valueOf(2.99))
                .currencyCode("EUR")
                .currencyId(1L)
                .unitName("unit")
                .unitId(2L)
                .shelfName("Pantry")
                .shelfId(2L)
                .categoryName("Canned Goods")
                .categoryId(2L)
                .certificationNames(Set.of())
                .certificationIds(Set.of())
                .isFresh(false)
                .producerId(2L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        ProductDocument product4 = ProductDocument.builder()
                .id(4L)
                .title("Deleted Product")
                .description("This product is deleted")
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

        productSearchRepository.saveAll(List.of(product1, product2, product3, product4));

        // Wait for indexing
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterEach
    void cleanUpSearchData() {
        productSearchRepository.deleteAll();
    }

    @Test
    void shouldSearchProductsByTitle() {
        // Given
        ProductSearchRequest request = ProductSearchRequest.builder()
                .q("tomatoes")
                .build();

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProducts(request);

        // Then
        assertThat(results.getTotalHits()).isEqualTo(2); // Fresh and Canned tomatoes
        assertThat(results.getSearchHits())
                .extracting(hit -> hit.getContent().getTitle())
                .containsAnyOf("Fresh Organic Tomatoes", "Canned Tomatoes");
    }

    @Test
    void shouldFilterByCategory() {
        // Given
        ProductSearchRequest request = ProductSearchRequest.builder()
                .categoryIds(Set.of(1L)) // Fresh Produce
                .build();

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProducts(request);

        // Then
        assertThat(results.getTotalHits()).isEqualTo(2); // Tomatoes and Carrots
    }

    @Test
    void shouldFilterByPriceRange() {
        // Given
        ProductSearchRequest request = ProductSearchRequest.builder()
                .priceMin(BigDecimal.valueOf(3.00))
                .priceMax(BigDecimal.valueOf(6.00))
                .build();

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProducts(request);

        // Then
        assertThat(results.getTotalHits()).isEqualTo(2); // Tomatoes (5.99) and Carrots (3.50)
    }

    @Test
    void shouldFilterFreshProducts() {
        // Given
        ProductSearchRequest request = ProductSearchRequest.builder()
                .fresh(true)
                .build();

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProducts(request);

        // Then
        assertThat(results.getTotalHits()).isEqualTo(2); // Fresh tomatoes and carrots
    }

    @Test
    void shouldFilterByCertification() {
        // Given
        ProductSearchRequest request = ProductSearchRequest.builder()
                .certificationIds(Set.of(1L)) // Organic
                .build();

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProducts(request);

        // Then
        assertThat(results.getTotalHits()).isEqualTo(1); // Only organic tomatoes
    }


    @Test
    void shouldExcludeDeletedProductsByDefault() {
        // Given
        ProductSearchRequest request = ProductSearchRequest.builder()
                .q("product")
                .build();

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProducts(request);

        // Then - Should not include the deleted product
        assertThat(results.getSearchHits())
                .noneMatch(hit -> hit.getContent().getIsDeleted());
    }

    @Test
    void shouldIncludeDeletedProductsWhenRequested() {
        // Given
        ProductSearchRequest request = ProductSearchRequest.builder()
                .q("deleted")
                .onlyDeleted(true)
                .build();

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProducts(request);

        // Then
        assertThat(results.getTotalHits()).isEqualTo(1);
        assertThat(results.getSearchHits().get(0).getContent().getTitle()).isEqualTo("Deleted Product");
    }

    @Test
    void shouldSortByPriceAscending() {
        // Given
        ProductSearchRequest request = ProductSearchRequest.builder()
                .sort("price_asc")
                .build();

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProducts(request);

        // Then
        List<BigDecimal> prices = results.getSearchHits().stream()
                .map(hit -> hit.getContent().getPrice())
                .toList();

        assertThat(prices).isSorted();
    }

    @Test
    void shouldSortByPriceDescending() {
        // Given
        ProductSearchRequest request = ProductSearchRequest.builder()
                .sort("price_desc")
                .build();

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProducts(request);

        // Then
        List<BigDecimal> prices = results.getSearchHits().stream()
                .map(hit -> hit.getContent().getPrice())
                .toList();

        assertThat(prices).isSortedAccordingTo((a, b) -> b.compareTo(a));
    }

    @Test
    void shouldPaginateResults() {
        // Given
        ProductSearchRequest request = ProductSearchRequest.builder()
                .page(0)
                .size(2)
                .build();

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProducts(request);

        // Then
        assertThat(results.getSearchHits()).hasSize(2);
    }

    @Test
    void shouldCombineMultipleFilters() {
        // Given - Search for fresh products in category 1 with price range
        ProductSearchRequest request = ProductSearchRequest.builder()
                .q("tomatoes")
                .categoryIds(Set.of(1L))
                .fresh(true)
                .priceMin(BigDecimal.valueOf(5.00))
                .priceMax(BigDecimal.valueOf(10.00))
                .build();

        // When
        SearchHits<ProductDocument> results = productSearchService.searchProducts(request);

        // Then
        assertThat(results.getTotalHits()).isEqualTo(1); // Only fresh organic tomatoes
        assertThat(results.getSearchHits().get(0).getContent().getTitle())
                .isEqualTo("Fresh Organic Tomatoes");
    }
}

