package org.igdevx.shopservice.mappers;

import org.igdevx.shopservice.dtos.ProductRequest;
import org.igdevx.shopservice.dtos.ProductResponse;
import org.igdevx.shopservice.models.Product;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductMapper {

    private final CurrencyMapper currencyMapper;
    private final UnitMapper unitMapper;
    private final ShelfMapper shelfMapper;
    private final ProductCertificationMapper certificationMapper;

    public ProductMapper(CurrencyMapper currencyMapper,
                        UnitMapper unitMapper,
                        ShelfMapper shelfMapper,
                        ProductCertificationMapper certificationMapper) {
        this.currencyMapper = currencyMapper;
        this.unitMapper = unitMapper;
        this.shelfMapper = shelfMapper;
        this.certificationMapper = certificationMapper;
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .currency(currencyMapper.toResponse(product.getCurrency()))
                .unit(unitMapper.toResponse(product.getUnit()))
                .shelf(shelfMapper.toResponse(product.getShelf()))
                .certifications(product.getCertifications() != null
                        ? product.getCertifications().stream()
                        .map(certificationMapper::toResponse)
                        .collect(Collectors.toSet())
                        : null)
                .imageUrl(product.getImageUrl())
                .imageKey(product.getImageKey())
                .imageThumbnailUrl(product.getImageThumbnailUrl())
                .isFresh(product.getIsFresh())
                .isAvailable(product.getIsAvailable())
                .producerId(product.getProducerId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .isDeleted(product.getIsDeleted())
                .build();
    }

    public void updateBasicFields(Product product, ProductRequest request) {
        if (product == null || request == null) {
            return;
        }

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setIsFresh(request.getIsFresh() != null ? request.getIsFresh() : false);
        product.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);
        product.setProducerId(request.getProducerId());
    }
}
