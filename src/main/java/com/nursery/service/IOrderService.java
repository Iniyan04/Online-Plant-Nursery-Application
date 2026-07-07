package com.nursery.service;

import com.nursery.entity.Order;
import java.util.List;

public interface IOrderService {

    Order addOrder(Order order);

    Order updateOrder(Order order);

    Order deleteOrder(int orderId);

    Order viewOrder(int orderId);

    List<Order> viewAllOrders();

    Order orderPlant(int plantId, int quantity, String transactionMode);

    Order orderSeed(int seedId, int quantity, String transactionMode);
}
