package org.igdevx.shopservice.services;

import org.igdevx.shopservice.dtos.ProductRequest;
import org.igdevx.shopservice.dtos.ProductResponse;
import org.igdevx.shopservice.exceptions.ResourceNotFoundException;
import org.igdevx.shopservice.mappers.ProductMapper;
import org.igdevx.shopservice.models.*;
import org.igdevx.shopservice.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ProductCategoryRepository categoryRepository;

    @Mock
    private ProductCertificationRepository certificationRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ImageStorageService imageStorageService;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;
    private org.igdevx.shopservice.models.Currency testCurrency;
    private Unit unit;
    private ProductCategory category;
    private ProductCertification certification;

    @BeforeEach
    void setUp() {
        testCurrency = org.igdevx.shopservice.models.Currency.builder()
                .id(1L)
                .code("USD")
                .label("US Dollar")
                .usdExchangeRate(new BigDecimal("1.00"))
                .build();

        unit = Unit.builder()
                .id(1L)
                .code("kg")
                .label("Kilogram")
                .build();

        category = ProductCategory.builder()
                .id(1L)
                .label("Fruits")
                .slug("fruits")
                .build();

        certification = ProductCertification.builder()
                .id(1L)
                .label("Organic")
                .build();

        product = Product.builder()
                .id(1L)
                .title("Organic Bananas")
                .description("Fresh organic bananas")
                .price(new BigDecimal("2.50"))
                .currency(testCurrency)
                .unit(unit)
                .category(category)
                .certifications(new HashSet<>(Arrays.asList(certification)))
                .isFresh(true)
                .isAvailable(true)
                .build();

        productRequest = ProductRequest.builder()
                .title("Organic Bananas")
                .description("Fresh organic bananas")
                .price(new BigDecimal("2.50"))
                .currencyId(1L)
                .unitId(1L)
                .categoryId(1L)
                .certificationIds(new HashSet<>(Arrays.asList(1L)))
                .isFresh(true)
                .isAvailable(true)
                .build();

        productResponse = ProductResponse.builder()
                .id(1L)
                .title("Organic Bananas")
                .price(new BigDecimal("2.50"))
                .isFresh(true)
                .isAvailable(true)
                .build();
    }

    @Test
    @DisplayName("Should return all products")
    void getAllProducts_ShouldReturnAllProducts() {
        // Given
        List<Product> products = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        // When
        List<ProductResponse> result = productService.getAllProducts();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Organic Bananas");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return product by ID when exists")
    void getProductById_WhenExists_ShouldReturnProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        // When
        ProductResponse result = productService.getProductById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void getProductById_WhenNotExists_ShouldThrowException() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should return all available products")
    void getAllAvailableProducts_ShouldReturnOnlyAvailable() {
        // Given
        List<Product> products = Arrays.asList(product);
        when(productRepository.findAllAvailable()).thenReturn(products);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        // When
        List<ProductResponse> result = productService.getAllAvailableProducts();

        // Then
        assertThat(result).hasSize(1);
        verify(productRepository, times(1)).findAllAvailable();
    }

    @Test
    @DisplayName("Should return all fresh products")
    void getAllFreshProducts_ShouldReturnOnlyFresh() {
        // Given
        List<Product> products = Arrays.asList(product);
        when(productRepository.findAllFresh()).thenReturn(products);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        // When
        List<ProductResponse> result = productService.getAllFreshProducts();

        // Then
        assertThat(result).hasSize(1);
        verify(productRepository, times(1)).findAllFresh();
    }

    @Test
    @DisplayName("Should return products by category")
    void getProductsByCategory_ShouldReturnProductsInCategory() {
        // Given
        List<Product> products = Arrays.asList(product);
        when(productRepository.findByCategoryId(1L)).thenReturn(products);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        // When
        List<ProductResponse> result = productService.getProductsByCategory(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(productRepository, times(1)).findByCategoryId(1L);
    }

    @Test
    @DisplayName("Should search products by title")
    void searchProducts_ShouldReturnMatchingProducts() {
        // Given
        List<Product> products = Arrays.asList(product);
        when(productRepository.searchByTitle("banana")).thenReturn(products);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        // When
        List<ProductResponse> result = productService.searchProducts("banana");

        // Then
        assertThat(result).hasSize(1);
        verify(productRepository, times(1)).searchByTitle("banana");
    }

    @Test
    @DisplayName("Should create product successfully")
    void createProduct_WithValidData_ShouldCreateProduct() {
        // Given
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(testCurrency));
        when(unitRepository.findById(1L)).thenReturn(Optional.of(unit));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(certificationRepository.findById(1L)).thenReturn(Optional.of(certification));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        // When
        ProductResponse result = productService.createProduct(productRequest);

        // Then
        assertThat(result).isNotNull();
        verify(productRepository, times(1)).save(any(Product.class));
        verify(currencyRepository, times(1)).findById(1L);
        verify(unitRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(certificationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when currency not found")
    void createProduct_WhenCurrencyNotFound_ShouldThrowException() {
        // Given
        when(currencyRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.createProduct(productRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Currency not found with id: 1");
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should upload product image successfully")
    void uploadProductImage_WithValidImage_ShouldUploadAndUpdateProduct() throws Exception {
        // Given
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "banana.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String imageKey = "products/uuid-123.jpg";
        String imageUrl = "http://localhost:9000/product-images/" + imageKey;

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(imageStorageService.uploadImage(imageFile)).thenReturn(imageKey);
        when(imageStorageService.getImageUrl(imageKey)).thenReturn(imageUrl);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        // When
        ProductResponse result = productService.uploadProductImage(1L, imageFile);

        // Then
        assertThat(result).isNotNull();
        verify(imageStorageService, times(1)).uploadImage(imageFile);
        verify(productRepository, times(1)).save(product);
        assertThat(product.getImageKey()).isEqualTo(imageKey);
        assertThat(product.getImageUrl()).isEqualTo(imageUrl);
    }

    @Test
    @DisplayName("Should delete old image when uploading new one")
    void uploadProductImage_WithExistingImage_ShouldDeleteOldImage() throws Exception {
        // Given
        product.setImageKey("products/old-image.jpg");
        MockMultipartFile newImageFile = new MockMultipartFile(
                "image",
                "new-banana.jpg",
                "image/jpeg",
                "new image content".getBytes()
        );

        String newImageKey = "products/uuid-456.jpg";
        String newImageUrl = "http://localhost:9000/product-images/" + newImageKey;

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(imageStorageService).deleteImage("products/old-image.jpg");
        when(imageStorageService.uploadImage(newImageFile)).thenReturn(newImageKey);
        when(imageStorageService.getImageUrl(newImageKey)).thenReturn(newImageUrl);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        // When
        productService.uploadProductImage(1L, newImageFile);

        // Then
        verify(imageStorageService, times(1)).deleteImage("products/old-image.jpg");
        verify(imageStorageService, times(1)).uploadImage(newImageFile);
    }

    @Test
    @DisplayName("Should update product successfully")
    void updateProduct_WhenExists_ShouldUpdateProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(testCurrency));
        when(unitRepository.findById(1L)).thenReturn(Optional.of(unit));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(certificationRepository.findById(1L)).thenReturn(Optional.of(certification));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        doNothing().when(productMapper).updateBasicFields(product, productRequest);

        // When
        ProductResponse result = productService.updateProduct(1L, productRequest);

        // Then
        assertThat(result).isNotNull();
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Should soft delete product successfully")
    void deleteProduct_WhenExists_ShouldSoftDeleteProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);
        assertThat(product.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("Should restore deleted product successfully")
    void restoreProduct_WhenDeleted_ShouldRestoreProduct() {
        // Given
        product.softDelete();
        when(productRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // When
        productService.restoreProduct(1L);

        // Then
        verify(productRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(productRepository, times(1)).save(product);
        assertThat(product.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when restoring non-deleted product")
    void restoreProduct_WhenNotDeleted_ShouldThrowException() {
        // Given
        when(productRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(product));

        // When & Then
        assertThatThrownBy(() -> productService.restoreProduct(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Product with id 1 is not deleted");
    }

    @Test
    @DisplayName("Should hard delete product and its image")
    void hardDeleteProduct_WithImage_ShouldDeleteProductAndImage() {
        // Given
        product.setImageKey("products/test-image.jpg");
        when(productRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(product));
        doNothing().when(imageStorageService).deleteImage("products/test-image.jpg");
        doNothing().when(productRepository).hardDeleteById(1L);

        // When
        productService.hardDeleteProduct(1L);

        // Then
        verify(productRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(imageStorageService, times(1)).deleteImage("products/test-image.jpg");
        verify(productRepository, times(1)).hardDeleteById(1L);
    }

    @Test
    @DisplayName("Should return all deleted products")
    void getAllDeletedProducts_ShouldReturnDeletedProducts() {
        // Given
        product.softDelete();
        List<Product> deletedProducts = Arrays.asList(product);
        when(productRepository.findAllDeleted()).thenReturn(deletedProducts);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        // When
        List<ProductResponse> result = productService.getAllDeletedProducts();

        // Then
        assertThat(result).hasSize(1);
        verify(productRepository, times(1)).findAllDeleted();
    }
}
