package com.nursery.repository;

import com.nursery.entity.Plant;
import java.util.List;

public interface IPlantRepository {

    Plant addPlant(Plant plant);

    Plant updatePlant(Plant plant);

    Plant deletePlant(Plant plant);

    Plant viewPlant(int plantId);

    Plant viewPlant(String commonName);

    List<Plant> viewAllPlants();

    List<Plant> viewAllPlants(String typeOfPlant);
}
