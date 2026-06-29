package com.nursery.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "plant")
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int plantId;

    private int plantHeight;
    private String plantSpread;
    private String commonName;
    private String bloomTime;
    private String medicinalOrCulinaryUse;
    private String difficultyLevel;
    private String temparature;
    private String typeOfPlant;
    private String plantDescription;
    private int plantsStock;
    private double plantCost;

    public Plant() {
    }

    public Plant(int plantHeight, String plantSpread, String commonName, String bloomTime,
                 String medicinalOrCulinaryUse, String difficultyLevel, String temparature,
                 String typeOfPlant, String plantDescription, int plantsStock, double plantCost) {
        this.plantHeight = plantHeight;
        this.plantSpread = plantSpread;
        this.commonName = commonName;
        this.bloomTime = bloomTime;
        this.medicinalOrCulinaryUse = medicinalOrCulinaryUse;
        this.difficultyLevel = difficultyLevel;
        this.temparature = temparature;
        this.typeOfPlant = typeOfPlant;
        this.plantDescription = plantDescription;
        this.plantsStock = plantsStock;
        this.plantCost = plantCost;
    }

    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    public int getPlantHeight() {
        return plantHeight;
    }

    public void setPlantHeight(int plantHeight) {
        this.plantHeight = plantHeight;
    }

    public String getPlantSpread() {
        return plantSpread;
    }

    public void setPlantSpread(String plantSpread) {
        this.plantSpread = plantSpread;
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

    public String getMedicinalOrCulinaryUse() {
        return medicinalOrCulinaryUse;
    }

    public void setMedicinalOrCulinaryUse(String medicinalOrCulinaryUse) {
        this.medicinalOrCulinaryUse = medicinalOrCulinaryUse;
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

    public String getTypeOfPlant() {
        return typeOfPlant;
    }

    public void setTypeOfPlant(String typeOfPlant) {
        this.typeOfPlant = typeOfPlant;
    }

    public String getPlantDescription() {
        return plantDescription;
    }

    public void setPlantDescription(String plantDescription) {
        this.plantDescription = plantDescription;
    }

    public int getPlantsStock() {
        return plantsStock;
    }

    public void setPlantsStock(int plantsStock) {
        this.plantsStock = plantsStock;
    }

    public double getPlantCost() {
        return plantCost;
    }

    public void setPlantCost(double plantCost) {
        this.plantCost = plantCost;
    }
}
