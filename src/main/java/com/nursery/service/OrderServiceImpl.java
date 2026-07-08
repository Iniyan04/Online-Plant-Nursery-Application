package com.nursery.service;

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

import java.time.LocalDate;
import java.util.List;

public class OrderServiceImpl implements IOrderService {

    public static final String ORDER_STATUS_ACTIVE = "ACTIVE";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";

    private final IOrderRepository orderRepository;
    private final IPlantRepository plantRepository;
    private final ISeedRepository seedRepository;
    private final IPlanterRepository planterRepository;
    private final ICustomerRepository customerRepository;

    public OrderServiceImpl(IOrderRepository orderRepository,
                            IPlantRepository plantRepository,
                            ISeedRepository seedRepository,
                            IPlanterRepository planterRepository,
                            ICustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.plantRepository = plantRepository;
        this.seedRepository = seedRepository;
        this.planterRepository = planterRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Order addOrder(Order order) {
        validateOrderData(order);
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }
        if (isBlank(order.getOrderStatus())) {
            order.setOrderStatus(ORDER_STATUS_ACTIVE);
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
    public Order cancelOrder(int customerId, int orderId) {
        if (customerId <= 0) {
            throw new InvalidOrderDataException("Customer ID must be greater than zero");
        }

        Customer customer = customerRepository.viewCustomer(customerId);
        if (customer == null) {
            throw new InvalidOrderDataException("No customer found with ID " + customerId);
        }

        Order order = orderRepository.viewOrder(orderId);
        if (order == null) {
            throw new OrderNotFoundException("No order found with ID " + orderId);
        }
        if (order.getCustomer() == null || order.getCustomer().getCustomerId() != customerId) {
            throw new InvalidOrderDataException("Order does not belong to customer ID " + customerId);
        }
        if (ORDER_STATUS_CANCELLED.equalsIgnoreCase(order.getOrderStatus())) {
            throw new InvalidOrderDataException("Order with ID " + orderId + " is already cancelled");
        }

        order.setOrderStatus(ORDER_STATUS_CANCELLED);
        return orderRepository.updateOrder(order);
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
    public List<Order> viewOrdersByCustomer(int customerId) {
        if (customerId <= 0) {
            throw new InvalidOrderDataException("Customer ID must be greater than zero");
        }
        Customer customer = customerRepository.viewCustomer(customerId);
        if (customer == null) {
            throw new InvalidOrderDataException("No customer found with ID " + customerId);
        }
        return orderRepository.viewOrdersByCustomer(customerId);
    }

    @Override
    public Order orderPlant(int customerId, int plantId, int quantity, String transactionMode) {
        Customer customer = validateOrderRequest(customerId, quantity, transactionMode);

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
        order.setOrderStatus(ORDER_STATUS_ACTIVE);
        order.setPlant(plant);
        order.setCustomer(customer);

        return orderRepository.addOrder(order);
    }

    @Override
    public Order orderSeed(int customerId, int seedId, int quantity, String transactionMode) {
        Customer customer = validateOrderRequest(customerId, quantity, transactionMode);

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
        order.setOrderStatus(ORDER_STATUS_ACTIVE);
        order.setSeed(seed);
        order.setCustomer(customer);

        return orderRepository.addOrder(order);
    }

    @Override
    public Order orderPlanter(int customerId, int planterId, int quantity, String transactionMode) {
        Customer customer = validateOrderRequest(customerId, quantity, transactionMode);

        Planter planter = planterRepository.viewPlanter(planterId);
        if (planter == null) {
            throw new PlanterNotFoundException("No planter found with ID " + planterId);
        }
        if (planter.getPlanterStock() < quantity) {
            throw new InvalidOrderDataException(
                    "Insufficient planter stock. Available: " + planter.getPlanterStock());
        }

        double totalCost = planter.getPlanterCost() * quantity;
        planter.setPlanterStock(planter.getPlanterStock() - quantity);
        planterRepository.updatePlanter(planter);

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setTransactionMode(transactionMode);
        order.setQuantity(quantity);
        order.setTotalCost(totalCost);
        order.setOrderStatus(ORDER_STATUS_ACTIVE);
        order.setPlanters(planter);
        order.setCustomer(customer);

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

    private Customer validateOrderRequest(int customerId, int quantity, String transactionMode) {
        if (customerId <= 0) {
            throw new InvalidOrderDataException("Customer ID must be greater than zero");
        }
        if (isBlank(transactionMode)) {
            throw new InvalidOrderDataException("Transaction mode must not be empty");
        }
        if (quantity <= 0) {
            throw new InvalidOrderDataException("Order quantity must be greater than zero");
        }

        Customer customer = customerRepository.viewCustomer(customerId);
        if (customer == null) {
            throw new InvalidOrderDataException("No customer found with ID " + customerId);
        }
        return customer;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
