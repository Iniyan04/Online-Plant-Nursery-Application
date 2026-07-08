package com.nursery.repository;

import com.nursery.entity.Address;
import com.nursery.entity.Customer;
import com.nursery.entity.Order;
import com.nursery.entity.Plant;
import com.nursery.entity.Planter;
import com.nursery.entity.Seed;
import com.nursery.service.OrderServiceImpl;
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
        em.createQuery("DELETE FROM Customer").executeUpdate();
        em.createQuery("DELETE FROM Address").executeUpdate();
        em.createQuery("DELETE FROM Planter").executeUpdate();
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

    private Customer persistCustomer() {
        Address address = new Address("12", "Green Park", "Bengaluru", "Karnataka", 560001);
        Customer customer = new Customer("Jane Doe", "jane@example.com", "janedoe", "secret123", address);
        em.getTransaction().begin();
        em.persist(customer);
        em.getTransaction().commit();
        return customer;
    }

    private Planter persistPlanter() {
        Planter planter = new Planter(12.5f, 5, 3, 1, "Round", 20, 150, null, null);
        em.getTransaction().begin();
        em.persist(planter);
        em.getTransaction().commit();
        return planter;
    }

    @Test
    @DisplayName("addOrder persists a plant order and assigns a generated ID")
    void addOrder_plantOrder_isPersistedWithGeneratedId() {
        Plant plant = persistPlant();
        Customer customer = persistCustomer();
        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setTransactionMode("UPI");
        order.setQuantity(2);
        order.setTotalCost(200.0);
        order.setOrderStatus(OrderServiceImpl.ORDER_STATUS_ACTIVE);
        order.setPlant(plant);
        order.setCustomer(customer);

        Order saved = orderRepository.addOrder(order);

        assertNotNull(saved);
        assertTrue(saved.getBookingOrderId() > 0);
        assertNotNull(saved.getPlant());
        assertEquals("Rose", saved.getPlant().getCommonName());
        assertNotNull(saved.getCustomer());
        assertEquals("janedoe", saved.getCustomer().getUsername());
    }

    @Test
    @DisplayName("addOrder persists a seed order linked to existing seed")
    void addOrder_seedOrder_linksExistingSeed() {
        Seed seed = new Seed("Basil", "Summer", "Daily", "Easy", "Warm",
                "Herb", "Test seed", 50, 25.0, 20);
        Customer customer = persistCustomer();
        em.getTransaction().begin();
        em.persist(seed);
        em.getTransaction().commit();

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setTransactionMode("Card");
        order.setQuantity(3);
        order.setTotalCost(75.0);
        order.setOrderStatus(OrderServiceImpl.ORDER_STATUS_ACTIVE);
        order.setSeed(seed);
        order.setCustomer(customer);

        Order saved = orderRepository.addOrder(order);

        assertNotNull(saved.getSeed());
        assertEquals("Basil", saved.getSeed().getCommonName());
    }

    @Test
    @DisplayName("viewAllOrders returns every order in the database")
    void viewAllOrders_returnsAllOrders() {
        Plant plant = persistPlant();
        Customer customer = persistCustomer();
        Order order1 = new Order(LocalDate.now(), "UPI", 1, 100.0, null);
        order1.setOrderStatus(OrderServiceImpl.ORDER_STATUS_ACTIVE);
        order1.setPlant(plant);
        order1.setCustomer(customer);
        orderRepository.addOrder(order1);

        Order order2 = new Order(LocalDate.now(), "Card", 2, 200.0, null);
        order2.setOrderStatus(OrderServiceImpl.ORDER_STATUS_ACTIVE);
        order2.setPlant(plant);
        order2.setCustomer(customer);
        orderRepository.addOrder(order2);

        List<Order> result = orderRepository.viewAllOrders();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("deleteOrder removes the order from the database")
    void deleteOrder_existingOrder_removesFromDatabase() {
        Plant plant = persistPlant();
        Customer customer = persistCustomer();
        Order order = new Order(LocalDate.now(), "UPI", 1, 100.0, null);
        order.setOrderStatus(OrderServiceImpl.ORDER_STATUS_ACTIVE);
        order.setPlant(plant);
        order.setCustomer(customer);
        Order saved = orderRepository.addOrder(order);
        int idToDelete = saved.getBookingOrderId();

        orderRepository.deleteOrder(idToDelete);

        Order result = orderRepository.viewOrder(idToDelete);
        assertNull(result);
    }

    @Test
    @DisplayName("addOrder persists a planter order linked to existing planter")
    void addOrder_planterOrder_linksExistingPlanter() {
        Planter planter = persistPlanter();
        Customer customer = persistCustomer();

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setTransactionMode("Card");
        order.setQuantity(2);
        order.setTotalCost(300.0);
        order.setOrderStatus(OrderServiceImpl.ORDER_STATUS_ACTIVE);
        order.setPlanters(planter);
        order.setCustomer(customer);

        Order saved = orderRepository.addOrder(order);

        assertNotNull(saved);
        assertNotNull(saved.getPlanters());
        assertEquals("Round", saved.getPlanters().getPlanterShape());
        assertEquals("janedoe", saved.getCustomer().getUsername());
    }

    @Test
    @DisplayName("viewOrdersByCustomer returns only orders belonging to that customer")
    void viewOrdersByCustomer_returnsOnlyCustomerOrders() {
        Plant plant = persistPlant();
        Customer customer1 = persistCustomer();
        Customer customer2 = new Customer("Alex Roy", "alex@example.com", "alexroy", "pass456",
                new Address("45", "Lake View", "Chennai", "Tamil Nadu", 600001));
        em.getTransaction().begin();
        em.persist(customer2);
        em.getTransaction().commit();

        Order order1 = new Order(LocalDate.now(), "UPI", 1, 100.0, null);
        order1.setOrderStatus(OrderServiceImpl.ORDER_STATUS_ACTIVE);
        order1.setPlant(plant);
        order1.setCustomer(customer1);
        orderRepository.addOrder(order1);

        Order order2 = new Order(LocalDate.now(), "Card", 2, 200.0, null);
        order2.setOrderStatus(OrderServiceImpl.ORDER_STATUS_ACTIVE);
        order2.setPlant(plant);
        order2.setCustomer(customer2);
        orderRepository.addOrder(order2);

        List<Order> result = orderRepository.viewOrdersByCustomer(customer1.getCustomerId());

        assertEquals(1, result.size());
        assertEquals("janedoe", result.get(0).getCustomer().getUsername());
    }

    @Test
    @DisplayName("updateOrder persists cancelled status to the database")
    void updateOrder_cancelledStatus_isPersisted() {
        Plant plant = persistPlant();
        Customer customer = persistCustomer();

        Order order = new Order(LocalDate.now(), "UPI", 1, 100.0, null);
        order.setOrderStatus(OrderServiceImpl.ORDER_STATUS_ACTIVE);
        order.setPlant(plant);
        order.setCustomer(customer);
        Order saved = orderRepository.addOrder(order);

        saved.setOrderStatus(OrderServiceImpl.ORDER_STATUS_CANCELLED);
        orderRepository.updateOrder(saved);

        Order refetched = orderRepository.viewOrder(saved.getBookingOrderId());
        assertEquals(OrderServiceImpl.ORDER_STATUS_CANCELLED, refetched.getOrderStatus());
    }

    @Test
    @DisplayName("countOrders and countOrdersByStatus return dashboard-ready metrics")
    void countOrdersAndCountByStatus_returnExpectedTotals() {
        Plant plant = persistPlant();
        Customer customer = persistCustomer();

        Order activeOrder = new Order(LocalDate.now(), "UPI", 1, 100.0, null);
        activeOrder.setOrderStatus(OrderServiceImpl.ORDER_STATUS_ACTIVE);
        activeOrder.setPlant(plant);
        activeOrder.setCustomer(customer);
        orderRepository.addOrder(activeOrder);

        Order cancelledOrder = new Order(LocalDate.now(), "Card", 2, 200.0, null);
        cancelledOrder.setOrderStatus(OrderServiceImpl.ORDER_STATUS_CANCELLED);
        cancelledOrder.setPlant(plant);
        cancelledOrder.setCustomer(customer);
        orderRepository.addOrder(cancelledOrder);

        assertEquals(2, orderRepository.countOrders());
        assertEquals(1, orderRepository.countOrdersByStatus(OrderServiceImpl.ORDER_STATUS_ACTIVE));
        assertEquals(1, orderRepository.countOrdersByStatus(OrderServiceImpl.ORDER_STATUS_CANCELLED));
    }
}
