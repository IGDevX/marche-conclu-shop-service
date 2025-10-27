package org.igdevx.shopservice.mappers;

import org.igdevx.shopservice.dtos.UnitRequest;
import org.igdevx.shopservice.dtos.UnitResponse;
import org.igdevx.shopservice.models.Unit;
import org.springframework.stereotype.Component;

@Component
public class UnitMapper {

    public UnitResponse toResponse(Unit unit) {
        return UnitResponse.builder()
                .id(unit.getId())
                .code(unit.getCode())
                .label(unit.getLabel())
                .build();
    }

    public Unit toEntity(UnitRequest request) {
        return Unit.builder()
                .code(request.getCode())
                .label(request.getLabel())
                .build();
    }

    public void updateEntity(Unit unit, UnitRequest request) {
        unit.setCode(request.getCode());
        unit.setLabel(request.getLabel());
    }
}
