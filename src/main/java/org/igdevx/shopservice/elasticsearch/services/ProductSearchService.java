package org.igdevx.shopservice.elasticsearch.services;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.igdevx.shopservice.dtos.ProductSearchRequest;
import org.igdevx.shopservice.dtos.ProductSearchResponse;
import org.igdevx.shopservice.dtos.ProductSuggestion;
import org.igdevx.shopservice.elasticsearch.documents.ProductDocument;
import org.igdevx.shopservice.elasticsearch.repositories.ProductSearchRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchService {

    private final ProductSearchRepository productSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * Search products with filters, sorting, and pagination
     */
    public SearchHits<ProductDocument> searchProducts(ProductSearchRequest request) {
        log.debug("Searching products with request: {}", request);

        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();

        // Build query
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        // Full-text search on title and description
        if (request.getQ() != null && !request.getQ().isBlank()) {
            String searchQuery = request.getQ().toLowerCase();

            boolQueryBuilder.must(Query.of(q -> q
                    .bool(b -> b
                            .should(s -> s
                                    .wildcard(w -> w
                                            .field("title")
                                            .value("*" + searchQuery + "*")
                                            .caseInsensitive(true)
                                            .boost(3.0f)
                                    )
                            )
                            .should(s -> s
                                    .wildcard(w -> w
                                            .field("description")
                                            .value("*" + searchQuery + "*")
                                            .caseInsensitive(true)
                                            .boost(1.0f)
                                    )
                            )
                            .should(s -> s
                                    .match(m -> m
                                            .field("title")
                                            .query(request.getQ())
                                            .fuzziness("AUTO")
                                            .boost(2.0f)
                                    )
                            )
                            .should(s -> s
                                    .match(m -> m
                                            .field("description")
                                            .query(request.getQ())
                                            .fuzziness("AUTO")
                                    )
                            )
                            .minimumShouldMatch("1")
                    )
            ));
        }

        // Filter by deleted status
        if (request.getOnlyDeleted() != null && request.getOnlyDeleted()) {
            boolQueryBuilder.filter(Query.of(q -> q
                    .term(t -> t.field("isDeleted").value(true))
            ));
        } else {
            boolQueryBuilder.filter(Query.of(q -> q
                    .term(t -> t.field("isDeleted").value(false))
            ));
        }

        // Filter by categories
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            boolQueryBuilder.filter(Query.of(q -> q
                    .terms(t -> t
                            .field("categoryId")
                            .terms(terms -> terms.value(request.getCategoryIds().stream()
                                    .map(id -> co.elastic.clients.elasticsearch._types.FieldValue.of(id))
                                    .collect(Collectors.toList())))
                    )
            ));
        }

        // Filter by price range
        if (request.getPriceMin() != null && request.getPriceMax() != null) {
            boolQueryBuilder.filter(Query.of(q -> q
                    .range(r -> r
                            .number(n -> n
                                    .field("price")
                                    .gte(request.getPriceMin().doubleValue())
                                    .lte(request.getPriceMax().doubleValue())
                            )
                    )
            ));
        } else if (request.getPriceMin() != null) {
            boolQueryBuilder.filter(Query.of(q -> q
                    .range(r -> r
                            .number(n -> n
                                    .field("price")
                                    .gte(request.getPriceMin().doubleValue())
                            )
                    )
            ));
        } else if (request.getPriceMax() != null) {
            boolQueryBuilder.filter(Query.of(q -> q
                    .range(r -> r
                            .number(n -> n
                                    .field("price")
                                    .lte(request.getPriceMax().doubleValue())
                            )
                    )
            ));
        }

        // Filter by currency
        if (request.getCurrencyId() != null) {
            boolQueryBuilder.filter(Query.of(q -> q
                    .term(t -> t.field("currencyId").value(request.getCurrencyId()))
            ));
        }

        // Filter by fresh products
        if (request.getFresh() != null && request.getFresh()) {
            boolQueryBuilder.filter(Query.of(q -> q
                    .term(t -> t.field("isFresh").value(true))
            ));
        }

        // Filter by certifications
        if (request.getCertificationIds() != null && !request.getCertificationIds().isEmpty()) {
            boolQueryBuilder.filter(Query.of(q -> q
                    .terms(t -> t
                            .field("certificationIds")
                            .terms(terms -> terms.value(request.getCertificationIds().stream()
                                    .map(id -> co.elastic.clients.elasticsearch._types.FieldValue.of(id))
                                    .collect(Collectors.toList())))
                    )
            ));
        }


        queryBuilder.withQuery(Query.of(q -> q.bool(boolQueryBuilder.build())));

        // Sorting
        applySorting(queryBuilder, request.getSort());

        // Pagination
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size);
        queryBuilder.withPageable(pageable);

        NativeQuery query = queryBuilder.build();

        return elasticsearchOperations.search(query, ProductDocument.class);
    }

    /**
     * Search products by producer with optional shelf filter
     * Used for producer shop page (/api/products/producer/{producerId})
     */
    public SearchHits<ProductDocument> searchProductsByProducer(Long producerId, Long shelfId, Boolean onlyDeleted, int page, int size) {
        log.debug("Searching products for producer: {}, shelf: {}, onlyDeleted: {}", producerId, shelfId, onlyDeleted);

        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        // Filter by producer (required)
        boolQueryBuilder.filter(Query.of(q -> q
                .term(t -> t.field("producerId").value(producerId))
        ));

        // Filter by shelf (optional)
        if (shelfId != null) {
            boolQueryBuilder.filter(Query.of(q -> q
                    .term(t -> t.field("shelfId").value(shelfId))
            ));
        }

        // Filter by deleted status
        if (onlyDeleted != null && onlyDeleted) {
            boolQueryBuilder.filter(Query.of(q -> q
                    .term(t -> t.field("isDeleted").value(true))
            ));
        } else {
            boolQueryBuilder.filter(Query.of(q -> q
                    .term(t -> t.field("isDeleted").value(false))
            ));
        }

        queryBuilder.withQuery(Query.of(q -> q.bool(boolQueryBuilder.build())));

        // Default sorting by creation date
        queryBuilder.withSort(co.elastic.clients.elasticsearch._types.SortOptions.of(s -> s
                .field(f -> f.field("createdAt").order(SortOrder.Desc))
        ));

        // Pagination
        Pageable pageable = PageRequest.of(page, size);
        queryBuilder.withPageable(pageable);

        NativeQuery query = queryBuilder.build();

        return elasticsearchOperations.search(query, ProductDocument.class);
    }

    /**
     * Get suggestions for autocomplete
     */
    public List<ProductSuggestion> getSuggestions(String query, int size) {
        log.debug("Getting suggestions for query: {}, size: {}", query, size);

        if (query == null || query.isBlank()) {
            return new ArrayList<>();
        }

        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        // Search in title with prefix match
        boolQueryBuilder.should(Query.of(q -> q
                .matchPhrase(m -> m
                        .field("title")
                        .query(query)
                )
        ));

        boolQueryBuilder.should(Query.of(q -> q
                .prefix(p -> p
                        .field("title")
                        .value(query)
                )
        ));

        // Only non-deleted products
        boolQueryBuilder.filter(Query.of(q -> q
                .term(t -> t.field("isDeleted").value(false))
        ));

        queryBuilder.withQuery(Query.of(q -> q.bool(boolQueryBuilder.build())));
        queryBuilder.withPageable(PageRequest.of(0, size));

        NativeQuery nativeQuery = queryBuilder.build();

        SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(nativeQuery, ProductDocument.class);

        return searchHits.getSearchHits().stream()
                .map(hit -> {
                    ProductDocument doc = hit.getContent();
                    return ProductSuggestion.builder()
                            .id(doc.getId())
                            .title(doc.getTitle())
                            .imageUrl(doc.getMainImageUrl())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private void applySorting(NativeQueryBuilder queryBuilder, String sort) {
        if (sort == null || sort.isBlank()) {
            // Default sorting by creation date descending
            queryBuilder.withSort(co.elastic.clients.elasticsearch._types.SortOptions.of(s -> s
                    .field(f -> f.field("createdAt").order(SortOrder.Desc))
            ));
            return;
        }

        switch (sort.toLowerCase()) {
            case "price_asc":
                queryBuilder.withSort(co.elastic.clients.elasticsearch._types.SortOptions.of(s -> s
                        .field(f -> f.field("price").order(SortOrder.Asc))
                ));
                break;
            case "price_desc":
                queryBuilder.withSort(co.elastic.clients.elasticsearch._types.SortOptions.of(s -> s
                        .field(f -> f.field("price").order(SortOrder.Desc))
                ));
                break;
            case "date_asc":
                queryBuilder.withSort(co.elastic.clients.elasticsearch._types.SortOptions.of(s -> s
                        .field(f -> f.field("createdAt").order(SortOrder.Asc))
                ));
                break;
            case "date_desc":
                queryBuilder.withSort(co.elastic.clients.elasticsearch._types.SortOptions.of(s -> s
                        .field(f -> f.field("createdAt").order(SortOrder.Desc))
                ));
                break;
            case "title_asc":
                queryBuilder.withSort(co.elastic.clients.elasticsearch._types.SortOptions.of(s -> s
                        .field(f -> f.field("title").order(SortOrder.Asc))
                ));
                break;
            case "title_desc":
                queryBuilder.withSort(co.elastic.clients.elasticsearch._types.SortOptions.of(s -> s
                        .field(f -> f.field("title").order(SortOrder.Desc))
                ));
                break;
            default:
                // Default sorting
                queryBuilder.withSort(co.elastic.clients.elasticsearch._types.SortOptions.of(s -> s
                        .field(f -> f.field("createdAt").order(SortOrder.Desc))
                ));
        }
    }
}

