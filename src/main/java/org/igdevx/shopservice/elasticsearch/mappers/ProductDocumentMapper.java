package org.igdevx.shopservice.elasticsearch.mappers;

import org.igdevx.shopservice.elasticsearch.documents.CertificationInfo;
import org.igdevx.shopservice.elasticsearch.documents.ProductDocument;
import org.igdevx.shopservice.models.Product;
import org.igdevx.shopservice.models.ProductCertification;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductDocumentMapper {

    public ProductDocument toDocument(Product product) {
        if (product == null) {
            return null;
        }

        return ProductDocument.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .currencyCode(product.getCurrency() != null ? product.getCurrency().getCode() : null)
                .currencyId(product.getCurrency() != null ? product.getCurrency().getId() : null)
                .unitName(product.getUnit() != null ? product.getUnit().getLabel() : null)
                .unitId(product.getUnit() != null ? product.getUnit().getId() : null)
                .shelfName(product.getShelf() != null ? product.getShelf().getLabel() : null)
                .shelfId(product.getShelf() != null ? product.getShelf().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .certifications(product.getCertifications() != null ?
                        product.getCertifications().stream()
                                .map(cert -> CertificationInfo.builder()
                                        .id(cert.getId())
                                        .label(cert.getLabel())
                                        .build())
                                .collect(Collectors.toList()) : null)
                .certificationNames(product.getCertifications() != null ?
                        product.getCertifications().stream()
                                .map(ProductCertification::getLabel)
                                .collect(Collectors.toSet()) : null)
                .certificationIds(product.getCertifications() != null ?
                        product.getCertifications().stream()
                                .map(ProductCertification::getId)
                                .collect(Collectors.toSet()) : null)
                .mainImageId(product.getMainImageId() != null ? product.getMainImageId().toString() : null)
                .mainImageUrl(product.getMainImageUrl())
                .isFresh(product.getIsFresh())
                .producerId(product.getProducerId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .isDeleted(product.getIsDeleted())
                .build();
    }
}

