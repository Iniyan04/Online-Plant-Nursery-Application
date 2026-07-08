package com.nursery.dto;

public class DashboardResponse {

    private long totalCustomers;
    private long totalOrders;
    private long totalPlants;
    private long totalSeeds;
    private long totalPlanters;
    private long activeOrders;
    private long cancelledOrders;

    public DashboardResponse() {
    }

    public DashboardResponse(long totalCustomers, long totalOrders, long totalPlants, long totalSeeds,
                             long totalPlanters, long activeOrders, long cancelledOrders) {
        this.totalCustomers = totalCustomers;
        this.totalOrders = totalOrders;
        this.totalPlants = totalPlants;
        this.totalSeeds = totalSeeds;
        this.totalPlanters = totalPlanters;
        this.activeOrders = activeOrders;
        this.cancelledOrders = cancelledOrders;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getTotalPlants() {
        return totalPlants;
    }

    public void setTotalPlants(long totalPlants) {
        this.totalPlants = totalPlants;
    }

    public long getTotalSeeds() {
        return totalSeeds;
    }

    public void setTotalSeeds(long totalSeeds) {
        this.totalSeeds = totalSeeds;
    }

    public long getTotalPlanters() {
        return totalPlanters;
    }

    public void setTotalPlanters(long totalPlanters) {
        this.totalPlanters = totalPlanters;
    }

    public long getActiveOrders() {
        return activeOrders;
    }

    public void setActiveOrders(long activeOrders) {
        this.activeOrders = activeOrders;
    }

    public long getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(long cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }
}
