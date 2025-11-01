package org.igdevx.shopservice.mappers;

import org.igdevx.shopservice.dtos.ShelfRequest;
import org.igdevx.shopservice.dtos.ShelfResponse;
import org.igdevx.shopservice.models.Shelf;
import org.springframework.stereotype.Component;

@Component
public class ShelfMapper {

    public ShelfResponse toResponse(Shelf shelf) {
        return ShelfResponse.builder()
                .id(shelf.getId())
                .label(shelf.getLabel())
                .producerId(shelf.getProducerId())
                .createdAt(shelf.getCreatedAt())
                .updatedAt(shelf.getUpdatedAt())
                .isDeleted(shelf.getIsDeleted())
                .build();
    }

    public Shelf toEntity(ShelfRequest request) {
        return Shelf.builder()
                .label(request.getLabel())
                .producerId(request.getProducerId())
                .build();
    }

    public void updateEntity(Shelf shelf, ShelfRequest request) {
        shelf.setProducerId(request.getProducerId());
        shelf.setLabel(request.getLabel());
    }
}
