package org.igdevx.shopservice.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRequest {

    @NotBlank(message = "Currency code is required")
    @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters (ISO 4217)")
    private String code;

    @NotBlank(message = "Currency label is required")
    @Size(max = 100, message = "Currency label must not exceed 100 characters")
    private String label;

    @NotNull(message = "USD exchange rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "USD exchange rate must be greater than 0")
    private BigDecimal usdExchangeRate;
}
