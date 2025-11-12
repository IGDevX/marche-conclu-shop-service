package org.igdevx.shopservice.unit.services;

import org.igdevx.shopservice.UnitTest;
import org.igdevx.shopservice.dtos.ShelfRequest;
import org.igdevx.shopservice.dtos.ShelfResponse;
import org.igdevx.shopservice.exceptions.DuplicateResourceException;
import org.igdevx.shopservice.exceptions.ResourceNotFoundException;
import org.igdevx.shopservice.mappers.ShelfMapper;
import org.igdevx.shopservice.models.Shelf;
import org.igdevx.shopservice.repositories.ShelfRepository;
import org.igdevx.shopservice.services.ShelfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@UnitTest

@ExtendWith(MockitoExtension.class)
@DisplayName("Shelf Service Tests")
class ShelfServiceTest {

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ShelfMapper shelfMapper;

    @InjectMocks
    private ShelfService shelfService;

    private Shelf shelf;
    private ShelfRequest shelfRequest;
    private ShelfResponse shelfResponse;

    @BeforeEach
    void setUp() {
        shelf = Shelf.builder()
                .id(1L)
                .label("Fruits")
                .producerId(1L)
                .build();

        shelfRequest = ShelfRequest.builder()
                .label("Fruits")
                .producerId(1L)
                .build();

        shelfResponse = ShelfResponse.builder()
                .id(1L)
                .label("Fruits")
                .producerId(1L)
                .build();
    }

    @Test
    @DisplayName("Should return all categories")
    void getAllShelves_ShouldReturnAllCategories() {
        // Given
        List<Shelf> categories = Collections.singletonList(shelf);
        when(shelfRepository.findAll()).thenReturn(categories);
        when(shelfMapper.toResponse(shelf)).thenReturn(shelfResponse);

        // When
        List<ShelfResponse> result = shelfService.getAllShelves();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLabel()).isEqualTo("Fruits");
        verify(shelfRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return shelf by ID when exists")
    void getShelfById_WhenExists_ShouldReturnCategory() {
        // Given
        when(shelfRepository.findById(1L)).thenReturn(Optional.of(shelf));
        when(shelfMapper.toResponse(shelf)).thenReturn(shelfResponse);

        // When
        ShelfResponse result = shelfService.getShelfById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLabel()).isEqualTo("Fruits");
        verify(shelfRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when shelf not found by ID")
    void getShelfById_WhenNotExists_ShouldThrowException() {
        // Given
        when(shelfRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> shelfService.getShelfById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Shelf not found with id: 999");
        verify(shelfRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create shelf successfully")
    void createShelf_WhenLabelNotExists_ShouldCreateCategory() {
        // Given
        when(shelfRepository.existsByProducerIdAndLabel(1L, "Fruits")).thenReturn(false);
        when(shelfMapper.toEntity(shelfRequest)).thenReturn(shelf);
        when(shelfRepository.save(shelf)).thenReturn(shelf);
        when(shelfMapper.toResponse(shelf)).thenReturn(shelfResponse);

        // When
        ShelfResponse result = shelfService.createShelf(shelfRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLabel()).isEqualTo("Fruits");
        verify(shelfRepository, times(1)).existsByProducerIdAndLabel(1L, "Fruits");
        verify(shelfRepository, times(1)).save(shelf);
    }

    @Test
    @DisplayName("Should throw exception when creating shelf with duplicate label for producer")
    void createShelf_WhenLabelExists_ShouldThrowException() {
        // Given
        when(shelfRepository.existsByProducerIdAndLabel(1L, "Fruits")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> shelfService.createShelf(shelfRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Shelf already exists with label: Fruits for this producer");
        verify(shelfRepository, times(1)).existsByProducerIdAndLabel(1L, "Fruits");
        verify(shelfRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update shelf successfully when only label changes")
    void updateShelf_WhenExists_ShouldUpdateCategory() {
        // Given
        ShelfRequest updateRequest = ShelfRequest.builder()
                .label("Fruits Updated")
                .producerId(1L)
                .build();

        when(shelfRepository.findById(1L)).thenReturn(Optional.of(shelf));
        when(shelfRepository.existsByProducerIdAndLabel(1L, "Fruits Updated")).thenReturn(false);
        when(shelfRepository.save(shelf)).thenReturn(shelf);
        when(shelfMapper.toResponse(shelf)).thenReturn(shelfResponse);
        doNothing().when(shelfMapper).updateEntity(shelf, updateRequest);

        // When
        ShelfResponse result = shelfService.updateShelf(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(shelfRepository, times(1)).findById(1L);
        verify(shelfRepository, times(1)).existsByProducerIdAndLabel(1L, "Fruits Updated"); // Label changed, check needed
        verify(shelfRepository, times(1)).save(shelf);
    }

    @Test
    @DisplayName("Should update shelf successfully when label changes to non-existing label")
    void updateShelf_WhenLabelChangesToNonExisting_ShouldUpdateCategory() {
        // Given
        ShelfRequest updateRequest = ShelfRequest.builder()
                .label("Vegetables")
                .producerId(1L)
                .build();

        when(shelfRepository.findById(1L)).thenReturn(Optional.of(shelf));
        when(shelfRepository.existsByProducerIdAndLabel(1L, "Vegetables")).thenReturn(false);
        when(shelfRepository.save(shelf)).thenReturn(shelf);
        when(shelfMapper.toResponse(shelf)).thenReturn(shelfResponse);
        doNothing().when(shelfMapper).updateEntity(shelf, updateRequest);

        // When
        ShelfResponse result = shelfService.updateShelf(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(shelfRepository, times(1)).findById(1L);
        verify(shelfRepository, times(1)).existsByProducerIdAndLabel(1L, "Vegetables");
        verify(shelfRepository, times(1)).save(shelf);
    }

    @Test
    @DisplayName("Should throw exception when updating shelf with existing label for producer")
    void updateShelf_WhenLabelChangesToExisting_ShouldThrowException() {
        // Given
        ShelfRequest updateRequest = ShelfRequest.builder()
                .label("Vegetables")
                .producerId(1L)
                .build();

        when(shelfRepository.findById(1L)).thenReturn(Optional.of(shelf));
        when(shelfRepository.existsByProducerIdAndLabel(1L, "Vegetables")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> shelfService.updateShelf(1L, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Shelf already exists with label: Vegetables for this producer");
        verify(shelfRepository, times(1)).findById(1L);
        verify(shelfRepository, times(1)).existsByProducerIdAndLabel(1L, "Vegetables");
        verify(shelfRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent category")
    void updateShelf_WhenNotExists_ShouldThrowException() {
        // Given
        when(shelfRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> shelfService.updateShelf(999L, shelfRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Shelf not found with id: 999");
        verify(shelfRepository, times(1)).findById(999L);
        verify(shelfRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should soft delete shelf successfully")
    void deleteShelf_WhenExists_ShouldSoftDeleteCategory() {
        // Given
        when(shelfRepository.findById(1L)).thenReturn(Optional.of(shelf));
        when(shelfRepository.save(any(Shelf.class))).thenReturn(shelf);

        // When
        shelfService.deleteShelf(1L);

        // Then
        verify(shelfRepository, times(1)).findById(1L);
        verify(shelfRepository, times(1)).save(shelf);
        assertThat(shelf.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when soft deleting non-existent category")
    void deleteShelf_WhenNotExists_ShouldThrowException() {
        // Given
        when(shelfRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> shelfService.deleteShelf(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Shelf not found with id: 999");
        verify(shelfRepository, times(1)).findById(999L);
        verify(shelfRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should restore deleted shelf successfully")
    void restoreShelf_WhenDeleted_ShouldRestoreCategory() {
        // Given
        shelf.softDelete(); // Mark as deleted
        when(shelfRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(shelf));
        when(shelfRepository.save(any(Shelf.class))).thenReturn(shelf);

        // When
        shelfService.restoreShelf(1L);

        // Then
        verify(shelfRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(shelfRepository, times(1)).save(shelf);
        assertThat(shelf.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when restoring non-deleted category")
    void restoreShelf_WhenNotDeleted_ShouldThrowException() {
        // Given
        when(shelfRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(shelf));

        // When & Then
        assertThatThrownBy(() -> shelfService.restoreShelf(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Shelf with id 1 is not deleted");
        verify(shelfRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(shelfRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when restoring non-existent category")
    void restoreShelf_WhenNotExists_ShouldThrowException() {
        // Given
        when(shelfRepository.findByIdIncludingDeleted(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> shelfService.restoreShelf(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Shelf not found with id: 999");
        verify(shelfRepository, times(1)).findByIdIncludingDeleted(999L);
        verify(shelfRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should hard delete shelf successfully")
    void hardDeleteShelf_WhenExists_ShouldHardDeleteCategory() {
        // Given
        when(shelfRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(shelf));
        doNothing().when(shelfRepository).hardDeleteById(1L);

        // When
        shelfService.hardDeleteShelf(1L);

        // Then
        verify(shelfRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(shelfRepository, times(1)).hardDeleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when hard deleting non-existent category")
    void hardDeleteShelf_WhenNotExists_ShouldThrowException() {
        // Given
        when(shelfRepository.findByIdIncludingDeleted(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> shelfService.hardDeleteShelf(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Shelf not found with id: 999");
        verify(shelfRepository, times(1)).findByIdIncludingDeleted(999L);
        verify(shelfRepository, never()).hardDeleteById(any());
    }

    @Test
    @DisplayName("Should return all deleted categories")
    void getAllDeletedShelves_ShouldReturnDeletedCategories() {
        // Given
        shelf.softDelete();
        List<Shelf> deletedCategories = Collections.singletonList(shelf);
        when(shelfRepository.findAllDeleted()).thenReturn(deletedCategories);
        when(shelfMapper.toResponse(shelf)).thenReturn(shelfResponse);

        // When
        List<ShelfResponse> result = shelfService.getAllDeletedShelves();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLabel()).isEqualTo("Fruits");
        verify(shelfRepository, times(1)).findAllDeleted();
    }
}
