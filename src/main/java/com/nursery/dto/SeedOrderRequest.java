package com.nursery.dto;

/**
 * Request body for US-012: Customer orders seeds.
 */
public class SeedOrderRequest {

    private int seedId;
    private int quantity;
    private String transactionMode;

    public SeedOrderRequest() {
    }

    public SeedOrderRequest(int seedId, int quantity, String transactionMode) {
        this.seedId = seedId;
        this.quantity = quantity;
        this.transactionMode = transactionMode;
    }

    public int getSeedId() {
        return seedId;
    }

    public void setSeedId(int seedId) {
        this.seedId = seedId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }
}
