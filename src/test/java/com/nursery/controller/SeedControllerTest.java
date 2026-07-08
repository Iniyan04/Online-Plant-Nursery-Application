package com.nursery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nursery.entity.Seed;
import com.nursery.exception.InvalidSeedDataException;
import com.nursery.exception.SeedNotFoundException;
import com.nursery.service.IAdminService;
import com.nursery.service.ISeedService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SeedController.class)
@DisplayName("SeedController (HTTP layer)")
class SeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ISeedService seedService;

    @MockBean
    private IAdminService adminService;

    private Seed sampleSeed() {
        Seed seed = new Seed("Basil", "Summer", "Daily", "Easy", "Warm",
                "Herb", "Aromatic herb seeds", 100, 49.99, 20);
        seed.setSeedId(1);
        return seed;
    }

    @Test
    @DisplayName("GET /api/seeds returns 200 and the full seed list")
    void viewAllSeeds_returns200WithList() throws Exception {
        when(seedService.viewAllSeeds()).thenReturn(List.of(sampleSeed()));

        mockMvc.perform(get("/api/seeds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commonName").value("Basil"));
    }

    @Test
    @DisplayName("GET /api/seeds/{id} returns 404 when the seed does not exist")
    void viewSeedById_nonExistentId_returns404() throws Exception {
        when(seedService.viewSeed(999)).thenThrow(new SeedNotFoundException("No seed found with ID 999"));

        mockMvc.perform(get("/api/seeds/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/seeds returns 201 when the seed is valid")
    void addSeed_validData_returns201() throws Exception {
        Seed seed = sampleSeed();
        when(seedService.addSeed(any(Seed.class))).thenReturn(seed);

        mockMvc.perform(post("/api/seeds")
                        .header("adminUsername", "admin")
                        .header("adminPassword", "admin123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seed)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commonName").value("Basil"));
    }

    @Test
    @DisplayName("POST /api/seeds returns 400 when the seed data is invalid")
    void addSeed_invalidData_returns400() throws Exception {
        when(seedService.addSeed(any(Seed.class)))
                .thenThrow(new InvalidSeedDataException("Seed common name must not be empty"));

        mockMvc.perform(post("/api/seeds")
                        .header("adminUsername", "admin")
                        .header("adminPassword", "admin123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Seed())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/seeds/{id} returns 204 when the seed exists")
    void deleteSeed_existingSeed_returns204() throws Exception {
        when(seedService.deleteSeed(any(Seed.class))).thenReturn(sampleSeed());

        mockMvc.perform(delete("/api/seeds/1")
                        .header("adminUsername", "admin")
                        .header("adminPassword", "admin123"))
                .andExpect(status().isNoContent());
    }
}
