package com.nursery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nursery.dto.DashboardResponse;
import com.nursery.dto.LoginRequest;
import com.nursery.entity.Admin;
import com.nursery.exception.InvalidCredentialsException;
import com.nursery.service.IAdminDashboardService;
import com.nursery.service.IAdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@DisplayName("AdminController (HTTP layer)")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IAdminService adminService;

    @MockBean
    private IAdminDashboardService adminDashboardService;

    @Test
    @DisplayName("POST /api/admins/login returns 200 and admin data on valid credentials")
    void login_validCredentials_returns200() throws Exception {
        Admin admin = new Admin("admin", "admin123");
        admin.setAdminId(1);
        when(adminService.validateAdmin("admin", "admin123")).thenReturn(admin);

        LoginRequest request = new LoginRequest("admin", "admin123");

        mockMvc.perform(post("/api/admins/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adminId").value(1))
                .andExpect(jsonPath("$.adminUsername").value("admin"));
    }

    @Test
    @DisplayName("POST /api/admins/login returns 401 on invalid credentials")
    void login_invalidCredentials_returns401() throws Exception {
        when(adminService.validateAdmin("admin", "wrongpass"))
                .thenThrow(new InvalidCredentialsException("Invalid admin username or password"));

        LoginRequest request = new LoginRequest("admin", "wrongpass");

        mockMvc.perform(post("/api/admins/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid admin username or password"));
    }

    @Test
    @DisplayName("GET /api/admins/dashboard returns 200 with dashboard metrics for valid admin")
    void getDashboard_validAdmin_returns200() throws Exception {
        DashboardResponse response = new DashboardResponse(5, 12, 7, 8, 4, 9, 3);
        when(adminDashboardService.getDashboardData()).thenReturn(response);

        mockMvc.perform(get("/api/admins/dashboard")
                        .header("adminUsername", "admin")
                        .header("adminPassword", "admin123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCustomers").value(5))
                .andExpect(jsonPath("$.totalOrders").value(12))
                .andExpect(jsonPath("$.totalPlants").value(7))
                .andExpect(jsonPath("$.totalSeeds").value(8))
                .andExpect(jsonPath("$.totalPlanters").value(4))
                .andExpect(jsonPath("$.activeOrders").value(9))
                .andExpect(jsonPath("$.cancelledOrders").value(3));

        verify(adminService).validateAdmin("admin", "admin123");
    }

    @Test
    @DisplayName("GET /api/admins/dashboard returns 401 for invalid admin credentials")
    void getDashboard_invalidAdmin_returns401() throws Exception {
        when(adminService.validateAdmin("admin", "wrongpass"))
                .thenThrow(new InvalidCredentialsException("Invalid admin username or password"));

        mockMvc.perform(get("/api/admins/dashboard")
                        .header("adminUsername", "admin")
                        .header("adminPassword", "wrongpass"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid admin username or password"));
    }
}
