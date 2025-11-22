package org.igdevx.shopservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.igdevx.shopservice.dtos.ProductCertificationRequest;
import org.igdevx.shopservice.dtos.ProductCertificationResponse;
import org.igdevx.shopservice.exceptions.ErrorResponse;
import org.igdevx.shopservice.services.ProductCertificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-certifications")
@RequiredArgsConstructor
@Tag(name = "Product Certifications", description = "Product certification management APIs")
public class ProductCertificationController {

    private final ProductCertificationService productCertificationService;

    @Operation(summary = "Get all product certifications", description = "Retrieve a list of all available product certifications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of certifications")
    })
    @GetMapping
    public ResponseEntity<List<ProductCertificationResponse>> getAllCertifications() {
        return ResponseEntity.ok(productCertificationService.getAllCertifications());
    }

    @Operation(summary = "Get product certification by ID", description = "Retrieve a specific product certification by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved certification"),
            @ApiResponse(responseCode = "404", description = "Certification not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductCertificationResponse> getCertificationById(@PathVariable Long id) {
        return ResponseEntity.ok(productCertificationService.getCertificationById(id));
    }

    @Operation(summary = "Get product certification by label", description = "Retrieve a specific product certification by its label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved certification"),
            @ApiResponse(responseCode = "404", description = "Certification not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/label/{label}")
    public ResponseEntity<ProductCertificationResponse> getCertificationByLabel(@PathVariable String label) {
        return ResponseEntity.ok(productCertificationService.getCertificationByLabel(label));
    }

    @Operation(summary = "Create a new product certification", description = "Create a new product certification with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Certification created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Certification with this label already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ProductCertificationResponse> createCertification(@Valid @RequestBody ProductCertificationRequest request) {
        ProductCertificationResponse response = productCertificationService.createCertification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update an existing product certification", description = "Update an existing product certification by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certification updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Certification not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Certification with this label already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductCertificationResponse> updateCertification(
            @PathVariable Long id,
            @Valid @RequestBody ProductCertificationRequest request
    ) {
        return ResponseEntity.ok(productCertificationService.updateCertification(id, request));
    }

    @Operation(summary = "Delete a product certification (soft delete)", description = "Soft delete a product certification by its ID - marks it as deleted but keeps it in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Certification soft deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Certification not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertification(@PathVariable Long id) {
        productCertificationService.deleteCertification(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Restore a deleted product certification", description = "Restore a soft-deleted product certification by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Certification restored successfully"),
            @ApiResponse(responseCode = "404", description = "Certification not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Certification is not deleted",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restoreCertification(@PathVariable Long id) {
        productCertificationService.restoreCertification(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Permanently delete a product certification", description = "Permanently delete a product certification by its ID - removes it from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Certification permanently deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Certification not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteCertification(@PathVariable Long id) {
        productCertificationService.hardDeleteCertification(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all deleted product certifications", description = "Retrieve a list of all soft-deleted product certifications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of deleted certifications")
    })
    @GetMapping("/deleted")
    public ResponseEntity<List<ProductCertificationResponse>> getAllDeletedCertifications() {
        return ResponseEntity.ok(productCertificationService.getAllDeletedCertifications());
    }
}
