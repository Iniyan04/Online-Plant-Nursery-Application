package com.nursery.service;

import com.nursery.entity.Plant;
import com.nursery.exception.InvalidPlantDataException;
import com.nursery.exception.PlantNotFoundException;
import com.nursery.repository.IPlantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PlantServiceImpl (US-003: View Plants, US-004: Add Plant,
 * US-005: Update Plant, US-006: Delete Plant).
 * The repository is mocked - these tests exercise ONLY the service's
 * business logic, with no real database involved.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PlantServiceImpl (US-003 to US-006)")
class PlantServiceImplTest {

    @Mock
    private IPlantRepository plantRepository;

    private IPlantService plantService;

    @BeforeEach
    void setUp() {
        plantService = new PlantServiceImpl(plantRepository);
    }

    private Plant validPlant() {
        return new Plant(30, "Medium", "Tulsi", "Summer", "Medicinal",
                "Easy", "Warm", "Herb", "A fragrant medicinal herb", 50, 99.99);
    }

    // ----------------------------------------------------------------
    // US-004: Add Plant
    // ----------------------------------------------------------------

    @Test
    @DisplayName("addPlant saves and returns the plant when data is valid")
    void addPlant_withValidData_returnsSavedPlant() {
        Plant plant = validPlant();
        when(plantRepository.addPlant(plant)).thenReturn(plant);

        Plant result = plantService.addPlant(plant);

        assertNotNull(result);
        assertEquals("Tulsi", result.getCommonName());
        verify(plantRepository, times(1)).addPlant(plant);
    }

    @Test
    @DisplayName("addPlant throws InvalidPlantDataException when commonName is blank")
    void addPlant_withBlankCommonName_throwsException() {
        Plant plant = new Plant(30, "Medium", "  ", "Summer", "Medicinal",
                "Easy", "Warm", "Herb", "desc", 50, 99.99);

        assertThrows(InvalidPlantDataException.class, () -> plantService.addPlant(plant));
        verify(plantRepository, never()).addPlant(any());
    }

    @Test
    @DisplayName("addPlant throws InvalidPlantDataException when typeOfPlant is blank")
    void addPlant_withBlankTypeOfPlant_throwsException() {
        Plant plant = new Plant(30, "Medium", "Tulsi", "Summer", "Medicinal",
                "Easy", "Warm", "  ", "desc", 50, 99.99);

        assertThrows(InvalidPlantDataException.class, () -> plantService.addPlant(plant));
        verify(plantRepository, never()).addPlant(any());
    }

    @Test
    @DisplayName("addPlant throws InvalidPlantDataException when plantCost is negative")
    void addPlant_withNegativeCost_throwsException() {
        Plant plant = new Plant(30, "Medium", "Tulsi", "Summer", "Medicinal",
                "Easy", "Warm", "Herb", "desc", 50, -10.0);

        assertThrows(InvalidPlantDataException.class, () -> plantService.addPlant(plant));
        verify(plantRepository, never()).addPlant(any());
    }

    @Test
    @DisplayName("addPlant throws InvalidPlantDataException when plantsStock is negative")
    void addPlant_withNegativeStock_throwsException() {
        Plant plant = new Plant(30, "Medium", "Tulsi", "Summer", "Medicinal",
                "Easy", "Warm", "Herb", "desc", -5, 99.99);

        assertThrows(InvalidPlantDataException.class, () -> plantService.addPlant(plant));
        verify(plantRepository, never()).addPlant(any());
    }

    @Test
    @DisplayName("addPlant throws InvalidPlantDataException when plant is null")
    void addPlant_withNullPlant_throwsException() {
        assertThrows(InvalidPlantDataException.class, () -> plantService.addPlant(null));
        verify(plantRepository, never()).addPlant(any());
    }

    // ----------------------------------------------------------------
    // US-005: Update Plant
    // ----------------------------------------------------------------

    @Test
    @DisplayName("updatePlant saves and returns the plant when it exists and data is valid")
    void updatePlant_existingPlantValidData_returnsUpdatedPlant() {
        Plant plant = validPlant();
        plant.setPlantId(1);
        when(plantRepository.viewPlant(1)).thenReturn(plant);
        when(plantRepository.updatePlant(plant)).thenReturn(plant);

        Plant result = plantService.updatePlant(plant);

        assertNotNull(result);
        verify(plantRepository, times(1)).updatePlant(plant);
    }

    @Test
    @DisplayName("updatePlant throws PlantNotFoundException when the plant does not exist")
    void updatePlant_nonExistentPlant_throwsException() {
        Plant plant = validPlant();
        plant.setPlantId(999);
        when(plantRepository.viewPlant(999)).thenReturn(null);

        assertThrows(PlantNotFoundException.class, () -> plantService.updatePlant(plant));
        verify(plantRepository, never()).updatePlant(any());
    }

    @Test
    @DisplayName("updatePlant throws InvalidPlantDataException when commonName is blank")
    void updatePlant_withBlankCommonName_throwsException() {
        Plant plant = new Plant(30, "Medium", " ", "Summer", "Medicinal",
                "Easy", "Warm", "Herb", "desc", 50, 99.99);
        plant.setPlantId(1);

        assertThrows(InvalidPlantDataException.class, () -> plantService.updatePlant(plant));
        verify(plantRepository, never()).updatePlant(any());
    }

    @Test
    @DisplayName("updatePlant throws InvalidPlantDataException when plantCost is negative")
    void updatePlant_withNegativeCost_throwsException() {
        Plant plant = new Plant(30, "Medium", "Tulsi", "Summer", "Medicinal",
                "Easy", "Warm", "Herb", "desc", 50, -1.0);
        plant.setPlantId(1);

        assertThrows(InvalidPlantDataException.class, () -> plantService.updatePlant(plant));
        verify(plantRepository, never()).updatePlant(any());
    }

    // ----------------------------------------------------------------
    // US-006: Delete Plant
    // ----------------------------------------------------------------

    @Test
    @DisplayName("deletePlant removes and returns the plant when it exists")
    void deletePlant_existingPlant_returnsDeletedPlant() {
        Plant plant = validPlant();
        plant.setPlantId(1);
        when(plantRepository.viewPlant(1)).thenReturn(plant);
        when(plantRepository.deletePlant(plant)).thenReturn(plant);

        Plant result = plantService.deletePlant(plant);

        assertNotNull(result);
        verify(plantRepository, times(1)).deletePlant(plant);
    }

    @Test
    @DisplayName("deletePlant throws PlantNotFoundException when the plant does not exist")
    void deletePlant_nonExistentPlant_throwsException() {
        Plant plant = validPlant();
        plant.setPlantId(999);
        when(plantRepository.viewPlant(999)).thenReturn(null);

        assertThrows(PlantNotFoundException.class, () -> plantService.deletePlant(plant));
        verify(plantRepository, never()).deletePlant(any());
    }

    @Test
    @DisplayName("deletePlant throws InvalidPlantDataException when plant is null")
    void deletePlant_withNullPlant_throwsException() {
        assertThrows(InvalidPlantDataException.class, () -> plantService.deletePlant(null));
        verify(plantRepository, never()).deletePlant(any());
    }

    // ----------------------------------------------------------------
    // US-003: View Plants
    // ----------------------------------------------------------------

    @Test
    @DisplayName("viewPlant(int) returns the plant when found by ID")
    void viewPlantById_existingId_returnsPlant() {
        Plant plant = validPlant();
        plant.setPlantId(1);
        when(plantRepository.viewPlant(1)).thenReturn(plant);

        Plant result = plantService.viewPlant(1);

        assertNotNull(result);
        assertEquals("Tulsi", result.getCommonName());
    }

    @Test
    @DisplayName("viewPlant(int) throws PlantNotFoundException when ID does not exist")
    void viewPlantById_nonExistentId_throwsException() {
        when(plantRepository.viewPlant(999)).thenReturn(null);

        assertThrows(PlantNotFoundException.class, () -> plantService.viewPlant(999));
    }

    @Test
    @DisplayName("viewPlant(String) returns the plant when found by common name")
    void viewPlantByName_existingName_returnsPlant() {
        Plant plant = validPlant();
        when(plantRepository.viewPlant("Tulsi")).thenReturn(plant);

        Plant result = plantService.viewPlant("Tulsi");

        assertNotNull(result);
        assertEquals("Tulsi", result.getCommonName());
    }

    @Test
    @DisplayName("viewPlant(String) throws PlantNotFoundException when name does not exist")
    void viewPlantByName_nonExistentName_throwsException() {
        when(plantRepository.viewPlant("Ghost Plant")).thenReturn(null);

        assertThrows(PlantNotFoundException.class, () -> plantService.viewPlant("Ghost Plant"));
    }

    @Test
    @DisplayName("viewAllPlants returns the full list from the repository")
    void viewAllPlants_returnsAllPlants() {
        List<Plant> plants = Collections.singletonList(validPlant());
        when(plantRepository.viewAllPlants()).thenReturn(plants);

        List<Plant> result = plantService.viewAllPlants();

        assertEquals(1, result.size());
        verify(plantRepository, times(1)).viewAllPlants();
    }

    @Test
    @DisplayName("viewAllPlants(typeOfPlant) returns the filtered list from the repository")
    void viewAllPlantsByType_returnsFilteredPlants() {
        List<Plant> plants = Collections.singletonList(validPlant());
        when(plantRepository.viewAllPlants("Herb")).thenReturn(plants);

        List<Plant> result = plantService.viewAllPlants("Herb");

        assertEquals(1, result.size());
        verify(plantRepository, times(1)).viewAllPlants("Herb");
    }
}
