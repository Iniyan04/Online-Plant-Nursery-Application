package com.nursery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nursery.dto.PlantOrderRequest;
import com.nursery.dto.SeedOrderRequest;
import com.nursery.entity.Order;
import com.nursery.exception.InvalidOrderDataException;
import com.nursery.exception.PlantNotFoundException;
import com.nursery.service.IOrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@DisplayName("OrderController (HTTP layer)")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IOrderService orderService;

    private Order sampleOrder() {
        Order order = new Order(LocalDate.now(), "UPI", 2, 200.0, null);
        order.setBookingOrderId(1);
        return order;
    }

    @Test
    @DisplayName("POST /api/orders/plants returns 201 when order is created")
    void orderPlant_validRequest_returns201() throws Exception {
        PlantOrderRequest request = new PlantOrderRequest(1, 2, "UPI");
        when(orderService.orderPlant(1, 2, "UPI")).thenReturn(sampleOrder());

        mockMvc.perform(post("/api/orders/plants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalCost").value(200.0));
    }

    @Test
    @DisplayName("POST /api/orders/plants returns 404 when plant does not exist")
    void orderPlant_nonExistentPlant_returns404() throws Exception {
        PlantOrderRequest request = new PlantOrderRequest(999, 1, "UPI");
        when(orderService.orderPlant(999, 1, "UPI"))
                .thenThrow(new PlantNotFoundException("No plant found with ID 999"));

        mockMvc.perform(post("/api/orders/plants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/orders/seeds returns 201 when order is created")
    void orderSeed_validRequest_returns201() throws Exception {
        SeedOrderRequest request = new SeedOrderRequest(1, 3, "Card");
        Order order = new Order(LocalDate.now(), "Card", 3, 75.0, null);
        order.setBookingOrderId(2);
        when(orderService.orderSeed(1, 3, "Card")).thenReturn(order);

        mockMvc.perform(post("/api/orders/seeds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    @DisplayName("POST /api/orders/seeds returns 400 when stock is insufficient")
    void orderSeed_insufficientStock_returns400() throws Exception {
        SeedOrderRequest request = new SeedOrderRequest(1, 100, "Card");
        when(orderService.orderSeed(anyInt(), anyInt(), anyString()))
                .thenThrow(new InvalidOrderDataException("Insufficient seed stock. Available: 5"));

        mockMvc.perform(post("/api/orders/seeds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/orders/{id} returns 200 when order exists")
    void viewOrderById_existingOrder_returns200() throws Exception {
        when(orderService.viewOrder(1)).thenReturn(sampleOrder());

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingOrderId").value(1));
    }
}
