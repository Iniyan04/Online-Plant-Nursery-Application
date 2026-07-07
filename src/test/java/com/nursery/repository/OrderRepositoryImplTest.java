package com.nursery.repository;

import com.nursery.entity.Order;
import com.nursery.entity.Plant;
import com.nursery.entity.Seed;
import com.nursery.util.JPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderRepositoryImpl (integration)")
class OrderRepositoryImplTest {

    private IOrderRepository orderRepository;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        orderRepository = new OrderRepositoryImpl();
        em = JPAUtil.getEntityManagerFactory().createEntityManager();
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Order").executeUpdate();
        em.createQuery("DELETE FROM Plant").executeUpdate();
        em.createQuery("DELETE FROM Seed").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    private Plant persistPlant() {
        Plant plant = new Plant(30, "Medium", "Rose", "Summer", "Ornamental",
                "Easy", "Warm", "Flower", "Test plant", 50, 100.0);
        em.getTransaction().begin();
        em.persist(plant);
        em.getTransaction().commit();
        return plant;
    }

    @Test
    @DisplayName("addOrder persists a plant order and assigns a generated ID")
    void addOrder_plantOrder_isPersistedWithGeneratedId() {
        Plant plant = persistPlant();
        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setTransactionMode("UPI");
        order.setQuantity(2);
        order.setTotalCost(200.0);
        order.setPlant(plant);

        Order saved = orderRepository.addOrder(order);

        assertNotNull(saved);
        assertTrue(saved.getBookingOrderId() > 0);
        assertNotNull(saved.getPlant());
        assertEquals("Rose", saved.getPlant().getCommonName());
    }

    @Test
    @DisplayName("addOrder persists a seed order linked to existing seed")
    void addOrder_seedOrder_linksExistingSeed() {
        Seed seed = new Seed("Basil", "Summer", "Daily", "Easy", "Warm",
                "Herb", "Test seed", 50, 25.0, 20);
        em.getTransaction().begin();
        em.persist(seed);
        em.getTransaction().commit();

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setTransactionMode("Card");
        order.setQuantity(3);
        order.setTotalCost(75.0);
        order.setSeed(seed);

        Order saved = orderRepository.addOrder(order);

        assertNotNull(saved.getSeed());
        assertEquals("Basil", saved.getSeed().getCommonName());
    }

    @Test
    @DisplayName("viewAllOrders returns every order in the database")
    void viewAllOrders_returnsAllOrders() {
        Plant plant = persistPlant();
        Order order1 = new Order(LocalDate.now(), "UPI", 1, 100.0, null);
        order1.setPlant(plant);
        orderRepository.addOrder(order1);

        Order order2 = new Order(LocalDate.now(), "Card", 2, 200.0, null);
        order2.setPlant(plant);
        orderRepository.addOrder(order2);

        List<Order> result = orderRepository.viewAllOrders();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("deleteOrder removes the order from the database")
    void deleteOrder_existingOrder_removesFromDatabase() {
        Plant plant = persistPlant();
        Order order = new Order(LocalDate.now(), "UPI", 1, 100.0, null);
        order.setPlant(plant);
        Order saved = orderRepository.addOrder(order);
        int idToDelete = saved.getBookingOrderId();

        orderRepository.deleteOrder(idToDelete);

        Order result = orderRepository.viewOrder(idToDelete);
        assertNull(result);
    }
}
