package com.nursery.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "planter")
public class Planter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int planterId;

    private float planterheight;
    private int planterCapacity;
    private int drainageHoles;
    private int planterColor;
    private String planterShape;
    private int planterStock;
    private int planterCost;
    private String imageUrl;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "plant_id")
    private Plant plants;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "seed_id")
    private Seed seeds;

    public Planter() {
    }

    public Planter(float planterheight, int planterCapacity, int drainageHoles, int planterColor,
                   String planterShape, int planterStock, int planterCost, Plant plants, Seed seeds) {
        this.planterheight = planterheight;
        this.planterCapacity = planterCapacity;
        this.drainageHoles = drainageHoles;
        this.planterColor = planterColor;
        this.planterShape = planterShape;
        this.planterStock = planterStock;
        this.planterCost = planterCost;
        this.plants = plants;
        this.seeds = seeds;
    }

    public int getPlanterId() {
        return planterId;
    }

    public void setPlanterId(int planterId) {
        this.planterId = planterId;
    }

    public float getPlanterheight() {
        return planterheight;
    }

    public void setPlanterheight(float planterheight) {
        this.planterheight = planterheight;
    }

    public int getPlanterCapacity() {
        return planterCapacity;
    }

    public void setPlanterCapacity(int planterCapacity) {
        this.planterCapacity = planterCapacity;
    }

    public int getDrainageHoles() {
        return drainageHoles;
    }

    public void setDrainageHoles(int drainageHoles) {
        this.drainageHoles = drainageHoles;
    }

    public int getPlanterColor() {
        return planterColor;
    }

    public void setPlanterColor(int planterColor) {
        this.planterColor = planterColor;
    }

    public String getPlanterShape() {
        return planterShape;
    }

    public void setPlanterShape(String planterShape) {
        this.planterShape = planterShape;
    }

    public int getPlanterStock() {
        return planterStock;
    }

    public void setPlanterStock(int planterStock) {
        this.planterStock = planterStock;
    }

    public int getPlanterCost() {
        return planterCost;
    }

    public void setPlanterCost(int planterCost) {
        this.planterCost = planterCost;
    }

    public Plant getPlants() {
        return plants;
    }

    public void setPlants(Plant plants) {
        this.plants = plants;
    }

    public Seed getSeeds() {
        return seeds;
    }

    public void setSeeds(Seed seeds) {
        this.seeds = seeds;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
