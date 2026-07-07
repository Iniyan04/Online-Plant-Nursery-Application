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

import java.time.LocalDate;
import java.util.List;

public class OrderServiceImpl implements IOrderService {

    private final IOrderRepository orderRepository;
    private final IPlantRepository plantRepository;
    private final ISeedRepository seedRepository;

    public OrderServiceImpl(IOrderRepository orderRepository,
                            IPlantRepository plantRepository,
                            ISeedRepository seedRepository) {
        this.orderRepository = orderRepository;
        this.plantRepository = plantRepository;
        this.seedRepository = seedRepository;
    }

    @Override
    public Order addOrder(Order order) {
        validateOrderData(order);
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }
        return orderRepository.addOrder(order);
    }

    @Override
    public Order updateOrder(Order order) {
        validateOrderData(order);

        Order existing = orderRepository.viewOrder(order.getBookingOrderId());
        if (existing == null) {
            throw new OrderNotFoundException(
                    "No order found with ID " + order.getBookingOrderId() + " to update");
        }

        return orderRepository.updateOrder(order);
    }

    @Override
    public Order deleteOrder(int orderId) {
        Order existing = orderRepository.viewOrder(orderId);
        if (existing == null) {
            throw new OrderNotFoundException("No order found with ID " + orderId + " to delete");
        }
        return orderRepository.deleteOrder(orderId);
    }

    @Override
    public Order viewOrder(int orderId) {
        Order order = orderRepository.viewOrder(orderId);
        if (order == null) {
            throw new OrderNotFoundException("No order found with ID " + orderId);
        }
        return order;
    }

    @Override
    public List<Order> viewAllOrders() {
        return orderRepository.viewAllOrders();
    }

    @Override
    public Order orderPlant(int plantId, int quantity, String transactionMode) {
        validateOrderRequest(quantity, transactionMode);

        Plant plant = plantRepository.viewPlant(plantId);
        if (plant == null) {
            throw new PlantNotFoundException("No plant found with ID " + plantId);
        }
        if (plant.getPlantsStock() < quantity) {
            throw new InvalidOrderDataException(
                    "Insufficient plant stock. Available: " + plant.getPlantsStock());
        }

        double totalCost = plant.getPlantCost() * quantity;
        plant.setPlantsStock(plant.getPlantsStock() - quantity);
        plantRepository.updatePlant(plant);

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setTransactionMode(transactionMode);
        order.setQuantity(quantity);
        order.setTotalCost(totalCost);
        order.setPlant(plant);

        return orderRepository.addOrder(order);
    }

    @Override
    public Order orderSeed(int seedId, int quantity, String transactionMode) {
        validateOrderRequest(quantity, transactionMode);

        Seed seed = seedRepository.viewSeed(seedId);
        if (seed == null) {
            throw new SeedNotFoundException("No seed found with ID " + seedId);
        }
        if (seed.getSeedsStock() < quantity) {
            throw new InvalidOrderDataException(
                    "Insufficient seed stock. Available: " + seed.getSeedsStock());
        }

        double totalCost = seed.getSeedsCost() * quantity;
        seed.setSeedsStock(seed.getSeedsStock() - quantity);
        seedRepository.updateSeed(seed);

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setTransactionMode(transactionMode);
        order.setQuantity(quantity);
        order.setTotalCost(totalCost);
        order.setSeed(seed);

        return orderRepository.addOrder(order);
    }

    private void validateOrderData(Order order) {
        if (order == null) {
            throw new InvalidOrderDataException("Order must not be null");
        }
        if (isBlank(order.getTransactionMode())) {
            throw new InvalidOrderDataException("Transaction mode must not be empty");
        }
        if (order.getQuantity() <= 0) {
            throw new InvalidOrderDataException("Order quantity must be greater than zero");
        }
        if (order.getTotalCost() < 0) {
            throw new InvalidOrderDataException("Order total cost must not be negative");
        }
    }

    private void validateOrderRequest(int quantity, String transactionMode) {
        if (isBlank(transactionMode)) {
            throw new InvalidOrderDataException("Transaction mode must not be empty");
        }
        if (quantity <= 0) {
            throw new InvalidOrderDataException("Order quantity must be greater than zero");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
