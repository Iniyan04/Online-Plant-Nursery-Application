package com.nursery.service;

import com.nursery.dto.CancelOrderRequest;
import com.nursery.entity.Customer;
import com.nursery.entity.Order;
import com.nursery.entity.Plant;
import com.nursery.entity.Planter;
import com.nursery.entity.Seed;
import com.nursery.exception.InvalidOrderDataException;
import com.nursery.exception.OrderNotFoundException;
import com.nursery.exception.PlantNotFoundException;
import com.nursery.exception.PlanterNotFoundException;
import com.nursery.exception.SeedNotFoundException;
import com.nursery.repository.ICustomerRepository;
import com.nursery.repository.IOrderRepository;
import com.nursery.repository.IPlantRepository;
import com.nursery.repository.IPlanterRepository;
import com.nursery.repository.ISeedRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Mock
    private IPlanterRepository planterRepository;

    @Mock
    private ICustomerRepository customerRepository;

    private IOrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, plantRepository, seedRepository, planterRepository, customerRepository);
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

    private Planter validPlanter() {
        Planter planter = new Planter(12.5f, 5, 3, 1, "Round", 20, 150, null, null);
        planter.setPlanterId(1);
        return planter;
    }

    private Customer validCustomer() {
        Customer customer = new Customer("Jane Doe", "jane@example.com", "janedoe", "secret123", null);
        customer.setCustomerId(1);
        return customer;
    }

    private Order activeOrderForCustomer(int orderId) {
        Order order = new Order(LocalDate.now(), "UPI", 2, 200.0, null);
        order.setBookingOrderId(orderId);
        order.setCustomer(validCustomer());
        order.setOrderStatus(OrderServiceImpl.ORDER_STATUS_ACTIVE);
        return order;
    }

    @Test
    @DisplayName("orderPlant creates order and decrements plant stock when valid")
    void orderPlant_withValidData_createsOrderAndDecrementsStock() {
        Plant plant = validPlant();
        Customer customer = validCustomer();
        when(customerRepository.viewCustomer(1)).thenReturn(customer);
        when(plantRepository.viewPlant(1)).thenReturn(plant);
        when(plantRepository.updatePlant(any(Plant.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.addOrder(any(Order.class))).thenAnswer(inv -> {
            Order order = inv.getArgument(0);
            order.setBookingOrderId(1);
            return order;
        });

        Order result = orderService.orderPlant(1, 1, 2, "UPI");

        assertNotNull(result);
        assertEquals(200.0, result.getTotalCost());
        assertEquals(2, result.getQuantity());
        assertEquals(48, plant.getPlantsStock());
        assertEquals(1, result.getCustomer().getCustomerId());
        verify(plantRepository).updatePlant(plant);
        verify(orderRepository).addOrder(any(Order.class));
    }

    @Test
    @DisplayName("orderPlant throws PlantNotFoundException when plant does not exist")
    void orderPlant_nonExistentPlant_throwsException() {
        when(customerRepository.viewCustomer(1)).thenReturn(validCustomer());
        when(plantRepository.viewPlant(999)).thenReturn(null);

        assertThrows(PlantNotFoundException.class,
                () -> orderService.orderPlant(1, 999, 1, "UPI"));
        verify(orderRepository, never()).addOrder(any());
    }

    @Test
    @DisplayName("orderPlant throws InvalidOrderDataException when stock is insufficient")
    void orderPlant_insufficientStock_throwsException() {
        Plant plant = validPlant();
        plant.setPlantsStock(1);
        when(customerRepository.viewCustomer(1)).thenReturn(validCustomer());
        when(plantRepository.viewPlant(1)).thenReturn(plant);

        assertThrows(InvalidOrderDataException.class,
                () -> orderService.orderPlant(1, 1, 5, "UPI"));
        verify(orderRepository, never()).addOrder(any());
    }

    @Test
    @DisplayName("orderPlant throws InvalidOrderDataException when quantity is invalid")
    void orderPlant_invalidQuantity_throwsException() {
        assertThrows(InvalidOrderDataException.class,
                () -> orderService.orderPlant(1, 1, 0, "UPI"));
        verify(plantRepository, never()).viewPlant(anyInt());
    }

    @Test
    @DisplayName("orderPlant throws InvalidOrderDataException when customer does not exist")
    void orderPlant_missingCustomer_throwsException() {
        when(customerRepository.viewCustomer(999)).thenReturn(null);

        assertThrows(InvalidOrderDataException.class,
                () -> orderService.orderPlant(999, 1, 1, "UPI"));
        verify(plantRepository, never()).viewPlant(anyInt());
        verify(orderRepository, never()).addOrder(any());
    }

    @Test
    @DisplayName("orderSeed creates order and decrements seed stock when valid")
    void orderSeed_withValidData_createsOrderAndDecrementsStock() {
        Seed seed = validSeed();
        Customer customer = validCustomer();
        when(customerRepository.viewCustomer(1)).thenReturn(customer);
        when(seedRepository.viewSeed(1)).thenReturn(seed);
        when(seedRepository.updateSeed(any(Seed.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.addOrder(any(Order.class))).thenAnswer(inv -> {
            Order order = inv.getArgument(0);
            order.setBookingOrderId(2);
            return order;
        });

        Order result = orderService.orderSeed(1, 1, 3, "Card");

        assertNotNull(result);
        assertEquals(75.0, result.getTotalCost());
        assertEquals(47, seed.getSeedsStock());
        assertEquals(1, result.getCustomer().getCustomerId());
        verify(seedRepository).updateSeed(seed);
    }

    @Test
    @DisplayName("orderSeed throws SeedNotFoundException when seed does not exist")
    void orderSeed_nonExistentSeed_throwsException() {
        when(customerRepository.viewCustomer(1)).thenReturn(validCustomer());
        when(seedRepository.viewSeed(999)).thenReturn(null);

        assertThrows(SeedNotFoundException.class,
                () -> orderService.orderSeed(1, 999, 1, "Card"));
        verify(orderRepository, never()).addOrder(any());
    }

    @Test
    @DisplayName("orderSeed throws InvalidOrderDataException when customer does not exist")
    void orderSeed_missingCustomer_throwsException() {
        when(customerRepository.viewCustomer(999)).thenReturn(null);

        assertThrows(InvalidOrderDataException.class,
                () -> orderService.orderSeed(999, 1, 1, "Card"));
        verify(seedRepository, never()).viewSeed(anyInt());
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

    @Test
    @DisplayName("cancelOrder marks the order as cancelled when customer owns it")
    void cancelOrder_validCustomerAndOrder_marksCancelled() {
        Order order = activeOrderForCustomer(10);
        when(customerRepository.viewCustomer(1)).thenReturn(validCustomer());
        when(orderRepository.viewOrder(10)).thenReturn(order);
        when(orderRepository.updateOrder(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.cancelOrder(1, 10);

        assertEquals(OrderServiceImpl.ORDER_STATUS_CANCELLED, result.getOrderStatus());
        verify(orderRepository).updateOrder(order);
    }

    @Test
    @DisplayName("cancelOrder throws OrderNotFoundException when order does not exist")
    void cancelOrder_missingOrder_throwsException() {
        when(customerRepository.viewCustomer(1)).thenReturn(validCustomer());
        when(orderRepository.viewOrder(999)).thenReturn(null);

        assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(1, 999));
        verify(orderRepository, never()).updateOrder(any());
    }

    @Test
    @DisplayName("cancelOrder throws InvalidOrderDataException when order belongs to another customer")
    void cancelOrder_wrongCustomer_throwsException() {
        Order order = activeOrderForCustomer(10);
        Customer otherCustomer = new Customer("Alex Roy", "alex@example.com", "alexroy", "pass456", null);
        otherCustomer.setCustomerId(2);
        order.setCustomer(otherCustomer);
        when(customerRepository.viewCustomer(1)).thenReturn(validCustomer());
        when(orderRepository.viewOrder(10)).thenReturn(order);

        assertThrows(InvalidOrderDataException.class, () -> orderService.cancelOrder(1, 10));
        verify(orderRepository, never()).updateOrder(any());
    }

    @Test
    @DisplayName("cancelOrder throws InvalidOrderDataException when order is already cancelled")
    void cancelOrder_duplicateCancellation_throwsException() {
        Order order = activeOrderForCustomer(10);
        order.setOrderStatus(OrderServiceImpl.ORDER_STATUS_CANCELLED);
        when(customerRepository.viewCustomer(1)).thenReturn(validCustomer());
        when(orderRepository.viewOrder(10)).thenReturn(order);

        assertThrows(InvalidOrderDataException.class, () -> orderService.cancelOrder(1, 10));
        verify(orderRepository, never()).updateOrder(any());
    }

    @Test
    @DisplayName("viewOrdersByCustomer returns only the customer's orders when customer exists")
    void viewOrdersByCustomer_existingCustomer_returnsOrders() {
        Order order = new Order(LocalDate.now(), "UPI", 2, 200.0, null);
        order.setCustomer(validCustomer());
        when(customerRepository.viewCustomer(1)).thenReturn(validCustomer());
        when(orderRepository.viewOrdersByCustomer(1)).thenReturn(Collections.singletonList(order));

        List<Order> result = orderService.viewOrdersByCustomer(1);

        assertEquals(1, result.size());
        verify(orderRepository).viewOrdersByCustomer(1);
    }

    @Test
    @DisplayName("viewOrdersByCustomer returns empty list when customer exists but has no orders")
    void viewOrdersByCustomer_existingCustomerWithNoOrders_returnsEmptyList() {
        when(customerRepository.viewCustomer(1)).thenReturn(validCustomer());
        when(orderRepository.viewOrdersByCustomer(1)).thenReturn(Collections.emptyList());

        List<Order> result = orderService.viewOrdersByCustomer(1);

        assertTrue(result.isEmpty());
        verify(orderRepository).viewOrdersByCustomer(1);
    }

    @Test
    @DisplayName("viewOrdersByCustomer throws InvalidOrderDataException when customer does not exist")
    void viewOrdersByCustomer_missingCustomer_throwsException() {
        when(customerRepository.viewCustomer(999)).thenReturn(null);

        assertThrows(InvalidOrderDataException.class, () -> orderService.viewOrdersByCustomer(999));
        verify(orderRepository, never()).viewOrdersByCustomer(anyInt());
    }

    @Test
    @DisplayName("orderPlanter creates order and decrements planter stock when valid")
    void orderPlanter_withValidData_createsOrderAndDecrementsStock() {
        Planter planter = validPlanter();
        Customer customer = validCustomer();
        when(customerRepository.viewCustomer(1)).thenReturn(customer);
        when(planterRepository.viewPlanter(1)).thenReturn(planter);
        when(planterRepository.updatePlanter(any(Planter.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.addOrder(any(Order.class))).thenAnswer(inv -> {
            Order order = inv.getArgument(0);
            order.setBookingOrderId(3);
            return order;
        });

        Order result = orderService.orderPlanter(1, 1, 4, "UPI");

        assertNotNull(result);
        assertEquals(600.0, result.getTotalCost());
        assertEquals(4, result.getQuantity());
        assertEquals(16, planter.getPlanterStock());
        assertEquals(1, result.getCustomer().getCustomerId());
        assertEquals(1, result.getPlanters().getPlanterId());
        verify(planterRepository).updatePlanter(planter);
        verify(orderRepository).addOrder(any(Order.class));
    }

    @Test
    @DisplayName("orderPlanter throws PlanterNotFoundException when planter does not exist")
    void orderPlanter_nonExistentPlanter_throwsException() {
        when(customerRepository.viewCustomer(1)).thenReturn(validCustomer());
        when(planterRepository.viewPlanter(999)).thenReturn(null);

        assertThrows(PlanterNotFoundException.class,
                () -> orderService.orderPlanter(1, 999, 1, "UPI"));
        verify(orderRepository, never()).addOrder(any());
    }

    @Test
    @DisplayName("orderPlanter throws InvalidOrderDataException when stock is insufficient")
    void orderPlanter_insufficientStock_throwsException() {
        Planter planter = validPlanter();
        planter.setPlanterStock(2);
        when(customerRepository.viewCustomer(1)).thenReturn(validCustomer());
        when(planterRepository.viewPlanter(1)).thenReturn(planter);

        assertThrows(InvalidOrderDataException.class,
                () -> orderService.orderPlanter(1, 1, 5, "UPI"));
        verify(orderRepository, never()).addOrder(any());
    }

    @Test
    @DisplayName("orderPlanter throws InvalidOrderDataException when quantity is invalid")
    void orderPlanter_invalidQuantity_throwsException() {
        assertThrows(InvalidOrderDataException.class,
                () -> orderService.orderPlanter(1, 1, 0, "UPI"));
        verify(planterRepository, never()).viewPlanter(anyInt());
        verify(orderRepository, never()).addOrder(any());
    }
}
