package com.nursery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nursery.entity.Planter;
import com.nursery.exception.PlanterNotFoundException;
import com.nursery.service.IPlanterService;
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

@WebMvcTest(PlanterController.class)
@DisplayName("PlanterController (HTTP layer)")
class PlanterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IPlanterService planterService;

    private Planter samplePlanter() {
        Planter planter = new Planter(12.5f, 5, 3, 1, "Round", 20, 150, null, null);
        planter.setPlanterId(1);
        return planter;
    }

    @Test
    @DisplayName("GET /api/planters returns 200 and the full planter list")
    void viewAllPlanters_returns200WithList() throws Exception {
        when(planterService.viewAllPlanters()).thenReturn(List.of(samplePlanter()));

        mockMvc.perform(get("/api/planters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].planterShape").value("Round"));
    }

    @Test
    @DisplayName("GET /api/planters?minCost=10&maxCost=200 returns filtered list")
    void viewAllPlantersByCost_returns200WithFilteredList() throws Exception {
        when(planterService.viewAllPlanters(10.0, 200.0)).thenReturn(List.of(samplePlanter()));

        mockMvc.perform(get("/api/planters").param("minCost", "10").param("maxCost", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].planterCost").value(150));
    }

    @Test
    @DisplayName("POST /api/planters returns 201 when the planter is valid")
    void addPlanter_validData_returns201() throws Exception {
        Planter planter = samplePlanter();
        when(planterService.addPlanter(any(Planter.class))).thenReturn(planter);

        mockMvc.perform(post("/api/planters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planter)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.planterShape").value("Round"));
    }

    @Test
    @DisplayName("GET /api/planters/{id} returns 404 when the planter does not exist")
    void viewPlanterById_nonExistentId_returns404() throws Exception {
        when(planterService.viewPlanter(999))
                .thenThrow(new PlanterNotFoundException("No planter found with ID 999"));

        mockMvc.perform(get("/api/planters/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/planters/{id} returns 204 when the planter exists")
    void deletePlanter_existingPlanter_returns204() throws Exception {
        when(planterService.deletePlanter(any(Planter.class))).thenReturn(samplePlanter());

        mockMvc.perform(delete("/api/planters/1"))
                .andExpect(status().isNoContent());
    }
}
