package com.nursery.dto;

/**
 * Request body for US-011: Customer orders plants.
 */
public class PlantOrderRequest {

    private int plantId;
    private int quantity;
    private String transactionMode;

    public PlantOrderRequest() {
    }

    public PlantOrderRequest(int plantId, int quantity, String transactionMode) {
        this.plantId = plantId;
        this.quantity = quantity;
        this.transactionMode = transactionMode;
    }

    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
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
