package org.igdevx.shopservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchResponse {

    private List<ProductResponse> products;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}

