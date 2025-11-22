package org.igdevx.shopservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.igdevx.shopservice.dtos.CurrencyRequest;
import org.igdevx.shopservice.dtos.CurrencyResponse;
import org.igdevx.shopservice.exceptions.ErrorResponse;
import org.igdevx.shopservice.services.CurrencyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
@Tag(name = "Currency", description = "Currency management APIs")
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    @Operation(summary = "Get all currencies", description = "Retrieve a list of all non-deleted currencies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of currencies"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<CurrencyResponse>> getAllCurrencies() {
        List<CurrencyResponse> currencies = currencyService.getAllCurrencies();
        return ResponseEntity.ok(currencies);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get currency by ID", description = "Retrieve a currency by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Currency found"),
            @ApiResponse(responseCode = "404", description = "Currency not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CurrencyResponse> getCurrencyById(@PathVariable Long id) {
        CurrencyResponse currency = currencyService.getCurrencyById(id);
        return ResponseEntity.ok(currency);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get currency by code", description = "Retrieve a currency by its ISO 4217 code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Currency found"),
            @ApiResponse(responseCode = "404", description = "Currency not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CurrencyResponse> getCurrencyByCode(@PathVariable String code) {
        CurrencyResponse currency = currencyService.getCurrencyByCode(code);
        return ResponseEntity.ok(currency);
    }

    @PostMapping
    @Operation(summary = "Create a new currency", description = "Create a new currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Currency created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Currency with this code already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CurrencyResponse> createCurrency(@Valid @RequestBody CurrencyRequest request) {
        CurrencyResponse currency = currencyService.createCurrency(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(currency);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a currency", description = "Update an existing currency by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Currency updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Currency not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Currency with this code already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CurrencyResponse> updateCurrency(@PathVariable Long id,
                                                            @Valid @RequestBody CurrencyRequest request) {
        CurrencyResponse currency = currencyService.updateCurrency(id, request);
        return ResponseEntity.ok(currency);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a currency", description = "Soft delete a currency by ID (marks as deleted)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Currency soft deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Currency not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteCurrency(@PathVariable Long id) {
        currencyService.deleteCurrency(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restore a deleted currency", description = "Restore a soft-deleted currency by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Currency restored successfully"),
            @ApiResponse(responseCode = "404", description = "Currency not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Currency is not deleted",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> restoreCurrency(@PathVariable Long id) {
        currencyService.restoreCurrency(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    @Operation(summary = "Hard delete a currency", description = "Permanently delete a currency by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Currency permanently deleted"),
            @ApiResponse(responseCode = "404", description = "Currency not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> hardDeleteCurrency(@PathVariable Long id) {
        currencyService.hardDeleteCurrency(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deleted")
    @Operation(summary = "Get all deleted currencies", description = "Retrieve a list of all soft-deleted currencies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of deleted currencies"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<CurrencyResponse>> getAllDeletedCurrencies() {
        List<CurrencyResponse> currencies = currencyService.getAllDeletedCurrencies();
        return ResponseEntity.ok(currencies);
    }
}
