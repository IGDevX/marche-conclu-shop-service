package org.igdevx.shopservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.igdevx.shopservice.dtos.UnitRequest;
import org.igdevx.shopservice.dtos.UnitResponse;
import org.igdevx.shopservice.exceptions.DuplicateResourceException;
import org.igdevx.shopservice.exceptions.ResourceNotFoundException;
import org.igdevx.shopservice.mappers.UnitMapper;
import org.igdevx.shopservice.models.Unit;
import org.igdevx.shopservice.repositories.UnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnitService {

    private final UnitRepository unitRepository;
    private final UnitMapper unitMapper;

    @Transactional(readOnly = true)
    public List<UnitResponse> getAllUnits() {
        log.debug("Fetching all units");
        return unitRepository.findAll()
                .stream()
                .map(unitMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UnitResponse getUnitById(Long id) {
        log.debug("Fetching unit with id: {}", id);
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + id));
        return unitMapper.toResponse(unit);
    }

    @Transactional(readOnly = true)
    public UnitResponse getUnitByCode(String code) {
        log.debug("Fetching unit with code: {}", code);
        Unit unit = unitRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with code: " + code));
        return unitMapper.toResponse(unit);
    }

    @Transactional
    public UnitResponse createUnit(UnitRequest request) {
        log.debug("Creating new unit with code: {}", request.getCode());
        
        if (unitRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Unit already exists with code: " + request.getCode());
        }

        Unit unit = unitMapper.toEntity(request);
        Unit savedUnit = unitRepository.save(unit);
        log.info("Unit created successfully with id: {}", savedUnit.getId());
        
        return unitMapper.toResponse(savedUnit);
    }

    @Transactional
    public UnitResponse updateUnit(Long id, UnitRequest request) {
        log.debug("Updating unit with id: {}", id);
        
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + id));

        // Check if code is being changed and if new code already exists
        if (!unit.getCode().equals(request.getCode()) && unitRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Unit already exists with code: " + request.getCode());
        }

        unitMapper.updateEntity(unit, request);
        Unit updatedUnit = unitRepository.save(unit);
        log.info("Unit updated successfully with id: {}", updatedUnit.getId());
        
        return unitMapper.toResponse(updatedUnit);
    }

    @Transactional
    public void deleteUnit(Long id) {
        log.debug("Soft deleting unit with id: {}", id);
        
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + id));

        unit.softDelete();
        unitRepository.save(unit);
        log.info("Unit soft deleted successfully with id: {}", id);
    }

    @Transactional
    public void restoreUnit(Long id) {
        log.debug("Restoring unit with id: {}", id);
        
        Unit unit = unitRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + id));

        if (!unit.getIsDeleted()) {
            throw new IllegalStateException("Unit with id " + id + " is not deleted");
        }

        unit.restore();
        unitRepository.save(unit);
        log.info("Unit restored successfully with id: {}", id);
    }

    @Transactional
    public void hardDeleteUnit(Long id) {
        log.debug("Hard deleting unit with id: {}", id);
        
        // Verify unit exists (including deleted ones)
        unitRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + id));

        unitRepository.hardDeleteById(id);
        log.info("Unit hard deleted successfully with id: {}", id);
    }

    @Transactional(readOnly = true)
    public List<UnitResponse> getAllDeletedUnits() {
        log.debug("Fetching all deleted units");
        return unitRepository.findAllDeleted()
                .stream()
                .map(unitMapper::toResponse)
                .collect(Collectors.toList());
    }
}
