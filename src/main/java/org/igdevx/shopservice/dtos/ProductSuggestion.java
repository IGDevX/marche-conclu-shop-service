package org.igdevx.shopservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSuggestion {

    private Long id;
    private String title;
    private String imageUrl;
}

