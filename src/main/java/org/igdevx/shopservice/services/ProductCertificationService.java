package org.igdevx.shopservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.igdevx.shopservice.dtos.ProductCertificationRequest;
import org.igdevx.shopservice.dtos.ProductCertificationResponse;
import org.igdevx.shopservice.exceptions.DuplicateResourceException;
import org.igdevx.shopservice.exceptions.ResourceNotFoundException;
import org.igdevx.shopservice.mappers.ProductCertificationMapper;
import org.igdevx.shopservice.models.ProductCertification;
import org.igdevx.shopservice.repositories.ProductCertificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCertificationService {

    private final ProductCertificationRepository productCertificationRepository;
    private final ProductCertificationMapper productCertificationMapper;

    @Transactional(readOnly = true)
    public List<ProductCertificationResponse> getAllCertifications() {
        log.debug("Fetching all product certifications");
        return productCertificationRepository.findAll()
                .stream()
                .map(productCertificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductCertificationResponse getCertificationById(Long id) {
        log.debug("Fetching product certification with id: {}", id);
        ProductCertification certification = productCertificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product certification not found with id: " + id));
        return productCertificationMapper.toResponse(certification);
    }

    @Transactional(readOnly = true)
    public ProductCertificationResponse getCertificationByLabel(String label) {
        log.debug("Fetching product certification with label: {}", label);
        ProductCertification certification = productCertificationRepository.findByLabel(label)
                .orElseThrow(() -> new ResourceNotFoundException("Product certification not found with label: " + label));
        return productCertificationMapper.toResponse(certification);
    }

    @Transactional
    public ProductCertificationResponse createCertification(ProductCertificationRequest request) {
        log.debug("Creating new product certification with label: {}", request.getLabel());
        
        if (productCertificationRepository.existsByLabel(request.getLabel())) {
            throw new DuplicateResourceException("Product certification already exists with label: " + request.getLabel());
        }

        ProductCertification certification = productCertificationMapper.toEntity(request);
        ProductCertification savedCertification = productCertificationRepository.save(certification);
        log.info("Product certification created successfully with id: {}", savedCertification.getId());
        
        return productCertificationMapper.toResponse(savedCertification);
    }

    @Transactional
    public ProductCertificationResponse updateCertification(Long id, ProductCertificationRequest request) {
        log.debug("Updating product certification with id: {}", id);
        
        ProductCertification certification = productCertificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product certification not found with id: " + id));

        // Check if label is being changed and if new label already exists
        if (!certification.getLabel().equals(request.getLabel()) 
                && productCertificationRepository.existsByLabel(request.getLabel())) {
            throw new DuplicateResourceException("Product certification already exists with label: " + request.getLabel());
        }

        productCertificationMapper.updateEntity(certification, request);
        ProductCertification updatedCertification = productCertificationRepository.save(certification);
        log.info("Product certification updated successfully with id: {}", updatedCertification.getId());
        
        return productCertificationMapper.toResponse(updatedCertification);
    }

    @Transactional
    public void deleteCertification(Long id) {
        log.debug("Soft deleting product certification with id: {}", id);
        
        ProductCertification certification = productCertificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product certification not found with id: " + id));

        certification.softDelete();
        productCertificationRepository.save(certification);
        log.info("Product certification soft deleted successfully with id: {}", id);
    }

    @Transactional
    public void restoreCertification(Long id) {
        log.debug("Restoring product certification with id: {}", id);
        
        ProductCertification certification = productCertificationRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product certification not found with id: " + id));

        if (!certification.getIsDeleted()) {
            throw new IllegalStateException("Product certification with id " + id + " is not deleted");
        }

        certification.restore();
        productCertificationRepository.save(certification);
        log.info("Product certification restored successfully with id: {}", id);
    }

    @Transactional
    public void hardDeleteCertification(Long id) {
        log.debug("Hard deleting product certification with id: {}", id);
        
        // Verify certification exists (including deleted ones)
        productCertificationRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product certification not found with id: " + id));

        productCertificationRepository.hardDeleteById(id);
        log.info("Product certification hard deleted successfully with id: {}", id);
    }

    @Transactional(readOnly = true)
    public List<ProductCertificationResponse> getAllDeletedCertifications() {
        log.debug("Fetching all deleted product certifications");
        return productCertificationRepository.findAllDeleted()
                .stream()
                .map(productCertificationMapper::toResponse)
                .collect(Collectors.toList());
    }
}
