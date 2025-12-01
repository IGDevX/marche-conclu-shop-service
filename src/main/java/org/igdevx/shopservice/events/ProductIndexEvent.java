package org.igdevx.shopservice.events;

public record ProductIndexEvent(Long productId, String action) {
    public static ProductIndexEvent updated(Long id) {
        return new ProductIndexEvent(id, "UPDATE");
    }

    public static ProductIndexEvent deleted(Long id) {
        return new ProductIndexEvent(id, "DELETE");
    }
}

