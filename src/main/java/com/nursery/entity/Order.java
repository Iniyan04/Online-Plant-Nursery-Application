package com.nursery.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "booking_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookingOrderId;

    private LocalDate orderDate;
    private String transactionMode;
    private int quantity;
    private double totalCost;

    @ManyToOne
    @JoinColumn(name = "planter_id")
    private Planter planters;

    @ManyToOne
    @JoinColumn(name = "plant_id")
    private Plant plant;

    @ManyToOne
    @JoinColumn(name = "seed_id")
    private Seed seed;

    public Order() {
    }

    public Order(LocalDate orderDate, String transactionMode, int quantity, double totalCost, Planter planters) {
        this.orderDate = orderDate;
        this.transactionMode = transactionMode;
        this.quantity = quantity;
        this.totalCost = totalCost;
        this.planters = planters;
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

    public Planter getPlanters() {
        return planters;
    }

    public void setPlanters(Planter planters) {
        this.planters = planters;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Seed getSeed() {
        return seed;
    }

    public void setSeed(Seed seed) {
        this.seed = seed;
    }
}
