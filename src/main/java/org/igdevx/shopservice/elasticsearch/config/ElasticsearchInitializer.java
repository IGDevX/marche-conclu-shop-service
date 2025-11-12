package org.igdevx.shopservice.elasticsearch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.igdevx.shopservice.elasticsearch.services.ProductIndexService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Initializes Elasticsearch index on application startup if needed
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchInitializer {

    private final ProductIndexService productIndexService;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeElasticsearchIndex() {
        try {
            log.info("Checking Elasticsearch index status...");

            long count = productIndexService.getIndexedProductsCount();

            if (count == 0) {
                log.warn("Elasticsearch index is empty. Starting automatic reindexation...");
                productIndexService.reindexAll();
                long newCount = productIndexService.getIndexedProductsCount();
                log.info("✅ Elasticsearch index initialized successfully with {} products", newCount);
            } else {
                log.info("✅ Elasticsearch index already contains {} products", count);
            }

        } catch (Exception e) {
            log.error("❌ Failed to initialize Elasticsearch index. You may need to run reindex-all manually.", e);
            log.error("To reindex manually, call: POST /api/products/index/reindex-all");
        }
    }
}

