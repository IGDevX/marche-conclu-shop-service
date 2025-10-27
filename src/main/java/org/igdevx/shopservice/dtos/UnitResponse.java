package org.igdevx.shopservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response object containing unit information")
public class UnitResponse {

    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    @Schema(description = "Unit code", example = "kg")
    private String code;

    @Schema(description = "Unit label", example = "Kilogram")
    private String label;
}
