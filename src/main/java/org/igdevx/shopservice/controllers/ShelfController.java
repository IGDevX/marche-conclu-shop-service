package org.igdevx.shopservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.igdevx.shopservice.dtos.ShelfRequest;
import org.igdevx.shopservice.dtos.ShelfResponse;
import org.igdevx.shopservice.exceptions.ErrorResponse;
import org.igdevx.shopservice.services.ShelfService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shelves")
@RequiredArgsConstructor
@Tag(name = "Shelves", description = "Shelf management APIs")
public class ShelfController {

    private final ShelfService shelfService;

    @Operation(summary = "Get all shelves", description = "Retrieve a list of all available shelves")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of shelves")
    })
    @GetMapping
    public ResponseEntity<List<ShelfResponse>> getAllShelves() {
        return ResponseEntity.ok(shelfService.getAllShelves());
    }

    @Operation(summary = "Get shelf by ID", description = "Retrieve a specific shelf by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved shelf"),
            @ApiResponse(responseCode = "404", description = "Shelf not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ShelfResponse> getShelfById(@PathVariable Long id) {
        return ResponseEntity.ok(shelfService.getShelfById(id));
    }

    @Operation(summary = "Get shelves by producer", description = "Retrieve all shelves belonging to a specific producer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved shelves for the producer")
    })
    @GetMapping("/producer/{producerId}")
    public ResponseEntity<List<ShelfResponse>> getShelvesByProducer(@PathVariable Long producerId) {
        return ResponseEntity.ok(shelfService.getShelvesByProducerId(producerId));
    }

    @Operation(summary = "Create a new shelf", description = "Create a new shelf with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Shelf created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Shelf with this label already exists for this producer",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ShelfResponse> createShelf(@Valid @RequestBody ShelfRequest request) {
        ShelfResponse response = shelfService.createShelf(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update an existing shelf", description = "Update an existing shelf by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shelf updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Shelf not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Shelf with this label already exists for this producer",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ShelfResponse> updateShelf(
            @PathVariable Long id,
            @Valid @RequestBody ShelfRequest request
    ) {
        return ResponseEntity.ok(shelfService.updateShelf(id, request));
    }

    @Operation(summary = "Delete a shelf (soft delete)", description = "Soft delete a shelf by its ID - marks it as deleted but keeps it in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Shelf soft deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Shelf not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShelf(@PathVariable Long id) {
        shelfService.deleteShelf(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Restore a deleted shelf", description = "Restore a soft-deleted shelf by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Shelf restored successfully"),
            @ApiResponse(responseCode = "404", description = "Shelf not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Shelf is not deleted",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restoreShelf(@PathVariable Long id) {
        shelfService.restoreShelf(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Permanently delete a shelf", description = "Permanently delete a shelf by its ID - removes it from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Shelf permanently deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Shelf not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteShelf(@PathVariable Long id) {
        shelfService.hardDeleteShelf(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all deleted shelves", description = "Retrieve a list of all soft-deleted shelves")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of deleted shelves")
    })
    @GetMapping("/deleted")
    public ResponseEntity<List<ShelfResponse>> getAllDeletedShelves() {
        return ResponseEntity.ok(shelfService.getAllDeletedShelves());
    }
}
