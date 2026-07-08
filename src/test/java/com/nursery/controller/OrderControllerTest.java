package com.nursery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nursery.dto.CancelOrderRequest;
import com.nursery.entity.Customer;
import com.nursery.dto.PlantOrderRequest;
import com.nursery.dto.PlanterOrderRequest;
import com.nursery.dto.SeedOrderRequest;
import com.nursery.entity.Order;
import com.nursery.exception.InvalidOrderDataException;
import com.nursery.exception.PlantNotFoundException;
import com.nursery.exception.PlanterNotFoundException;
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
        order.setOrderStatus("ACTIVE");
        return order;
    }

    @Test
    @DisplayName("POST /api/orders/plants returns 201 when order is created")
    void orderPlant_validRequest_returns201() throws Exception {
        PlantOrderRequest request = new PlantOrderRequest(1, 1, 2, "UPI");
        when(orderService.orderPlant(1, 1, 2, "UPI")).thenReturn(sampleOrder());

        mockMvc.perform(post("/api/orders/plants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalCost").value(200.0));
    }

    @Test
    @DisplayName("POST /api/orders/plants returns 404 when plant does not exist")
    void orderPlant_nonExistentPlant_returns404() throws Exception {
        PlantOrderRequest request = new PlantOrderRequest(1, 999, 1, "UPI");
        when(orderService.orderPlant(1, 999, 1, "UPI"))
                .thenThrow(new PlantNotFoundException("No plant found with ID 999"));

        mockMvc.perform(post("/api/orders/plants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/orders/seeds returns 201 when order is created")
    void orderSeed_validRequest_returns201() throws Exception {
        SeedOrderRequest request = new SeedOrderRequest(1, 1, 3, "Card");
        Order order = new Order(LocalDate.now(), "Card", 3, 75.0, null);
        order.setBookingOrderId(2);
        when(orderService.orderSeed(1, 1, 3, "Card")).thenReturn(order);

        mockMvc.perform(post("/api/orders/seeds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    @DisplayName("POST /api/orders/seeds returns 400 when stock is insufficient")
    void orderSeed_insufficientStock_returns400() throws Exception {
        SeedOrderRequest request = new SeedOrderRequest(1, 1, 100, "Card");
        when(orderService.orderSeed(anyInt(), anyInt(), anyInt(), anyString()))
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

    @Test
    @DisplayName("POST /api/orders/planters returns 201 when order is created")
    void orderPlanter_validRequest_returns201() throws Exception {
        PlanterOrderRequest request = new PlanterOrderRequest(1, 1, 2, "Card");
        Order order = new Order(LocalDate.now(), "Card", 2, 300.0, null);
        order.setBookingOrderId(3);
        when(orderService.orderPlanter(1, 1, 2, "Card")).thenReturn(order);

        mockMvc.perform(post("/api/orders/planters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalCost").value(300.0));
    }

    @Test
    @DisplayName("POST /api/orders/planters returns 404 when planter does not exist")
    void orderPlanter_nonExistentPlanter_returns404() throws Exception {
        PlanterOrderRequest request = new PlanterOrderRequest(1, 999, 1, "Card");
        when(orderService.orderPlanter(1, 999, 1, "Card"))
                .thenThrow(new PlanterNotFoundException("No planter found with ID 999"));

        mockMvc.perform(post("/api/orders/planters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/orders/customer/{customerId} returns customer order history")
    void viewOrdersByCustomer_returns200WithOrderHistory() throws Exception {
        Customer customer = new Customer();
        customer.setCustomerId(1);
        Order order = sampleOrder();
        order.setCustomer(customer);
        when(orderService.viewOrdersByCustomer(1)).thenReturn(java.util.List.of(order));

        mockMvc.perform(get("/api/orders/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingOrderId").value(1))
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[0].orderStatus").value("ACTIVE"))
                .andExpect(jsonPath("$[0].itemType").value("Unknown"));
    }

    @Test
    @DisplayName("GET /api/orders/customer/{customerId} returns 400 when customer is invalid")
    void viewOrdersByCustomer_invalidCustomer_returns400() throws Exception {
        when(orderService.viewOrdersByCustomer(999))
                .thenThrow(new InvalidOrderDataException("Customer not found with ID 999"));

        mockMvc.perform(get("/api/orders/customer/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Customer not found with ID 999"));
    }

    @Test
    @DisplayName("PUT /api/orders/{orderId}/cancel returns 200 when cancellation succeeds")
    void cancelOrder_validRequest_returns200() throws Exception {
        Customer customer = new Customer();
        customer.setCustomerId(1);
        Order order = sampleOrder();
        order.setCustomer(customer);
        order.setOrderStatus("CANCELLED");
        when(orderService.cancelOrder(1, 1)).thenReturn(order);

        mockMvc.perform(put("/api/orders/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CancelOrderRequest(1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingOrderId").value(1))
                .andExpect(jsonPath("$.orderStatus").value("CANCELLED"));
    }

    @Test
    @DisplayName("PUT /api/orders/{orderId}/cancel returns 400 when order is already cancelled")
    void cancelOrder_duplicateCancellation_returns400() throws Exception {
        when(orderService.cancelOrder(1, 1))
                .thenThrow(new InvalidOrderDataException("Order is already cancelled"));

        mockMvc.perform(put("/api/orders/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CancelOrderRequest(1))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Order is already cancelled"));
    }
}
