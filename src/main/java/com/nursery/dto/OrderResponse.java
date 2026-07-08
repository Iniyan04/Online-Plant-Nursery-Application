package com.nursery.dto;

import com.nursery.entity.Order;

import java.time.LocalDate;

public class OrderResponse {

    private int bookingOrderId;
    private LocalDate orderDate;
    private String transactionMode;
    private int quantity;
    private double totalCost;
    private String orderStatus;
    private int customerId;
    private String itemType;
    private int itemId;
    private String itemName;

    public OrderResponse() {
    }

    public OrderResponse(int bookingOrderId, LocalDate orderDate, String transactionMode, int quantity,
                         double totalCost, String orderStatus, int customerId, String itemType, int itemId, String itemName) {
        this.bookingOrderId = bookingOrderId;
        this.orderDate = orderDate;
        this.transactionMode = transactionMode;
        this.quantity = quantity;
        this.totalCost = totalCost;
        this.orderStatus = orderStatus;
        this.customerId = customerId;
        this.itemType = itemType;
        this.itemId = itemId;
        this.itemName = itemName;
    }

    public static OrderResponse fromEntity(Order order) {
        if (order.getPlant() != null) {
            return new OrderResponse(
                    order.getBookingOrderId(),
                    order.getOrderDate(),
                    order.getTransactionMode(),
                    order.getQuantity(),
                    order.getTotalCost(),
                    order.getOrderStatus(),
                    order.getCustomer().getCustomerId(),
                    "Plant",
                    order.getPlant().getPlantId(),
                    order.getPlant().getCommonName()
            );
        }
        if (order.getSeed() != null) {
            return new OrderResponse(
                    order.getBookingOrderId(),
                    order.getOrderDate(),
                    order.getTransactionMode(),
                    order.getQuantity(),
                    order.getTotalCost(),
                    order.getOrderStatus(),
                    order.getCustomer().getCustomerId(),
                    "Seed",
                    order.getSeed().getSeedId(),
                    order.getSeed().getCommonName()
            );
        }
        if (order.getPlanters() != null) {
            return new OrderResponse(
                    order.getBookingOrderId(),
                    order.getOrderDate(),
                    order.getTransactionMode(),
                    order.getQuantity(),
                    order.getTotalCost(),
                    order.getOrderStatus(),
                    order.getCustomer().getCustomerId(),
                    "Planter",
                    order.getPlanters().getPlanterId(),
                    order.getPlanters().getPlanterShape()
            );
        }
        return new OrderResponse(
                order.getBookingOrderId(),
                order.getOrderDate(),
                order.getTransactionMode(),
                order.getQuantity(),
                order.getTotalCost(),
                order.getOrderStatus(),
                order.getCustomer() != null ? order.getCustomer().getCustomerId() : 0,
                "Unknown",
                0,
                null
        );
    }

    public int getBookingOrderId() {
        return bookingOrderId;
    }

    public void setBookingOrderId(int bookingOrderId) {
        this.bookingOrderId = bookingOrderId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
