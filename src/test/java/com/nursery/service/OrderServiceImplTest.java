package com.nursery.service;

import com.nursery.entity.Order;
import com.nursery.entity.Plant;
import com.nursery.entity.Seed;
import com.nursery.exception.InvalidOrderDataException;
import com.nursery.exception.OrderNotFoundException;
import com.nursery.exception.PlantNotFoundException;
import com.nursery.exception.SeedNotFoundException;
import com.nursery.repository.IOrderRepository;
import com.nursery.repository.IPlantRepository;
import com.nursery.repository.ISeedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderServiceImpl (US-011 to US-012)")
class OrderServiceImplTest {

    @Mock
    private IOrderRepository orderRepository;

    @Mock
    private IPlantRepository plantRepository;

    @Mock
    private ISeedRepository seedRepository;

    private IOrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, plantRepository, seedRepository);
    }

    private Plant validPlant() {
        Plant plant = new Plant(30, "Medium", "Rose", "Summer", "Ornamental",
                "Easy", "Warm", "Flower", "Test plant", 50, 100.0);
        plant.setPlantId(1);
        return plant;
    }

    private Seed validSeed() {
        Seed seed = new Seed("Basil", "Summer", "Daily", "Easy", "Warm",
                "Herb", "Aromatic herb seeds", 50, 25.0, 20);
        seed.setSeedId(1);
        return seed;
    }

    @Test
    @DisplayName("orderPlant creates order and decrements plant stock when valid")
    void orderPlant_withValidData_createsOrderAndDecrementsStock() {
        Plant plant = validPlant();
        when(plantRepository.viewPlant(1)).thenReturn(plant);
        when(plantRepository.updatePlant(any(Plant.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.addOrder(any(Order.class))).thenAnswer(inv -> {
            Order order = inv.getArgument(0);
            order.setBookingOrderId(1);
            return order;
        });

        Order result = orderService.orderPlant(1, 2, "UPI");

        assertNotNull(result);
        assertEquals(200.0, result.getTotalCost());
        assertEquals(2, result.getQuantity());
        assertEquals(48, plant.getPlantsStock());
        verify(plantRepository).updatePlant(plant);
        verify(orderRepository).addOrder(any(Order.class));
    }

    @Test
    @DisplayName("orderPlant throws PlantNotFoundException when plant does not exist")
    void orderPlant_nonExistentPlant_throwsException() {
        when(plantRepository.viewPlant(999)).thenReturn(null);

        assertThrows(PlantNotFoundException.class,
                () -> orderService.orderPlant(999, 1, "UPI"));
        verify(orderRepository, never()).addOrder(any());
    }

    @Test
    @DisplayName("orderPlant throws InvalidOrderDataException when stock is insufficient")
    void orderPlant_insufficientStock_throwsException() {
        Plant plant = validPlant();
        plant.setPlantsStock(1);
        when(plantRepository.viewPlant(1)).thenReturn(plant);

        assertThrows(InvalidOrderDataException.class,
                () -> orderService.orderPlant(1, 5, "UPI"));
        verify(orderRepository, never()).addOrder(any());
    }

    @Test
    @DisplayName("orderPlant throws InvalidOrderDataException when quantity is invalid")
    void orderPlant_invalidQuantity_throwsException() {
        assertThrows(InvalidOrderDataException.class,
                () -> orderService.orderPlant(1, 0, "UPI"));
        verify(plantRepository, never()).viewPlant(anyInt());
    }

    @Test
    @DisplayName("orderSeed creates order and decrements seed stock when valid")
    void orderSeed_withValidData_createsOrderAndDecrementsStock() {
        Seed seed = validSeed();
        when(seedRepository.viewSeed(1)).thenReturn(seed);
        when(seedRepository.updateSeed(any(Seed.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.addOrder(any(Order.class))).thenAnswer(inv -> {
            Order order = inv.getArgument(0);
            order.setBookingOrderId(2);
            return order;
        });

        Order result = orderService.orderSeed(1, 3, "Card");

        assertNotNull(result);
        assertEquals(75.0, result.getTotalCost());
        assertEquals(47, seed.getSeedsStock());
        verify(seedRepository).updateSeed(seed);
    }

    @Test
    @DisplayName("orderSeed throws SeedNotFoundException when seed does not exist")
    void orderSeed_nonExistentSeed_throwsException() {
        when(seedRepository.viewSeed(999)).thenReturn(null);

        assertThrows(SeedNotFoundException.class,
                () -> orderService.orderSeed(999, 1, "Card"));
        verify(orderRepository, never()).addOrder(any());
    }

    @Test
    @DisplayName("viewOrder throws OrderNotFoundException when order does not exist")
    void viewOrder_nonExistentOrder_throwsException() {
        when(orderRepository.viewOrder(999)).thenReturn(null);

        assertThrows(OrderNotFoundException.class, () -> orderService.viewOrder(999));
    }

    @Test
    @DisplayName("viewAllOrders returns the full list from the repository")
    void viewAllOrders_returnsAllOrders() {
        Order order = new Order(LocalDate.now(), "UPI", 2, 200.0, null);
        when(orderRepository.viewAllOrders()).thenReturn(Collections.singletonList(order));

        List<Order> result = orderService.viewAllOrders();

        assertEquals(1, result.size());
    }
}
