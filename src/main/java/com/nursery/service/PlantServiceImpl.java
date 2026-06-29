package com.nursery.service;

import com.nursery.entity.Plant;
import com.nursery.exception.InvalidPlantDataException;
import com.nursery.exception.PlantNotFoundException;
import com.nursery.repository.IPlantRepository;

import java.util.List;

public class PlantServiceImpl implements IPlantService {

    private final IPlantRepository plantRepository;

    public PlantServiceImpl(IPlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    @Override
    public Plant addPlant(Plant plant) {
        validatePlantData(plant);
        return plantRepository.addPlant(plant);
    }

    @Override
    public Plant updatePlant(Plant plant) {
        validatePlantData(plant);

        Plant existing = plantRepository.viewPlant(plant.getPlantId());
        if (existing == null) {
            throw new PlantNotFoundException(
                    "No plant found with ID " + plant.getPlantId() + " to update");
        }

        return plantRepository.updatePlant(plant);
    }

    @Override
    public Plant deletePlant(Plant plant) {
        if (plant == null) {
            throw new InvalidPlantDataException("Plant must not be null");
        }

        Plant existing = plantRepository.viewPlant(plant.getPlantId());
        if (existing == null) {
            throw new PlantNotFoundException(
                    "No plant found with ID " + plant.getPlantId() + " to delete");
        }

        return plantRepository.deletePlant(plant);
    }

    @Override
    public Plant viewPlant(int plantId) {
        Plant plant = plantRepository.viewPlant(plantId);
        if (plant == null) {
            throw new PlantNotFoundException("No plant found with ID " + plantId);
        }
        return plant;
    }

    @Override
    public Plant viewPlant(String commonName) {
        Plant plant = plantRepository.viewPlant(commonName);
        if (plant == null) {
            throw new PlantNotFoundException("No plant found with common name '" + commonName + "'");
        }
        return plant;
    }

    @Override
    public List<Plant> viewAllPlants() {
        return plantRepository.viewAllPlants();
    }

    @Override
    public List<Plant> viewAllPlants(String typeOfPlant) {
        return plantRepository.viewAllPlants(typeOfPlant);
    }

    private void validatePlantData(Plant plant) {
        if (plant == null) {
            throw new InvalidPlantDataException("Plant must not be null");
        }
        if (isBlank(plant.getCommonName())) {
            throw new InvalidPlantDataException("Plant common name must not be empty");
        }
        if (isBlank(plant.getTypeOfPlant())) {
            throw new InvalidPlantDataException("Plant type must not be empty");
        }
        if (plant.getPlantCost() < 0) {
            throw new InvalidPlantDataException("Plant cost must not be negative");
        }
        if (plant.getPlantsStock() < 0) {
            throw new InvalidPlantDataException("Plant stock must not be negative");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
