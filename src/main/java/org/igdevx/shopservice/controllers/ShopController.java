package org.igdevx.shopservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/shops")
@Tag(name = "Shop", description = "Shop management APIs")
public class ShopController {

    @Operation(
            summary = "Health check",
            description = "Check if the shop service is running and healthy"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is healthy")
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "shop-service");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get service info",
            description = "Get information about the shop service"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved service info")
    })
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        Map<String, String> response = new HashMap<>();
        response.put("service", "shop-service");
        response.put("version", "1.0.0");
        response.put("description", "Service for managing shops");
        return ResponseEntity.ok(response);
    }
}
