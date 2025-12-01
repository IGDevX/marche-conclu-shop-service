package org.igdevx.shopservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.igdevx.shopservice.dtos.*;
import org.igdevx.shopservice.elasticsearch.documents.ProductDocument;
import org.igdevx.shopservice.elasticsearch.services.ProductIndexService;
import org.igdevx.shopservice.elasticsearch.services.ProductSearchService;
import org.igdevx.shopservice.exceptions.ErrorResponse;
import org.igdevx.shopservice.mappers.ProductMapper;
import org.igdevx.shopservice.services.ProductService;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product management APIs with Elasticsearch search")
public class ProductController {

    private final ProductService productService;
    private final ProductSearchService productSearchService;
    private final ProductIndexService productIndexService;
    private final ProductMapper productMapper;
    private final org.igdevx.shopservice.elasticsearch.mappers.ProductDocumentResponseMapper documentResponseMapper;

    @PostMapping("/search")
    @Operation(summary = "Search products", description = "Search and filter products using Elasticsearch with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved search results"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductSearchResponse> searchProducts(@RequestBody ProductSearchRequest request) {
        SearchHits<ProductDocument> searchHits = productSearchService.searchProducts(request);

        List<ProductResponse> products = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(documentResponseMapper::toProductResponse)
                .collect(Collectors.toList());

        ProductSearchResponse response = ProductSearchResponse.builder()
                .products(products)
                .totalElements(searchHits.getTotalHits())
                .totalPages((int) Math.ceil((double) searchHits.getTotalHits() / (request.getSize() != null ? request.getSize() : 20)))
                .currentPage(request.getPage() != null ? request.getPage() : 0)
                .pageSize(request.getSize() != null ? request.getSize() : 20)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/suggest")
    @Operation(summary = "Get product suggestions", description = "Get autocomplete suggestions for product search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved suggestions")
    })
    public ResponseEntity<List<ProductSuggestion>> getSuggestions(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int size) {
        List<ProductSuggestion> suggestions = productSearchService.getSuggestions(q, size);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/producer/{producerId}")
    @Operation(summary = "Get products by producer", description = "Retrieve all products from a specific producer with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    public ResponseEntity<ProductSearchResponse> getProductsByProducer(
            @PathVariable Long producerId,
            @RequestParam(required = false) Long shelfId,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyDeleted,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {

        SearchHits<ProductDocument> searchHits = productSearchService.searchProductsByProducer(
                producerId, shelfId, onlyDeleted, page, size);

        List<ProductResponse> products = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(documentResponseMapper::toProductResponse)
                .collect(Collectors.toList());

        ProductSearchResponse response = ProductSearchResponse.builder()
                .products(products)
                .totalElements(searchHits.getTotalHits())
                .totalPages((int) Math.ceil((double) searchHits.getTotalHits() / size))
                .currentPage(page)
                .pageSize(size)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @Operation(summary = "Create a new product", description = "Create a new product without image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Related entity not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Update an existing product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                          @Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a product", description = "Soft delete a product by ID (marks as deleted)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product soft deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restore a deleted product", description = "Restore a soft-deleted product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product restored successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Product is not deleted",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> restoreProduct(@PathVariable Long id) {
        productService.restoreProduct(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    @Operation(summary = "Hard delete a product", description = "Permanently delete a product and its image by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product permanently deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> hardDeleteProduct(@PathVariable Long id) {
        productService.hardDeleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/index/recreate")
    @Operation(summary = "Recreate Elasticsearch index", description = "Delete and recreate the Elasticsearch index with updated mapping. Use this when ProductDocument structure changes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Index recreated successfully")
    })
    public ResponseEntity<String> recreateIndex() {
        productIndexService.recreateIndex();
        return ResponseEntity.ok("Successfully recreated Elasticsearch index with new mapping");
    }

    @PostMapping("/index/recreate-and-reindex")
    @Operation(summary = "Recreate index and reindex all products", description = "Delete and recreate the index with new mapping, then reindex all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Index recreated and reindexed successfully")
    })
    public ResponseEntity<String> recreateAndReindex() {
        productIndexService.recreateIndex();
        long count = productIndexService.reindexAllPaginated();
        return ResponseEntity.ok("Successfully recreated index and reindexed " + count + " products");
    }

    @PostMapping("/index/reindex-all")
    @Operation(summary = "Reindex all products", description = "Rebuild the entire Elasticsearch index with pagination (memory safe)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reindexing completed successfully")
    })
    public ResponseEntity<String> reindexAll() {
        long count = productIndexService.reindexAllPaginated();
        return ResponseEntity.ok("Successfully reindexed " + count + " products");
    }

    @PostMapping("/index/reindex/{id}")
    @Operation(summary = "Reindex a specific product", description = "Reindex a single product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product reindexed successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> reindexProduct(@PathVariable Long id) {
        productIndexService.indexProductById(id);
        return ResponseEntity.ok("Successfully reindexed product with id: " + id);
    }

    @DeleteMapping("/index/clear")
    @Operation(summary = "Clear Elasticsearch index", description = "Delete all documents from the Elasticsearch index")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Index cleared successfully")
    })
    public ResponseEntity<String> clearIndex() {
        productIndexService.clearIndex();
        return ResponseEntity.ok("Successfully cleared Elasticsearch index");
    }
}

