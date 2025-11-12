package org.igdevx.shopservice.unit.services;

import org.igdevx.shopservice.UnitTest;
import org.igdevx.shopservice.dtos.UnitRequest;
import org.igdevx.shopservice.dtos.UnitResponse;
import org.igdevx.shopservice.exceptions.DuplicateResourceException;
import org.igdevx.shopservice.exceptions.ResourceNotFoundException;
import org.igdevx.shopservice.mappers.UnitMapper;
import org.igdevx.shopservice.models.Unit;
import org.igdevx.shopservice.repositories.UnitRepository;
import org.igdevx.shopservice.services.UnitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@UnitTest

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Service Tests")
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private UnitMapper unitMapper;

    @InjectMocks
    private UnitService unitService;

    private Unit unit;
    private UnitRequest unitRequest;
    private UnitResponse unitResponse;

    @BeforeEach
    void setUp() {
        unit = Unit.builder()
                .id(1L)
                .code("kg")
                .label("Kilogram")
                .build();

        unitRequest = UnitRequest.builder()
                .code("kg")
                .label("Kilogram")
                .build();

        unitResponse = UnitResponse.builder()
                .id(1L)
                .code("kg")
                .label("Kilogram")
                .build();
    }

    @Test
    @DisplayName("Should return all units")
    void getAllUnits_ShouldReturnAllUnits() {
        // Given
        List<Unit> units = Arrays.asList(unit);
        when(unitRepository.findAll()).thenReturn(units);
        when(unitMapper.toResponse(unit)).thenReturn(unitResponse);

        // When
        List<UnitResponse> result = unitService.getAllUnits();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("kg");
        verify(unitRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return unit by ID when exists")
    void getUnitById_WhenExists_ShouldReturnUnit() {
        // Given
        when(unitRepository.findById(1L)).thenReturn(Optional.of(unit));
        when(unitMapper.toResponse(unit)).thenReturn(unitResponse);

        // When
        UnitResponse result = unitService.getUnitById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCode()).isEqualTo("kg");
        verify(unitRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when unit not found by ID")
    void getUnitById_WhenNotExists_ShouldThrowException() {
        // Given
        when(unitRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> unitService.getUnitById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Unit not found with id: 999");
        verify(unitRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should return unit by code when exists")
    void getUnitByCode_WhenExists_ShouldReturnUnit() {
        // Given
        when(unitRepository.findByCode("kg")).thenReturn(Optional.of(unit));
        when(unitMapper.toResponse(unit)).thenReturn(unitResponse);

        // When
        UnitResponse result = unitService.getUnitByCode("kg");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("kg");
        verify(unitRepository, times(1)).findByCode("kg");
    }

    @Test
    @DisplayName("Should create unit successfully")
    void createUnit_WhenCodeNotExists_ShouldCreateUnit() {
        // Given
        when(unitRepository.existsByCode("kg")).thenReturn(false);
        when(unitMapper.toEntity(unitRequest)).thenReturn(unit);
        when(unitRepository.save(unit)).thenReturn(unit);
        when(unitMapper.toResponse(unit)).thenReturn(unitResponse);

        // When
        UnitResponse result = unitService.createUnit(unitRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("kg");
        verify(unitRepository, times(1)).existsByCode("kg");
        verify(unitRepository, times(1)).save(unit);
    }

    @Test
    @DisplayName("Should throw exception when creating unit with duplicate code")
    void createUnit_WhenCodeExists_ShouldThrowException() {
        // Given
        when(unitRepository.existsByCode("kg")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> unitService.createUnit(unitRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Unit already exists with code: kg");
        verify(unitRepository, times(1)).existsByCode("kg");
        verify(unitRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update unit successfully when only label changes")
    void updateUnit_WhenExists_ShouldUpdateUnit() {
        // Given
        UnitRequest updateRequest = UnitRequest.builder()
                .code("kg")
                .label("Kilogram Updated")
                .build();

        when(unitRepository.findById(1L)).thenReturn(Optional.of(unit));
        when(unitRepository.save(unit)).thenReturn(unit);
        when(unitMapper.toResponse(unit)).thenReturn(unitResponse);
        doNothing().when(unitMapper).updateEntity(unit, updateRequest);

        // When
        UnitResponse result = unitService.updateUnit(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(unitRepository, times(1)).findById(1L);
        verify(unitRepository, never()).existsByCode(any()); // Code unchanged, no check needed
        verify(unitRepository, times(1)).save(unit);
    }

    @Test
    @DisplayName("Should update unit successfully when code changes to non-existing code")
    void updateUnit_WhenCodeChangesToNonExisting_ShouldUpdateUnit() {
        // Given
        UnitRequest updateRequest = UnitRequest.builder()
                .code("g")
                .label("Gram")
                .build();

        when(unitRepository.findById(1L)).thenReturn(Optional.of(unit));
        when(unitRepository.existsByCode("g")).thenReturn(false);
        when(unitRepository.save(unit)).thenReturn(unit);
        when(unitMapper.toResponse(unit)).thenReturn(unitResponse);
        doNothing().when(unitMapper).updateEntity(unit, updateRequest);

        // When
        UnitResponse result = unitService.updateUnit(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(unitRepository, times(1)).findById(1L);
        verify(unitRepository, times(1)).existsByCode("g");
        verify(unitRepository, times(1)).save(unit);
    }

    @Test
    @DisplayName("Should throw exception when updating unit with existing code")
    void updateUnit_WhenCodeChangesToExisting_ShouldThrowException() {
        // Given
        UnitRequest updateRequest = UnitRequest.builder()
                .code("g")
                .label("Gram")
                .build();

        when(unitRepository.findById(1L)).thenReturn(Optional.of(unit));
        when(unitRepository.existsByCode("g")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> unitService.updateUnit(1L, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Unit already exists with code: g");
        verify(unitRepository, times(1)).findById(1L);
        verify(unitRepository, times(1)).existsByCode("g");
        verify(unitRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent unit")
    void updateUnit_WhenNotExists_ShouldThrowException() {
        // Given
        when(unitRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> unitService.updateUnit(999L, unitRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Unit not found with id: 999");
        verify(unitRepository, times(1)).findById(999L);
        verify(unitRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should soft delete unit successfully")
    void deleteUnit_WhenExists_ShouldSoftDeleteUnit() {
        // Given
        when(unitRepository.findById(1L)).thenReturn(Optional.of(unit));
        when(unitRepository.save(any(Unit.class))).thenReturn(unit);

        // When
        unitService.deleteUnit(1L);

        // Then
        verify(unitRepository, times(1)).findById(1L);
        verify(unitRepository, times(1)).save(unit);
        assertThat(unit.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when soft deleting non-existent unit")
    void deleteUnit_WhenNotExists_ShouldThrowException() {
        // Given
        when(unitRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> unitService.deleteUnit(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Unit not found with id: 999");
        verify(unitRepository, times(1)).findById(999L);
        verify(unitRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should restore deleted unit successfully")
    void restoreUnit_WhenDeleted_ShouldRestoreUnit() {
        // Given
        unit.softDelete(); // Mark as deleted
        when(unitRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(unit));
        when(unitRepository.save(any(Unit.class))).thenReturn(unit);

        // When
        unitService.restoreUnit(1L);

        // Then
        verify(unitRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(unitRepository, times(1)).save(unit);
        assertThat(unit.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when restoring non-deleted unit")
    void restoreUnit_WhenNotDeleted_ShouldThrowException() {
        // Given
        when(unitRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(unit));

        // When & Then
        assertThatThrownBy(() -> unitService.restoreUnit(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Unit with id 1 is not deleted");
        verify(unitRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(unitRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when restoring non-existent unit")
    void restoreUnit_WhenNotExists_ShouldThrowException() {
        // Given
        when(unitRepository.findByIdIncludingDeleted(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> unitService.restoreUnit(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Unit not found with id: 999");
        verify(unitRepository, times(1)).findByIdIncludingDeleted(999L);
        verify(unitRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should hard delete unit successfully")
    void hardDeleteUnit_WhenExists_ShouldHardDeleteUnit() {
        // Given
        when(unitRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(unit));
        doNothing().when(unitRepository).hardDeleteById(1L);

        // When
        unitService.hardDeleteUnit(1L);

        // Then
        verify(unitRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(unitRepository, times(1)).hardDeleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when hard deleting non-existent unit")
    void hardDeleteUnit_WhenNotExists_ShouldThrowException() {
        // Given
        when(unitRepository.findByIdIncludingDeleted(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> unitService.hardDeleteUnit(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Unit not found with id: 999");
        verify(unitRepository, times(1)).findByIdIncludingDeleted(999L);
        verify(unitRepository, never()).hardDeleteById(any());
    }

    @Test
    @DisplayName("Should return all deleted units")
    void getAllDeletedUnits_ShouldReturnDeletedUnits() {
        // Given
        unit.softDelete();
        List<Unit> deletedUnits = Arrays.asList(unit);
        when(unitRepository.findAllDeleted()).thenReturn(deletedUnits);
        when(unitMapper.toResponse(unit)).thenReturn(unitResponse);

        // When
        List<UnitResponse> result = unitService.getAllDeletedUnits();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("kg");
        verify(unitRepository, times(1)).findAllDeleted();
    }
}
