package com.nursery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nursery.entity.Plant;
import com.nursery.exception.InvalidPlantDataException;
import com.nursery.exception.PlantNotFoundException;
import com.nursery.service.IPlantService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-layer tests for PlantController (US-003 to US-006).
 * The service layer is mocked - these tests verify ONLY the HTTP wiring.
 */
@WebMvcTest(PlantController.class)
@DisplayName("PlantController (HTTP layer)")
class PlantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IPlantService plantService;

    private Plant samplePlant() {
        Plant plant = new Plant(30, "Medium", "Tulsi", "Summer", "Medicinal",
                "Easy", "Warm", "Herb", "A fragrant medicinal herb", 50, 99.99);
        plant.setPlantId(1);
        return plant;
    }

    @Test
    @DisplayName("GET /api/plants returns 200 and the full plant list")
    void viewAllPlants_returns200WithList() throws Exception {
        when(plantService.viewAllPlants()).thenReturn(List.of(samplePlant()));

        mockMvc.perform(get("/api/plants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commonName").value("Tulsi"));
    }

    @Test
    @DisplayName("GET /api/plants?type=Herb returns the filtered plant list")
    void viewAllPlantsByType_returns200WithFilteredList() throws Exception {
        when(plantService.viewAllPlants("Herb")).thenReturn(List.of(samplePlant()));

        mockMvc.perform(get("/api/plants").param("type", "Herb"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].typeOfPlant").value("Herb"));
    }

    @Test
    @DisplayName("GET /api/plants/{id} returns 200 when the plant exists")
    void viewPlantById_existingId_returns200() throws Exception {
        when(plantService.viewPlant(1)).thenReturn(samplePlant());

        mockMvc.perform(get("/api/plants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commonName").value("Tulsi"));
    }

    @Test
    @DisplayName("GET /api/plants/{id} returns 404 when the plant does not exist")
    void viewPlantById_nonExistentId_returns404() throws Exception {
        when(plantService.viewPlant(999)).thenThrow(new PlantNotFoundException("No plant found with ID 999"));

        mockMvc.perform(get("/api/plants/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/plants returns 201 when the plant is valid")
    void addPlant_validData_returns201() throws Exception {
        Plant plant = samplePlant();
        when(plantService.addPlant(any(Plant.class))).thenReturn(plant);

        mockMvc.perform(post("/api/plants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plant)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commonName").value("Tulsi"));
    }

    @Test
    @DisplayName("POST /api/plants returns 400 when the plant data is invalid")
    void addPlant_invalidData_returns400() throws Exception {
        when(plantService.addPlant(any(Plant.class)))
                .thenThrow(new InvalidPlantDataException("Plant common name must not be empty"));

        Plant invalidPlant = new Plant();

        mockMvc.perform(post("/api/plants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPlant)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/plants/{id} returns 200 when the plant exists and data is valid")
    void updatePlant_existingPlant_returns200() throws Exception {
        Plant plant = samplePlant();
        when(plantService.updatePlant(any(Plant.class))).thenReturn(plant);

        mockMvc.perform(put("/api/plants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commonName").value("Tulsi"));
    }

    @Test
    @DisplayName("PUT /api/plants/{id} returns 404 when the plant does not exist")
    void updatePlant_nonExistentPlant_returns404() throws Exception {
        when(plantService.updatePlant(any(Plant.class)))
                .thenThrow(new PlantNotFoundException("No plant found with ID 999"));

        Plant plant = samplePlant();

        mockMvc.perform(put("/api/plants/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plant)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/plants/{id} returns 204 when the plant exists")
    void deletePlant_existingPlant_returns204() throws Exception {
        when(plantService.deletePlant(any(Plant.class))).thenReturn(samplePlant());

        mockMvc.perform(delete("/api/plants/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/plants/{id} returns 404 when the plant does not exist")
    void deletePlant_nonExistentPlant_returns404() throws Exception {
        when(plantService.deletePlant(any(Plant.class)))
                .thenThrow(new PlantNotFoundException("No plant found with ID 999"));

        mockMvc.perform(delete("/api/plants/999"))
                .andExpect(status().isNotFound());
    }
}
