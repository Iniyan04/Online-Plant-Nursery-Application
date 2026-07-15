package com.nursery.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "seed")
public class Seed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int seedId;

    private String commonName;
    private String bloomTime;
    private String watering;
    private String difficultyLevel;
    private String temparature;
    private String typeOfSeeds;
    private String seedsDescription;
    private int seedsStock;
    private double seedsCost;
    private int seedsPerPacket;
    private String imageUrl;

    public Seed() {
    }

    public Seed(String commonName, String bloomTime, String watering, String difficultyLevel,
                String temparature, String typeOfSeeds, String seedsDescription, int seedsStock,
                double seedsCost, int seedsPerPacket) {
        this.commonName = commonName;
        this.bloomTime = bloomTime;
        this.watering = watering;
        this.difficultyLevel = difficultyLevel;
        this.temparature = temparature;
        this.typeOfSeeds = typeOfSeeds;
        this.seedsDescription = seedsDescription;
        this.seedsStock = seedsStock;
        this.seedsCost = seedsCost;
        this.seedsPerPacket = seedsPerPacket;
    }

    public int getSeedId() {
        return seedId;
    }

    public void setSeedId(int seedId) {
        this.seedId = seedId;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getBloomTime() {
        return bloomTime;
    }

    public void setBloomTime(String bloomTime) {
        this.bloomTime = bloomTime;
    }

    public String getWatering() {
        return watering;
    }

    public void setWatering(String watering) {
        this.watering = watering;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getTemparature() {
        return temparature;
    }

    public void setTemparature(String temparature) {
        this.temparature = temparature;
    }

    public String getTypeOfSeeds() {
        return typeOfSeeds;
    }

    public void setTypeOfSeeds(String typeOfSeeds) {
        this.typeOfSeeds = typeOfSeeds;
    }

    public String getSeedsDescription() {
        return seedsDescription;
    }

    public void setSeedsDescription(String seedsDescription) {
        this.seedsDescription = seedsDescription;
    }

    public int getSeedsStock() {
        return seedsStock;
    }

    public void setSeedsStock(int seedsStock) {
        this.seedsStock = seedsStock;
    }

    public double getSeedsCost() {
        return seedsCost;
    }

    public void setSeedsCost(double seedsCost) {
        this.seedsCost = seedsCost;
    }

    public int getSeedsPerPacket() {
        return seedsPerPacket;
    }

    public void setSeedsPerPacket(int seedsPerPacket) {
        this.seedsPerPacket = seedsPerPacket;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
