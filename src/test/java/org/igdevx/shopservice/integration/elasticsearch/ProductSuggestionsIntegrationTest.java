package org.igdevx.shopservice.integration.elasticsearch;

import org.igdevx.shopservice.dtos.ProductSuggestion;
import org.igdevx.shopservice.elasticsearch.documents.ProductDocument;
import org.igdevx.shopservice.elasticsearch.repositories.ProductSearchRepository;
import org.igdevx.shopservice.elasticsearch.services.ProductSearchService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Product Suggestions (Autocomplete)
 * Tests the endpoint /api/products/suggest
 */
class ProductSuggestionsIntegrationTest extends ElasticsearchIntegrationTestBase {

    @Autowired
    private ProductSearchService productSearchService;

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @BeforeEach
    void setUpSuggestionsTestData() {
        productSearchRepository.deleteAll();

        // Create test products with various titles for autocomplete
        ProductDocument product1 = ProductDocument.builder()
                .id(1L)
                .title("Apple iPhone 15")
                .description("Latest iPhone model")
                .price(BigDecimal.valueOf(999.99))
                .currencyCode("EUR")
                .currencyId(1L)
                .unitName("unit")
                .unitId(1L)
                .shelfName("Electronics")
                .shelfId(1L)
                .categoryName("Smartphones")
                .categoryId(1L)
                .certificationNames(Set.of())
                .certificationIds(Set.of())
                .isFresh(false)
                .producerId(1L)
                .mainImageUrl("http://example.com/iphone.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        ProductDocument product2 = ProductDocument.builder()
                .id(2L)
                .title("Apple MacBook Pro")
                .description("Professional laptop")
                .price(BigDecimal.valueOf(2499.99))
                .currencyCode("EUR")
                .currencyId(1L)
                .unitName("unit")
                .unitId(1L)
                .shelfName("Electronics")
                .shelfId(1L)
                .categoryName("Computers")
                .categoryId(2L)
                .certificationNames(Set.of())
                .certificationIds(Set.of())
                .isFresh(false)
                .producerId(1L)
                .mainImageUrl("http://example.com/macbook.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        ProductDocument product3 = ProductDocument.builder()
                .id(3L)
                .title("Apple AirPods Pro")
                .description("Wireless earbuds")
                .price(BigDecimal.valueOf(249.99))
                .currencyCode("EUR")
                .currencyId(1L)
                .unitName("unit")
                .unitId(1L)
                .shelfName("Electronics")
                .shelfId(1L)
                .categoryName("Audio")
                .categoryId(3L)
                .certificationNames(Set.of())
                .certificationIds(Set.of())
                .isFresh(false)
                .producerId(1L)
                .mainImageUrl("http://example.com/airpods.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        ProductDocument product4 = ProductDocument.builder()
                .id(4L)
                .title("Fresh Organic Apples")
                .description("Red apples from local farm")
                .price(BigDecimal.valueOf(3.99))
                .currencyCode("EUR")
                .currencyId(1L)
                .unitName("kg")
                .unitId(2L)
                .shelfName("Fruits")
                .shelfId(2L)
                .categoryName("Fresh Produce")
                .categoryId(4L)
                .certificationNames(Set.of("Organic"))
                .certificationIds(Set.of(1L))
                .isFresh(true)
                .producerId(2L)
                .mainImageUrl("http://example.com/apples.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        ProductDocument product5 = ProductDocument.builder()
                .id(5L)
                .title("Banana Bundle")
                .description("Fresh bananas")
                .price(BigDecimal.valueOf(2.49))
                .currencyCode("EUR")
                .currencyId(1L)
                .unitName("kg")
                .unitId(2L)
                .shelfName("Fruits")
                .shelfId(2L)
                .categoryName("Fresh Produce")
                .categoryId(4L)
                .certificationNames(Set.of("Local"))
                .certificationIds(Set.of(2L))
                .isFresh(true)
                .producerId(2L)
                .mainImageUrl("http://example.com/banana.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        ProductDocument product6 = ProductDocument.builder()
                .id(6L)
                .title("Application Development Book")
                .description("Learn app development")
                .price(BigDecimal.valueOf(29.99))
                .currencyCode("EUR")
                .currencyId(1L)
                .unitName("unit")
                .unitId(1L)
                .shelfName("Books")
                .shelfId(3L)
                .categoryName("Books")
                .categoryId(5L)
                .certificationNames(Set.of())
                .certificationIds(Set.of())
                .isFresh(false)
                .producerId(3L)
                .mainImageUrl("http://example.com/book.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        // Deleted product (should not appear in suggestions)
        ProductDocument product7 = ProductDocument.builder()
                .id(7L)
                .title("Apple Watch Deleted")
                .description("This product is deleted")
                .price(BigDecimal.valueOf(399.99))
                .currencyCode("EUR")
                .currencyId(1L)
                .unitName("unit")
                .unitId(1L)
                .shelfName("Electronics")
                .shelfId(1L)
                .categoryName("Wearables")
                .categoryId(6L)
                .certificationNames(Set.of())
                .certificationIds(Set.of())
                .isFresh(false)
                .producerId(1L)
                .mainImageUrl("http://example.com/watch.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(true)
                .build();

        productSearchRepository.saveAll(List.of(product1, product2, product3, product4, product5, product6, product7));

        // Wait for indexing
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterEach
    void cleanUpSuggestionsData() {
        productSearchRepository.deleteAll();
    }

    @Test
    void shouldReturnSuggestionsForPartialQuery() {
        // Given - User types "app"
        String query = "app";
        int size = 10;

        // When
        List<ProductSuggestion> suggestions = productSearchService.getSuggestions(query, size);

        // Then - Should return all products starting with or containing "app"
        assertThat(suggestions).isNotEmpty();
        assertThat(suggestions)
                .extracting(ProductSuggestion::getTitle)
                .anyMatch(title -> title.toLowerCase().contains("app"));
    }

    @Test
    void shouldReturnLimitedNumberOfSuggestions() {
        // Given - User types "apple" and wants max 2 suggestions
        String query = "apple";
        int size = 2;

        // When
        List<ProductSuggestion> suggestions = productSearchService.getSuggestions(query, size);

        // Then - Should return at most 2 suggestions
        assertThat(suggestions).hasSizeLessThanOrEqualTo(2);
    }

    @Test
    void shouldReturnSuggestionsWithProductDetails() {
        // Given - User types "iphone"
        String query = "iphone";
        int size = 10;

        // When
        List<ProductSuggestion> suggestions = productSearchService.getSuggestions(query, size);

        // Then - Should return suggestions with id, title, and image
        assertThat(suggestions).isNotEmpty();
        ProductSuggestion firstSuggestion = suggestions.get(0);
        assertThat(firstSuggestion.getId()).isNotNull();
        assertThat(firstSuggestion.getTitle()).isNotNull();
        assertThat(firstSuggestion.getImageUrl()).isNotNull();
    }

    @Test
    void shouldReturnMainImageUrlForProduct() {
        // Given - User types "macbook"
        String query = "macbook";
        int size = 10;

        // When
        List<ProductSuggestion> suggestions = productSearchService.getSuggestions(query, size);

        // Then - Should return main image URL
        assertThat(suggestions).isNotEmpty();
        ProductSuggestion macbookSuggestion = suggestions.stream()
                .filter(s -> s.getTitle().contains("MacBook"))
                .findFirst()
                .orElseThrow();

        assertThat(macbookSuggestion.getImageUrl())
                .isNotNull()
                .isEqualTo("http://example.com/macbook.jpg");
    }

    @Test
    void shouldExcludeDeletedProductsFromSuggestions() {
        // Given - User types "apple watch" (deleted product)
        String query = "apple watch";
        int size = 10;

        // When
        List<ProductSuggestion> suggestions = productSearchService.getSuggestions(query, size);

        // Then - Should not include deleted products
        assertThat(suggestions)
                .noneMatch(s -> s.getTitle().contains("Deleted"));
    }

    @Test
    void shouldReturnEmptyListForNullQuery() {
        // Given - User submits null query
        String query = null;
        int size = 10;

        // When
        List<ProductSuggestion> suggestions = productSearchService.getSuggestions(query, size);

        // Then - Should return empty list
        assertThat(suggestions).isEmpty();
    }

    @Test
    void shouldReturnEmptyListForBlankQuery() {
        // Given - User submits blank query
        String query = "   ";
        int size = 10;

        // When
        List<ProductSuggestion> suggestions = productSearchService.getSuggestions(query, size);

        // Then - Should return empty list
        assertThat(suggestions).isEmpty();
    }

    @Test
    void shouldHandleCaseInsensitiveSearch() {
        // Given - User types "APPLE" in uppercase
        String query = "APPLE";
        int size = 10;

        // When
        List<ProductSuggestion> suggestions = productSearchService.getSuggestions(query, size);

        // Then - Should still find "Apple" products
        assertThat(suggestions).isNotEmpty();
        assertThat(suggestions)
                .extracting(ProductSuggestion::getTitle)
                .anyMatch(title -> title.toLowerCase().contains("apple"));
    }

    @Test
    void shouldSupportPrefixMatching() {
        // Given - User types "ban" (prefix of "banana")
        String query = "ban";
        int size = 10;

        // When
        List<ProductSuggestion> suggestions = productSearchService.getSuggestions(query, size);

        // Then - Should find products starting with "ban"
        assertThat(suggestions)
                .extracting(ProductSuggestion::getTitle)
                .anyMatch(title -> title.toLowerCase().startsWith("ban"));
    }

    @Test
    void shouldReturnMultipleSuggestionsForCommonPrefix() {
        // Given - User types "apple" (multiple products contain this)
        String query = "apple";
        int size = 10;

        // When
        List<ProductSuggestion> suggestions = productSearchService.getSuggestions(query, size);

        // Then - Should return Apple products (4 products contain "apple" or start with "app")
        assertThat(suggestions).hasSizeGreaterThanOrEqualTo(2);
        // At least some should contain "apple"
        assertThat(suggestions)
                .extracting(ProductSuggestion::getTitle)
                .anyMatch(title -> title.toLowerCase().contains("apple"));
    }

    @Test
    void shouldRespectSizeLimit() {
        // Given - User types "a" (matches many products) but wants only 3
        String query = "a";
        int size = 3;

        // When
        List<ProductSuggestion> suggestions = productSearchService.getSuggestions(query, size);

        // Then - Should return at most 3 suggestions
        assertThat(suggestions).hasSizeLessThanOrEqualTo(size);
    }

    @Test
    void shouldReturnSuggestionsForSingleLetter() {
        // Given - User types single letter "b"
        String query = "b";
        int size = 10;

        // When
        List<ProductSuggestion> suggestions = productSearchService.getSuggestions(query, size);

        // Then - Should find products containing "b"
        assertThat(suggestions)
                .extracting(ProductSuggestion::getTitle)
                .anyMatch(title -> title.toLowerCase().contains("b"));
    }

    @Test
    void shouldMatchPartialWords() {
        // Given - User types "org" (part of "Organic")
        String query = "org";
        int size = 10;

        // When
        List<ProductSuggestion> suggestions = productSearchService.getSuggestions(query, size);

        // Then - Should find products with "organic" in title
        assertThat(suggestions)
                .extracting(ProductSuggestion::getTitle)
                .anyMatch(title -> title.toLowerCase().contains("org"));
    }

    @Test
    void shouldHandleSpecialCharacters() {
        // Given - User types query with numbers
        String query = "15";
        int size = 10;

        // When
        List<ProductSuggestion> suggestions = productSearchService.getSuggestions(query, size);

        // Then - Should find products with "15" in title (iPhone 15)
        assertThat(suggestions)
                .extracting(ProductSuggestion::getTitle)
                .anyMatch(title -> title.contains("15"));
    }

    @Test
    void shouldReturnRelevantSuggestionsQuickly() {
        // Given - User types "app" - common query
        String query = "app";
        int size = 5;

        // When
        long startTime = System.currentTimeMillis();
        List<ProductSuggestion> suggestions = productSearchService.getSuggestions(query, size);
        long endTime = System.currentTimeMillis();

        // Then - Should return results quickly (under 1 second)
        long executionTime = endTime - startTime;
        assertThat(executionTime).isLessThan(1000); // 1 second max
        assertThat(suggestions).isNotEmpty();
    }
}

