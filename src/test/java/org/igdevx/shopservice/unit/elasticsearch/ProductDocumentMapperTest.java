package org.igdevx.shopservice.unit.elasticsearch;

import org.igdevx.shopservice.UnitTest;
import org.igdevx.shopservice.elasticsearch.documents.ProductDocument;
import org.igdevx.shopservice.elasticsearch.mappers.ProductDocumentMapper;
import org.igdevx.shopservice.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ProductDocumentMapper
 * Tests the mapping logic without Spring context or external dependencies
 */
@UnitTest
@DisplayName("ProductDocumentMapper Unit Tests")
class ProductDocumentMapperTest {

    private ProductDocumentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductDocumentMapper();
    }

    @Nested
    @DisplayName("Mapping Product to ProductDocument")
    class ToDocumentTests {

        @Test
        @DisplayName("Should map all product fields correctly")
        void shouldMapAllFieldsCorrectly() {
            // Given
            Product product = createTestProduct();

            // When
            ProductDocument document = mapper.toDocument(product);

            // Then
            assertThat(document).isNotNull();
            assertThat(document.getId()).isEqualTo(1L);
            assertThat(document.getTitle()).isEqualTo("Bio Tomatoes");
            assertThat(document.getDescription()).isEqualTo("Fresh organic tomatoes");
            assertThat(document.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(5.99));
            assertThat(document.getIsFresh()).isTrue();
            assertThat(document.getProducerId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("Should map currency information correctly")
        void shouldMapCurrencyCorrectly() {
            // Given
            Product product = createTestProduct();

            // When
            ProductDocument document = mapper.toDocument(product);

            // Then
            assertThat(document.getCurrencyId()).isEqualTo(1L);
            assertThat(document.getCurrencyCode()).isEqualTo("EUR");
        }

        @Test
        @DisplayName("Should map unit information correctly")
        void shouldMapUnitCorrectly() {
            // Given
            Product product = createTestProduct();

            // When
            ProductDocument document = mapper.toDocument(product);

            // Then
            assertThat(document.getUnitId()).isEqualTo(2L);
            assertThat(document.getUnitName()).isEqualTo("Kilogram");
        }

        @Test
        @DisplayName("Should map category information correctly")
        void shouldMapCategoryCorrectly() {
            // Given
            Product product = createTestProduct();

            // When
            ProductDocument document = mapper.toDocument(product);

            // Then
            assertThat(document.getCategoryId()).isEqualTo(3L);
            assertThat(document.getCategoryName()).isEqualTo("Vegetables");
        }

        @Test
        @DisplayName("Should map shelf information correctly")
        void shouldMapShelfCorrectly() {
            // Given
            Product product = createTestProduct();

            // When
            ProductDocument document = mapper.toDocument(product);

            // Then
            assertThat(document.getShelfId()).isEqualTo(4L);
            assertThat(document.getShelfName()).isEqualTo("Fresh Produce");
        }

        @Test
        @DisplayName("Should map certifications correctly")
        void shouldMapCertificationsCorrectly() {
            // Given
            Product product = createTestProduct();

            // When
            ProductDocument document = mapper.toDocument(product);

            // Then
            assertThat(document.getCertificationIds())
                .isNotNull()
                .hasSize(2)
                .contains(10L, 11L);
            assertThat(document.getCertificationNames())
                .isNotNull()
                .hasSize(2)
                .contains("Organic", "Local");
        }

        @Test
        @DisplayName("Should handle null category gracefully")
        void shouldHandleNullCategory() {
            // Given
            Product product = createTestProduct();
            product.setCategory(null);

            // When
            ProductDocument document = mapper.toDocument(product);

            // Then
            assertThat(document.getCategoryId()).isNull();
            assertThat(document.getCategoryName()).isNull();
        }

        @Test
        @DisplayName("Should handle null shelf gracefully")
        void shouldHandleNullShelf() {
            // Given
            Product product = createTestProduct();
            product.setShelf(null);

            // When
            ProductDocument document = mapper.toDocument(product);

            // Then
            assertThat(document.getShelfId()).isNull();
            assertThat(document.getShelfName()).isNull();
        }

        @Test
        @DisplayName("Should handle empty certifications")
        void shouldHandleEmptyCertifications() {
            // Given
            Product product = createTestProduct();
            product.setCertifications(Set.of());

            // When
            ProductDocument document = mapper.toDocument(product);

            // Then
            assertThat(document.getCertificationIds()).isEmpty();
            assertThat(document.getCertificationNames()).isEmpty();
        }

        @Test
        @DisplayName("Should handle null certifications")
        void shouldHandleNullCertifications() {
            // Given
            Product product = createTestProduct();
            product.setCertifications(null);

            // When
            ProductDocument document = mapper.toDocument(product);

            // Then
            assertThat(document.getCertificationIds()).isNull();
            assertThat(document.getCertificationNames()).isNull();
        }

        @Test
        @DisplayName("Should map image URLs correctly")
        void shouldMapImageUrls() {
            // Given
            Product product = createTestProduct();

            // When
            ProductDocument document = mapper.toDocument(product);

            // Then
            assertThat(document.getImageUrl()).isEqualTo("http://example.com/tomato.jpg");
            assertThat(document.getImageThumbnailUrl()).isEqualTo("http://example.com/tomato_thumb.jpg");
        }

        @Test
        @DisplayName("Should map timestamps correctly")
        void shouldMapTimestamps() {
            // Given
            Product product = createTestProduct();
            LocalDateTime now = LocalDateTime.now();
            product.setCreatedAt(now);
            product.setUpdatedAt(now);

            // When
            ProductDocument document = mapper.toDocument(product);

            // Then
            assertThat(document.getCreatedAt()).isEqualTo(now);
            assertThat(document.getUpdatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("Should map isDeleted flag correctly")
        void shouldMapIsDeletedFlag() {
            // Given
            Product product = createTestProduct();
            product.setIsDeleted(true);

            // When
            ProductDocument document = mapper.toDocument(product);

            // Then
            assertThat(document.getIsDeleted()).isTrue();
        }
    }

    /**
     * Helper method to create a test product with all fields populated
     */
    private Product createTestProduct() {
        Currency currency = new Currency();
        currency.setId(1L);
        currency.setCode("EUR");
        currency.setLabel("Euro");

        Unit unit = new Unit();
        unit.setId(2L);
        unit.setCode("kg");
        unit.setLabel("Kilogram");

        Category category = new Category();
        category.setId(3L);
        category.setName("Vegetables");

        Shelf shelf = new Shelf();
        shelf.setId(4L);
        shelf.setLabel("Fresh Produce");
        shelf.setProducerId(100L);

        ProductCertification cert1 = new ProductCertification();
        cert1.setId(10L);
        cert1.setLabel("Organic");

        ProductCertification cert2 = new ProductCertification();
        cert2.setId(11L);
        cert2.setLabel("Local");

        Product product = new Product();
        product.setId(1L);
        product.setTitle("Bio Tomatoes");
        product.setDescription("Fresh organic tomatoes");
        product.setPrice(BigDecimal.valueOf(5.99));
        product.setCurrency(currency);
        product.setUnit(unit);
        product.setCategory(category);
        product.setShelf(shelf);
        product.setIsFresh(true);
        product.setProducerId(100L);
        product.setCertifications(Set.of(cert1, cert2));
        product.setImageUrl("http://example.com/tomato.jpg");
        product.setImageThumbnailUrl("http://example.com/tomato_thumb.jpg");
        product.setImageKey("tomato.jpg");
        product.setIsDeleted(false);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        return product;
    }
}

