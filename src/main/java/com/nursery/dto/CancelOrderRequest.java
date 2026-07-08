package com.nursery.dto;

/**
 * Request body for US-015: Customer cancels an existing order.
 */
public class CancelOrderRequest {

    private int customerId;

    public CancelOrderRequest() {
    }

    public CancelOrderRequest(int customerId) {
        this.customerId = customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
