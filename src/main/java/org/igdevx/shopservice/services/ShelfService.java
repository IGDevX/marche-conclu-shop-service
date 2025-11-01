package org.igdevx.shopservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.igdevx.shopservice.dtos.ShelfRequest;
import org.igdevx.shopservice.dtos.ShelfResponse;
import org.igdevx.shopservice.exceptions.DuplicateResourceException;
import org.igdevx.shopservice.exceptions.ResourceNotFoundException;
import org.igdevx.shopservice.mappers.ShelfMapper;
import org.igdevx.shopservice.models.Shelf;
import org.igdevx.shopservice.repositories.ShelfRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShelfService {

    private final ShelfRepository shelfRepository;
    private final ShelfMapper shelfMapper;

    @Transactional(readOnly = true)
    public List<ShelfResponse> getAllShelves() {
        log.debug("Fetching all shelves");
        return shelfRepository.findAll()
                .stream()
                .map(shelfMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShelfResponse getShelfById(Long id) {
        log.debug("Fetching shelf with id: {}", id);
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shelf not found with id: " + id));
        return shelfMapper.toResponse(shelf);
    }

    @Transactional
    public ShelfResponse createShelf(ShelfRequest request) {
        log.debug("Creating new shelf with label: {} for producer: {}", request.getLabel(), request.getProducerId());
        
        // Check uniqueness per producer (not globally)
        if (shelfRepository.existsByProducerIdAndLabel(request.getProducerId(), request.getLabel())) {
            throw new DuplicateResourceException("Shelf already exists with label: " + request.getLabel() + " for this producer");
        }

        Shelf shelf = shelfMapper.toEntity(request);
        Shelf savedShelf = shelfRepository.save(shelf);
        log.info("Shelf created successfully with id: {} for producer: {}", savedShelf.getId(), savedShelf.getProducerId());
        
        return shelfMapper.toResponse(savedShelf);
    }

    @Transactional
    public ShelfResponse updateShelf(Long id, ShelfRequest request) {
        log.debug("Updating shelf with id: {}", id);
        
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shelf not found with id: " + id));

        // Ensure producer_id cannot be changed (business rule: shelves belong to producers)
        if (!shelf.getProducerId().equals(request.getProducerId())) {
            throw new IllegalArgumentException("Cannot change producer_id of an existing shelf");
        }

        // Check if label is being changed and if new label already exists for this producer
        if (!shelf.getLabel().equals(request.getLabel())
                && shelfRepository.existsByProducerIdAndLabel(request.getProducerId(), request.getLabel())) {
            throw new DuplicateResourceException("Shelf already exists with label: " + request.getLabel() + " for this producer");
        }

        shelfMapper.updateEntity(shelf, request);
        Shelf updatedShelf = shelfRepository.save(shelf);
        log.info("Shelf updated successfully with id: {}", updatedShelf.getId());
        
        return shelfMapper.toResponse(updatedShelf);
    }

    @Transactional
    public void deleteShelf(Long id) {
        log.debug("Soft deleting shelf with id: {}", id);
        
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shelf not found with id: " + id));

        shelf.softDelete();
        shelfRepository.save(shelf);
        log.info("Shelf soft deleted successfully with id: {}", id);
    }

    @Transactional
    public void restoreShelf(Long id) {
        log.debug("Restoring shelf with id: {}", id);
        
        Shelf shelf = shelfRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shelf not found with id: " + id));

        if (!shelf.getIsDeleted()) {
            throw new IllegalStateException("Shelf with id " + id + " is not deleted");
        }

        shelf.restore();
        shelfRepository.save(shelf);
        log.info("Shelf restored successfully with id: {}", id);
    }

    @Transactional
    public void hardDeleteShelf(Long id) {
        log.debug("Hard deleting shelf with id: {}", id);
        
        // Verify shelf exists (including deleted ones)
        shelfRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shelf not found with id: " + id));

        shelfRepository.hardDeleteById(id);
        log.info("Shelf hard deleted successfully with id: {}", id);
    }

    @Transactional(readOnly = true)
    public List<ShelfResponse> getAllDeletedShelves() {
        log.debug("Fetching all deleted shelves");
        return shelfRepository.findAllDeleted()
                .stream()
                .map(shelfMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShelfResponse> getShelvesByProducerId(Long producerId) {
        log.debug("Fetching shelves for producer: {}", producerId);
        return shelfRepository.findByProducerId(producerId)
                .stream()
                .map(shelfMapper::toResponse)
                .collect(Collectors.toList());
    }
}
