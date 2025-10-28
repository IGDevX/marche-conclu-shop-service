package org.igdevx.shopservice.mappers;

import org.igdevx.shopservice.dtos.ProductCertificationRequest;
import org.igdevx.shopservice.dtos.ProductCertificationResponse;
import org.igdevx.shopservice.models.ProductCertification;
import org.springframework.stereotype.Component;

@Component
public class ProductCertificationMapper {

    public ProductCertificationResponse toResponse(ProductCertification certification) {
        return ProductCertificationResponse.builder()
                .id(certification.getId())
                .label(certification.getLabel())
                .createdAt(certification.getCreatedAt())
                .updatedAt(certification.getUpdatedAt())
                .isDeleted(certification.getIsDeleted())
                .build();
    }

    public ProductCertification toEntity(ProductCertificationRequest request) {
        return ProductCertification.builder()
                .label(request.getLabel())
                .build();
    }

    public void updateEntity(ProductCertification certification, ProductCertificationRequest request) {
        certification.setLabel(request.getLabel());
    }
}
