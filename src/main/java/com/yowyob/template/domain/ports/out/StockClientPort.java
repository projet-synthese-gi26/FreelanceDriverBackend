package com.yowyob.template.domain.ports.out;

import reactor.core.publisher.Mono;

public interface StockClientPort {
    /**
     * Verify if the stock is full.
     * @return true full, false otherwise
     */
    Mono<Boolean> isStockFull(String productName);

    /**
     * Update the stock for a product.
     * @param productName the name of the product
     * @param quantity the quantity to update
     * @return Mono<Void>
     */
    Mono<Void> updateStock(String productName, int quantity);

    /**
     * Clear the stock for a product.
     * @param productName the name of the product
     * @return Mono<Void>
     */
    Mono<Void> clearStock(String productName);
}