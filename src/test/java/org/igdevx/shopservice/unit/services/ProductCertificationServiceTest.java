package org.igdevx.shopservice.unit.services;

import org.igdevx.shopservice.UnitTest;
import org.igdevx.shopservice.dtos.ProductCertificationRequest;
import org.igdevx.shopservice.dtos.ProductCertificationResponse;
import org.igdevx.shopservice.exceptions.DuplicateResourceException;
import org.igdevx.shopservice.exceptions.ResourceNotFoundException;
import org.igdevx.shopservice.mappers.ProductCertificationMapper;
import org.igdevx.shopservice.models.ProductCertification;
import org.igdevx.shopservice.repositories.ProductCertificationRepository;
import org.igdevx.shopservice.services.ProductCertificationService;
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
@DisplayName("ProductCertification Service Tests")
class ProductCertificationServiceTest {

    @Mock
    private ProductCertificationRepository certificationRepository;

    @Mock
    private ProductCertificationMapper certificationMapper;

    @InjectMocks
    private ProductCertificationService certificationService;

    private ProductCertification certification;
    private ProductCertificationRequest certificationRequest;
    private ProductCertificationResponse certificationResponse;

    @BeforeEach
    void setUp() {
        certification = ProductCertification.builder()
                .id(1L)
                .label("Organic")
                .build();

        certificationRequest = ProductCertificationRequest.builder()
                .label("Organic")
                .build();

        certificationResponse = ProductCertificationResponse.builder()
                .id(1L)
                .label("Organic")
                .build();
    }

    @Test
    @DisplayName("Should return all certifications")
    void getAllCertifications_ShouldReturnAllCertifications() {
        // Given
        List<ProductCertification> certifications = Arrays.asList(certification);
        when(certificationRepository.findAll()).thenReturn(certifications);
        when(certificationMapper.toResponse(certification)).thenReturn(certificationResponse);

        // When
        List<ProductCertificationResponse> result = certificationService.getAllCertifications();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLabel()).isEqualTo("Organic");
        verify(certificationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return certification by ID when exists")
    void getCertificationById_WhenExists_ShouldReturnCertification() {
        // Given
        when(certificationRepository.findById(1L)).thenReturn(Optional.of(certification));
        when(certificationMapper.toResponse(certification)).thenReturn(certificationResponse);

        // When
        ProductCertificationResponse result = certificationService.getCertificationById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLabel()).isEqualTo("Organic");
        verify(certificationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when certification not found by ID")
    void getCertificationById_WhenNotExists_ShouldThrowException() {
        // Given
        when(certificationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> certificationService.getCertificationById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product certification not found with id: 999");
        verify(certificationRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should return certification by label when exists")
    void getCertificationByLabel_WhenExists_ShouldReturnCertification() {
        // Given
        when(certificationRepository.findByLabel("Organic")).thenReturn(Optional.of(certification));
        when(certificationMapper.toResponse(certification)).thenReturn(certificationResponse);

        // When
        ProductCertificationResponse result = certificationService.getCertificationByLabel("Organic");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLabel()).isEqualTo("Organic");
        verify(certificationRepository, times(1)).findByLabel("Organic");
    }

    @Test
    @DisplayName("Should throw exception when certification not found by label")
    void getCertificationByLabel_WhenNotExists_ShouldThrowException() {
        // Given
        when(certificationRepository.findByLabel("Unknown")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> certificationService.getCertificationByLabel("Unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product certification not found with label: Unknown");
        verify(certificationRepository, times(1)).findByLabel("Unknown");
    }

    @Test
    @DisplayName("Should create certification successfully")
    void createCertification_WhenLabelNotExists_ShouldCreateCertification() {
        // Given
        when(certificationRepository.existsByLabel("Organic")).thenReturn(false);
        when(certificationMapper.toEntity(certificationRequest)).thenReturn(certification);
        when(certificationRepository.save(certification)).thenReturn(certification);
        when(certificationMapper.toResponse(certification)).thenReturn(certificationResponse);

        // When
        ProductCertificationResponse result = certificationService.createCertification(certificationRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLabel()).isEqualTo("Organic");
        verify(certificationRepository, times(1)).existsByLabel("Organic");
        verify(certificationRepository, times(1)).save(certification);
    }

    @Test
    @DisplayName("Should throw exception when creating certification with duplicate label")
    void createCertification_WhenLabelExists_ShouldThrowException() {
        // Given
        when(certificationRepository.existsByLabel("Organic")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> certificationService.createCertification(certificationRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Product certification already exists with label: Organic");
        verify(certificationRepository, times(1)).existsByLabel("Organic");
        verify(certificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update certification successfully when label stays same")
    void updateCertification_WhenExists_ShouldUpdateCertification() {
        // Given
        ProductCertificationRequest updateRequest = ProductCertificationRequest.builder()
                .label("Organic")
                .build();

        when(certificationRepository.findById(1L)).thenReturn(Optional.of(certification));
        when(certificationRepository.save(certification)).thenReturn(certification);
        when(certificationMapper.toResponse(certification)).thenReturn(certificationResponse);
        doNothing().when(certificationMapper).updateEntity(certification, updateRequest);

        // When
        ProductCertificationResponse result = certificationService.updateCertification(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(certificationRepository, times(1)).findById(1L);
        verify(certificationRepository, never()).existsByLabel(any()); // Label unchanged, no check needed
        verify(certificationRepository, times(1)).save(certification);
    }

    @Test
    @DisplayName("Should update certification successfully when label changes to non-existing label")
    void updateCertification_WhenLabelChangesToNonExisting_ShouldUpdateCertification() {
        // Given
        ProductCertificationRequest updateRequest = ProductCertificationRequest.builder()
                .label("Fair Trade")
                .build();

        when(certificationRepository.findById(1L)).thenReturn(Optional.of(certification));
        when(certificationRepository.existsByLabel("Fair Trade")).thenReturn(false);
        when(certificationRepository.save(certification)).thenReturn(certification);
        when(certificationMapper.toResponse(certification)).thenReturn(certificationResponse);
        doNothing().when(certificationMapper).updateEntity(certification, updateRequest);

        // When
        ProductCertificationResponse result = certificationService.updateCertification(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(certificationRepository, times(1)).findById(1L);
        verify(certificationRepository, times(1)).existsByLabel("Fair Trade");
        verify(certificationRepository, times(1)).save(certification);
    }

    @Test
    @DisplayName("Should throw exception when updating certification with existing label")
    void updateCertification_WhenLabelChangesToExisting_ShouldThrowException() {
        // Given
        ProductCertificationRequest updateRequest = ProductCertificationRequest.builder()
                .label("Fair Trade")
                .build();

        when(certificationRepository.findById(1L)).thenReturn(Optional.of(certification));
        when(certificationRepository.existsByLabel("Fair Trade")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> certificationService.updateCertification(1L, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Product certification already exists with label: Fair Trade");
        verify(certificationRepository, times(1)).findById(1L);
        verify(certificationRepository, times(1)).existsByLabel("Fair Trade");
        verify(certificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent certification")
    void updateCertification_WhenNotExists_ShouldThrowException() {
        // Given
        when(certificationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> certificationService.updateCertification(999L, certificationRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product certification not found with id: 999");
        verify(certificationRepository, times(1)).findById(999L);
        verify(certificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should soft delete certification successfully")
    void deleteCertification_WhenExists_ShouldSoftDeleteCertification() {
        // Given
        when(certificationRepository.findById(1L)).thenReturn(Optional.of(certification));
        when(certificationRepository.save(any(ProductCertification.class))).thenReturn(certification);

        // When
        certificationService.deleteCertification(1L);

        // Then
        verify(certificationRepository, times(1)).findById(1L);
        verify(certificationRepository, times(1)).save(certification);
        assertThat(certification.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when soft deleting non-existent certification")
    void deleteCertification_WhenNotExists_ShouldThrowException() {
        // Given
        when(certificationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> certificationService.deleteCertification(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product certification not found with id: 999");
        verify(certificationRepository, times(1)).findById(999L);
        verify(certificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should restore deleted certification successfully")
    void restoreCertification_WhenDeleted_ShouldRestoreCertification() {
        // Given
        certification.softDelete(); // Mark as deleted
        when(certificationRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(certification));
        when(certificationRepository.save(any(ProductCertification.class))).thenReturn(certification);

        // When
        certificationService.restoreCertification(1L);

        // Then
        verify(certificationRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(certificationRepository, times(1)).save(certification);
        assertThat(certification.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when restoring non-deleted certification")
    void restoreCertification_WhenNotDeleted_ShouldThrowException() {
        // Given
        when(certificationRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(certification));

        // When & Then
        assertThatThrownBy(() -> certificationService.restoreCertification(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Product certification with id 1 is not deleted");
        verify(certificationRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(certificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when restoring non-existent certification")
    void restoreCertification_WhenNotExists_ShouldThrowException() {
        // Given
        when(certificationRepository.findByIdIncludingDeleted(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> certificationService.restoreCertification(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product certification not found with id: 999");
        verify(certificationRepository, times(1)).findByIdIncludingDeleted(999L);
        verify(certificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should hard delete certification successfully")
    void hardDeleteCertification_WhenExists_ShouldHardDeleteCertification() {
        // Given
        when(certificationRepository.findByIdIncludingDeleted(1L)).thenReturn(Optional.of(certification));
        doNothing().when(certificationRepository).hardDeleteById(1L);

        // When
        certificationService.hardDeleteCertification(1L);

        // Then
        verify(certificationRepository, times(1)).findByIdIncludingDeleted(1L);
        verify(certificationRepository, times(1)).hardDeleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when hard deleting non-existent certification")
    void hardDeleteCertification_WhenNotExists_ShouldThrowException() {
        // Given
        when(certificationRepository.findByIdIncludingDeleted(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> certificationService.hardDeleteCertification(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product certification not found with id: 999");
        verify(certificationRepository, times(1)).findByIdIncludingDeleted(999L);
        verify(certificationRepository, never()).hardDeleteById(any());
    }

    @Test
    @DisplayName("Should return all deleted certifications")
    void getAllDeletedCertifications_ShouldReturnDeletedCertifications() {
        // Given
        certification.softDelete();
        List<ProductCertification> deletedCertifications = Arrays.asList(certification);
        when(certificationRepository.findAllDeleted()).thenReturn(deletedCertifications);
        when(certificationMapper.toResponse(certification)).thenReturn(certificationResponse);

        // When
        List<ProductCertificationResponse> result = certificationService.getAllDeletedCertifications();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLabel()).isEqualTo("Organic");
        verify(certificationRepository, times(1)).findAllDeleted();
    }
}
