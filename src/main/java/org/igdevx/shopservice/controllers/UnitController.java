package org.igdevx.shopservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.igdevx.shopservice.dtos.UnitRequest;
import org.igdevx.shopservice.dtos.UnitResponse;
import org.igdevx.shopservice.exceptions.ErrorResponse;
import org.igdevx.shopservice.services.UnitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
@Tag(name = "Units", description = "Unit management APIs")
public class UnitController {

    private final UnitService unitService;

    @Operation(summary = "Get all units", description = "Retrieve a list of all available units")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of units")
    })
    @GetMapping
    public ResponseEntity<List<UnitResponse>> getAllUnits() {
        return ResponseEntity.ok(unitService.getAllUnits());
    }

    @Operation(summary = "Get unit by ID", description = "Retrieve a specific unit by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved unit"),
            @ApiResponse(responseCode = "404", description = "Unit not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UnitResponse> getUnitById(@PathVariable Long id) {
        return ResponseEntity.ok(unitService.getUnitById(id));
    }

    @Operation(summary = "Get unit by code", description = "Retrieve a specific unit by its code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved unit"),
            @ApiResponse(responseCode = "404", description = "Unit not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/code/{code}")
    public ResponseEntity<UnitResponse> getUnitByCode(@PathVariable String code) {
        return ResponseEntity.ok(unitService.getUnitByCode(code));
    }

    @Operation(summary = "Create a new unit", description = "Create a new unit with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Unit created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Unit with this code already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<UnitResponse> createUnit(@Valid @RequestBody UnitRequest request) {
        UnitResponse response = unitService.createUnit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update an existing unit", description = "Update an existing unit by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unit updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Unit not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Unit with this code already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<UnitResponse> updateUnit(
            @PathVariable Long id,
            @Valid @RequestBody UnitRequest request
    ) {
        return ResponseEntity.ok(unitService.updateUnit(id, request));
    }

    @Operation(summary = "Delete a unit (soft delete)", description = "Soft delete a unit by its ID - marks it as deleted but keeps it in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Unit soft deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Unit not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Restore a deleted unit", description = "Restore a soft-deleted unit by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Unit restored successfully"),
            @ApiResponse(responseCode = "404", description = "Unit not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Unit is not deleted",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restoreUnit(@PathVariable Long id) {
        unitService.restoreUnit(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Permanently delete a unit", description = "Permanently delete a unit by its ID - removes it from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Unit permanently deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Unit not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteUnit(@PathVariable Long id) {
        unitService.hardDeleteUnit(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all deleted units", description = "Retrieve a list of all soft-deleted units")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of deleted units")
    })
    @GetMapping("/deleted")
    public ResponseEntity<List<UnitResponse>> getAllDeletedUnits() {
        return ResponseEntity.ok(unitService.getAllDeletedUnits());
    }
}
