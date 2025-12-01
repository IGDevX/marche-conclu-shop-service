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
                .certifications(document.getCertifications() != null ?
                        document.getCertifications().stream()
                                .map(cert -> ProductCertificationResponse.builder()
                                        .id(cert.getId())
                                        .label(cert.getLabel())
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
}

