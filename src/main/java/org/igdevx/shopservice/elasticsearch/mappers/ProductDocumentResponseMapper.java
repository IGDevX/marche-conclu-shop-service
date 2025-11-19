package org.igdevx.shopservice.elasticsearch.mappers;

import org.igdevx.shopservice.dtos.*;
import org.igdevx.shopservice.elasticsearch.documents.ProductDocument;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductDocumentResponseMapper {

    public ProductResponse toProductResponse(ProductDocument document) {
        if (document == null) {
            return null;
        }

        return ProductResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .description(document.getDescription())
                .price(document.getPrice())
                .currency(document.getCurrencyId() != null ? CurrencyResponse.builder()
                        .id(document.getCurrencyId())
                        .code(document.getCurrencyCode())
                        .build() : null)
                .unit(document.getUnitId() != null ? UnitResponse.builder()
                        .id(document.getUnitId())
                        .label(document.getUnitName())
                        .build() : null)
                .shelf(document.getShelfId() != null ? ShelfResponse.builder()
                        .id(document.getShelfId())
                        .label(document.getShelfName())
                        .build() : null)
                .category(document.getCategoryId() != null ? CategoryResponse.builder()
                        .id(document.getCategoryId())
                        .name(document.getCategoryName())
                        .build() : null)
                .certifications(document.getCertificationIds() != null && document.getCertificationNames() != null ?
                        document.getCertificationIds().stream()
                                .map(id -> ProductCertificationResponse.builder()
                                        .id(id)
                                        .label(findCertificationName(id, document))
                                        .build())
                                .collect(Collectors.toSet()) : null)
                .mainImageId(document.getMainImageId() != null ? java.util.UUID.fromString(document.getMainImageId()) : null)
                .mainImageUrl(document.getMainImageUrl())
                .isFresh(document.getIsFresh())
                .producerId(document.getProducerId())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .isDeleted(document.getIsDeleted())
                .build();
    }

    private String findCertificationName(Long id, ProductDocument document) {
        // For now, we can't match perfectly without the names being in a map
        // We'll just return a placeholder or the first available name
        if (document.getCertificationNames() != null && !document.getCertificationNames().isEmpty()) {
            return document.getCertificationNames().iterator().next();
        }
        return "Certification " + id;
    }
}

