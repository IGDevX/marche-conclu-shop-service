package org.igdevx.shopservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.igdevx.shopservice.dtos.ProductCategoryRequest;
import org.igdevx.shopservice.dtos.ProductCategoryResponse;
import org.igdevx.shopservice.exceptions.ErrorResponse;
import org.igdevx.shopservice.services.ProductCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-categories")
@RequiredArgsConstructor
@Tag(name = "Product Categories", description = "Product category management APIs")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @Operation(summary = "Get all product categories", description = "Retrieve a list of all available product categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of categories")
    })
    @GetMapping
    public ResponseEntity<List<ProductCategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(productCategoryService.getAllCategories());
    }

    @Operation(summary = "Get product category by ID", description = "Retrieve a specific product category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved category"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(productCategoryService.getCategoryById(id));
    }

    @Operation(summary = "Get product category by slug", description = "Retrieve a specific product category by its slug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved category"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductCategoryResponse> getCategoryBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productCategoryService.getCategoryBySlug(slug));
    }

    @Operation(summary = "Create a new product category", description = "Create a new product category with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Category with this label or slug already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ProductCategoryResponse> createCategory(@Valid @RequestBody ProductCategoryRequest request) {
        ProductCategoryResponse response = productCategoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update an existing product category", description = "Update an existing product category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Category with this label or slug already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductCategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody ProductCategoryRequest request
    ) {
        return ResponseEntity.ok(productCategoryService.updateCategory(id, request));
    }

    @Operation(summary = "Delete a product category (soft delete)", description = "Soft delete a product category by its ID - marks it as deleted but keeps it in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category soft deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        productCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Restore a deleted product category", description = "Restore a soft-deleted product category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category restored successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Category is not deleted",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restoreCategory(@PathVariable Long id) {
        productCategoryService.restoreCategory(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Permanently delete a product category", description = "Permanently delete a product category by its ID - removes it from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category permanently deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteCategory(@PathVariable Long id) {
        productCategoryService.hardDeleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all deleted product categories", description = "Retrieve a list of all soft-deleted product categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of deleted categories")
    })
    @GetMapping("/deleted")
    public ResponseEntity<List<ProductCategoryResponse>> getAllDeletedCategories() {
        return ResponseEntity.ok(productCategoryService.getAllDeletedCategories());
    }
}
