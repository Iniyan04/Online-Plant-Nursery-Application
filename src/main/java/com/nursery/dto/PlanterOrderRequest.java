package com.nursery.dto;

/**
 * Request body for US-013: Customer orders planters.
 */
public class PlanterOrderRequest {

    private int customerId;
    private int planterId;
    private int quantity;
    private String transactionMode;

    public PlanterOrderRequest() {
    }

    public PlanterOrderRequest(int customerId, int planterId, int quantity, String transactionMode) {
        this.customerId = customerId;
        this.planterId = planterId;
        this.quantity = quantity;
        this.transactionMode = transactionMode;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getPlanterId() {
        return planterId;
    }

    public void setPlanterId(int planterId) {
        this.planterId = planterId;
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
