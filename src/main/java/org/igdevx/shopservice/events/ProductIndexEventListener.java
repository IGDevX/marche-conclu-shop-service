package org.igdevx.shopservice.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.igdevx.shopservice.elasticsearch.services.ProductIndexService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductIndexEventListener {

    private final ProductIndexService productIndexService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleProductIndexEvent(ProductIndexEvent event) {
        log.info("🔔 Événement reçu après commit: {} pour produit {}", event.action(), event.productId());

        try {
            if ("UPDATE".equals(event.action())) {
                log.info("📝 Indexation du produit {}", event.productId());
                productIndexService.indexProductById(event.productId());
                log.info("✅ Produit {} indexé avec succès", event.productId());
            } else if ("DELETE".equals(event.action())) {
                log.info("🗑️ Suppression du produit {} de l'index", event.productId());
                productIndexService.deleteFromIndex(event.productId());
                log.info("✅ Produit {} supprimé de l'index", event.productId());
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors du traitement de l'événement pour le produit {}: {}",
                     event.productId(), e.getMessage(), e);
        }
    }
}

