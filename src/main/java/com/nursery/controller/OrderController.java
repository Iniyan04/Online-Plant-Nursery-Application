package com.nursery.controller;

import com.nursery.dto.PlantOrderRequest;
import com.nursery.dto.SeedOrderRequest;
import com.nursery.entity.Order;
import com.nursery.service.IOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for US-011 (Order Plants) and US-012 (Order Seeds).
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * US-011: Customer orders plants.
     * POST /api/orders/plants
     */
    @PostMapping("/plants")
    public ResponseEntity<Order> orderPlant(@RequestBody PlantOrderRequest request) {
        Order order = orderService.orderPlant(
                request.getPlantId(), request.getQuantity(), request.getTransactionMode());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * US-012: Customer orders seeds.
     * POST /api/orders/seeds
     */
    @PostMapping("/seeds")
    public ResponseEntity<Order> orderSeed(@RequestBody SeedOrderRequest request) {
        Order order = orderService.orderSeed(
                request.getSeedId(), request.getQuantity(), request.getTransactionMode());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * View a single order by ID.
     * GET /api/orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> viewOrderById(@PathVariable int id) {
        return ResponseEntity.ok(orderService.viewOrder(id));
    }

    /**
     * View all orders.
     * GET /api/orders
     */
    @GetMapping
    public ResponseEntity<List<Order>> viewAllOrders() {
        return ResponseEntity.ok(orderService.viewAllOrders());
    }
}
