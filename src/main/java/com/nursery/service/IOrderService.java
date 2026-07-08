package com.nursery.service;

import com.nursery.entity.Order;
import java.util.List;

public interface IOrderService {

    Order addOrder(Order order);

    Order updateOrder(Order order);

    Order deleteOrder(int orderId);

    Order cancelOrder(int customerId, int orderId);

    Order viewOrder(int orderId);

    List<Order> viewAllOrders();

    List<Order> viewOrdersByCustomer(int customerId);

    Order orderPlant(int customerId, int plantId, int quantity, String transactionMode);

    Order orderSeed(int customerId, int seedId, int quantity, String transactionMode);

    Order orderPlanter(int customerId, int planterId, int quantity, String transactionMode);
}
