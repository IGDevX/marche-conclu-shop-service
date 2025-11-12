package org.igdevx.shopservice.unit.services;

import org.igdevx.shopservice.UnitTest;
import org.igdevx.shopservice.dtos.CurrencyRequest;
import org.igdevx.shopservice.dtos.CurrencyResponse;
import org.igdevx.shopservice.exceptions.DuplicateResourceException;
import org.igdevx.shopservice.exceptions.ResourceNotFoundException;
import org.igdevx.shopservice.mappers.CurrencyMapper;
import org.igdevx.shopservice.models.Currency;
import org.igdevx.shopservice.repositories.CurrencyRepository;
import org.igdevx.shopservice.services.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@UnitTest

@ExtendWith(MockitoExtension.class)
@DisplayName("Currency Service Tests")
class CurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private CurrencyMapper currencyMapper;

    @InjectMocks
    private CurrencyService currencyService;

    private Currency currency;
    private CurrencyRequest currencyRequest;
    private CurrencyResponse currencyResponse;

    @BeforeEach
    void setUp() {
        currency = Currency.builder()
                .id(1L)
                .code("USD")
                .label("US Dollar")
                .usdExchangeRate(new BigDecimal("1.00"))
                .build();

        currencyRequest = CurrencyRequest.builder()
                .code("USD")
                .label("US Dollar")
                .usdExchangeRate(new BigDecimal("1.00"))
                .build();

        currencyResponse = CurrencyResponse.builder()
                .id(1L)
                .code("USD")
                .label("US Dollar")
                .usdExchangeRate(new BigDecimal("1.00"))
                .build();
    }

    @Test
    @DisplayName("Should return all currencies")
    void getAllCurrencies_ShouldReturnAllCurrencies() {
        // Given
        List<Currency> currencies = Arrays.asList(currency);
        when(currencyRepository.findAll()).thenReturn(currencies);
        when(currencyMapper.toResponse(currency)).thenReturn(currencyResponse);

        // When
        List<CurrencyResponse> result = currencyService.getAllCurrencies();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("USD");
        verify(currencyRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return currency by ID when exists")
    void getCurrencyById_WhenExists_ShouldReturnCurrency() {
        // Given
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
        when(currencyMapper.toResponse(currency)).thenReturn(currencyResponse);

        // When
        CurrencyResponse result = currencyService.getCurrencyById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCode()).isEqualTo("USD");
        verify(currencyRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when currency not found by ID")
    void getCurrencyById_WhenNotExists_ShouldThrowException() {
        // Given
        when(currencyRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> currencyService.getCurrencyById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Currency not found with id: 999");
        verify(currencyRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should return currency by code when exists")
    void getCurrencyByCode_WhenExists_ShouldReturnCurrency() {
        // Given
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(currency));
        when(currencyMapper.toResponse(currency)).thenReturn(currencyResponse);

        // When
        CurrencyResponse result = currencyService.getCurrencyByCode("USD");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("USD");
        verify(currencyRepository, times(1)).findByCode("USD");
    }

    @Test
    @DisplayName("Should throw exception when currency not found by code")
    void getCurrencyByCode_WhenNotExists_ShouldThrowException() {
        // Given
        when(currencyRepository.findByCode("XXX")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> currencyService.getCurrencyByCode("XXX"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Currency not found with code: XXX");
        verify(currencyRepository, times(1)).findByCode("XXX");
    }

    @Test
    @DisplayName("Should create currency successfully")
    void createCurrency_WhenCodeNotExists_ShouldCreateCurrency() {
        // Given
        when(currencyRepository.existsByCode("USD")).thenReturn(false);
        when(currencyMapper.toEntity(currencyRequest)).thenReturn(currency);
        when(currencyRepository.save(currency)).thenReturn(currency);
        when(currencyMapper.toResponse(currency)).thenReturn(currencyResponse);

        // When
        CurrencyResponse result = currencyService.createCurrency(currencyRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("USD");
        verify(currencyRepository, times(1)).existsByCode("USD");
        verify(currencyRepository, times(1)).save(currency);
    }

    @Test
    @DisplayName("Should throw exception when creating currency with duplicate code")
    void createCurrency_WhenCodeExists_ShouldThrowException() {
        // Given
        when(currencyRepository.existsByCode("USD")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> currencyService.createCurrency(currencyRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Currency already exists with code: USD");
        verify(currencyRepository, times(1)).existsByCode("USD");
        verify(currencyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update currency successfully when only label changes")
    void updateCurrency_WhenExists_ShouldUpdateCurrency() {
        // Given
        CurrencyRequest updateRequest = CurrencyRequest.builder()
                .code("USD")
                .label("United States Dollar")
                .usdExchangeRate(new BigDecimal("1.00"))
                .build();

        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
        when(currencyRepository.save(currency)).thenReturn(currency);
        when(currencyMapper.toResponse(currency)).thenReturn(currencyResponse);
        doNothing().when(currencyMapper).updateEntity(currency, updateRequest);

        // When
        CurrencyResponse result = currencyService.updateCurrency(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(currencyRepository, times(1)).findById(1L);
        verify(currencyRepository, never()).existsByCode(any()); // Code unchanged, no check needed
        verify(currencyRepository, times(1)).save(currency);
    }

    @Test
    @DisplayName("Should update currency successfully when code changes to non-existing code")
    void updateCurrency_WhenCodeChangesToNonExisting_ShouldUpdateCurrency() {
        // Given
        CurrencyRequest updateRequest = CurrencyRequest.builder()
                .code("EUR")
                .label("Euro")
                .usdExchangeRate(new BigDecimal("0.92"))
                .build();

        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
        when(currencyRepository.existsByCode("EUR")).thenReturn(false);
        when(currencyRepository.save(currency)).thenReturn(currency);
        when(currencyMapper.toResponse(currency)).thenReturn(currencyResponse);
        doNothing().when(currencyMapper).updateEntity(currency, updateRequest);

        // When
        CurrencyResponse result = currencyService.updateCurrency(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(currencyRepository, times(1)).findById(1L);
        verify(currencyRepository, times(1)).existsByCode("EUR");
        verify(currencyRepository, times(1)).save(currency);
    }

    @Test
    @DisplayName("Should throw exception when updating currency with existing code")
    void updateCurrency_WhenCodeChangesToExisting_ShouldThrowException() {
        // Given
        CurrencyRequest updateRequest = CurrencyRequest.builder()
                .code("EUR")
                .label("Euro")
                .usdExchangeRate(new BigDecimal("0.92"))
                .build();

        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
        when(currencyRepository.existsByCode("EUR")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> currencyService.updateCurrency(1L, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Currency already exists with code: EUR");
        verify(currencyRepository, times(1)).findById(1L);
        verify(currencyRepository, times(1)).existsByCode("EUR");
        verify(currencyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent currency")
    void updateCurrency_WhenNotExists_ShouldThrowException() {
        // Given
        when(currencyRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> currencyService.updateCurrency(999L, currencyRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Currency not found with id: 999");
        verify(currencyRepository, times(1)).findById(999L);
        verify(currencyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should soft delete currency successfully")
    void deleteCurrency_WhenExists_ShouldSoftDeleteCurrency() {
        // Given
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
        when(currencyRepository.save(any(Currency.class))).thenReturn(currency);

        // When
        currencyService.deleteCurrency(1L);

        // Then
        verify(currencyRepository, times(1)).findById(1L);
        verify(currencyRepository, times(1)).save(currency);
        assertThat(currency.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when soft deleting non-existent currency")
    void deleteCurrency_WhenNotExists_ShouldThrowException() {
        // Given
        when(currencyRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> currencyService.deleteCurrency(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Currency not found with id: 999");
        verify(currencyRepository, times(1)).findById(999L);
        verify(currencyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should restore deleted currency successfully")
    void restoreCurrency_WhenDeleted_ShouldRestoreCurrency() {
        // Given
        currency.softDelete(); // Mark as deleted
        when(currencyRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(currency));
        when(currencyRepository.save(any(Currency.class))).thenReturn(currency);

        // When
        currencyService.restoreCurrency(1L);

        // Then
        verify(currencyRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(currencyRepository, times(1)).save(currency);
        assertThat(currency.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when restoring non-deleted currency")
    void restoreCurrency_WhenNotDeleted_ShouldThrowException() {
        // Given
        when(currencyRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(currency));

        // When & Then
        assertThatThrownBy(() -> currencyService.restoreCurrency(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Currency with id 1 is not deleted");
        verify(currencyRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(currencyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when restoring non-existent currency")
    void restoreCurrency_WhenNotExists_ShouldThrowException() {
        // Given
        when(currencyRepository.findByIdIncludingDeleted(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> currencyService.restoreCurrency(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Currency not found with id: 999");
        verify(currencyRepository, times(1)).findByIdIncludingDeleted(999L);
        verify(currencyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should hard delete currency successfully")
    void hardDeleteCurrency_WhenExists_ShouldHardDeleteCurrency() {
        // Given
        when(currencyRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(currency));
        doNothing().when(currencyRepository).hardDeleteById(1L);

        // When
        currencyService.hardDeleteCurrency(1L);

        // Then
        verify(currencyRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(currencyRepository, times(1)).hardDeleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when hard deleting non-existent currency")
    void hardDeleteCurrency_WhenNotExists_ShouldThrowException() {
        // Given
        when(currencyRepository.findByIdIncludingDeleted(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> currencyService.hardDeleteCurrency(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Currency not found with id: 999");
        verify(currencyRepository, times(1)).findByIdIncludingDeleted(999L);
        verify(currencyRepository, never()).hardDeleteById(any());
    }

    @Test
    @DisplayName("Should return all deleted currencies")
    void getAllDeletedCurrencies_ShouldReturnDeletedCurrencies() {
        // Given
        currency.softDelete();
        List<Currency> deletedCurrencies = Arrays.asList(currency);
        when(currencyRepository.findAllDeleted()).thenReturn(deletedCurrencies);
        when(currencyMapper.toResponse(currency)).thenReturn(currencyResponse);

        // When
        List<CurrencyResponse> result = currencyService.getAllDeletedCurrencies();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("USD");
        verify(currencyRepository, times(1)).findAllDeleted();
    }
}
