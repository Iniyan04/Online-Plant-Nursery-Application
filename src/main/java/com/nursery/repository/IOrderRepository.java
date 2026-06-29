package com.nursery.repository;

import com.nursery.entity.Order;
import java.util.List;

public interface IOrderRepository {

    Order addOrder(Order order);

    Order updateOrder(Order order);

    Order deleteOrder(int orderId);

    Order viewOrder(int orderId);

    List<Order> viewAllOrders();
}
