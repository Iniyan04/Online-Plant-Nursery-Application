package com.nursery.repository;

import com.nursery.entity.Customer;
import com.nursery.entity.Order;
import com.nursery.entity.Plant;
import com.nursery.entity.Planter;
import com.nursery.entity.Seed;
import com.nursery.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class OrderRepositoryImpl implements IOrderRepository {

    @Override
    public Order addOrder(Order order) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            linkAssociations(em, order);
            em.persist(order);
            em.getTransaction().commit();
            return order;
        } finally {
            em.close();
        }
    }

    @Override
    public Order updateOrder(Order order) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            linkAssociations(em, order);
            Order merged = em.merge(order);
            em.getTransaction().commit();
            return merged;
        } finally {
            em.close();
        }
    }

    @Override
    public Order deleteOrder(int orderId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Order managed = em.find(Order.class, orderId);
            if (managed != null) {
                em.remove(managed);
            }
            em.getTransaction().commit();
            return managed;
        } finally {
            em.close();
        }
    }

    @Override
    public Order viewOrder(int orderId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Order.class, orderId);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> viewAllOrders() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o", Order.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> viewOrdersByCustomer(int customerId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery(
                    "SELECT o FROM Order o WHERE o.customer.customerId = :customerId",
                    Order.class);
            query.setParameter("customerId", customerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public long countOrders() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(o) FROM Order o", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public long countOrdersByStatus(String orderStatus) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :orderStatus",
                    Long.class);
            query.setParameter("orderStatus", orderStatus);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    private void linkAssociations(EntityManager em, Order order) {
        if (order.getPlant() != null && order.getPlant().getPlantId() > 0) {
            Plant plant = em.find(Plant.class, order.getPlant().getPlantId());
            order.setPlant(plant);
        }
        if (order.getSeed() != null && order.getSeed().getSeedId() > 0) {
            Seed seed = em.find(Seed.class, order.getSeed().getSeedId());
            order.setSeed(seed);
        }
        if (order.getPlanters() != null && order.getPlanters().getPlanterId() > 0) {
            Planter planter = em.find(Planter.class, order.getPlanters().getPlanterId());
            order.setPlanters(planter);
        }
        if (order.getCustomer() != null && order.getCustomer().getCustomerId() > 0) {
            Customer customer = em.find(Customer.class, order.getCustomer().getCustomerId());
            order.setCustomer(customer);
        }
    }
}
