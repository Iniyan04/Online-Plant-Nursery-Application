package com.nursery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nursery.entity.Planter;
import com.nursery.entity.Plant;
import com.nursery.entity.Seed;
import com.nursery.service.IAdminService;
import com.nursery.util.JPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Application flow integration")
class ApplicationFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IAdminService adminService;

    private EntityManager em;

    private int planterId;

    @BeforeEach
    void setUp() {
        em = JPAUtil.getEntityManagerFactory().createEntityManager();
        cleanDatabase();
        adminService.ensureDefaultAdmin();
        seedCatalog();
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
        em.close();
    }

    @Test
    @DisplayName("Customer can register, login, browse, order planter, view orders, cancel order, and admin can view dashboard")
    void fullApplicationFlow_succeeds() throws Exception {
        String registerRequest = """
                {
                  "customerName": "Flow User",
                  "customerEmail": "flow@example.com",
                  "username": "flowuser",
                  "password": "secret123",
                  "houseNo": "12",
                  "colony": "Green Park",
                  "city": "Bengaluru",
                  "state": "Karnataka",
                  "pincode": 560001
                }
                """;

        MvcResult registerResult = mockMvc.perform(post("/api/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("flowuser"))
                .andReturn();

        int customerId = readJson(registerResult).get("customerId").asInt();

        mockMvc.perform(post("/api/customers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "flowuser",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.username").value("flowuser"));

        mockMvc.perform(post("/api/admins/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "admin123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adminUsername").value("admin"));

        mockMvc.perform(get("/api/plants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commonName").value("Rose"));

        mockMvc.perform(get("/api/seeds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commonName").value("Basil"));

        mockMvc.perform(get("/api/planters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].planterShape").value("Round"));

        MvcResult orderResult = mockMvc.perform(post("/api/orders/planters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": %d,
                                  "planterId": %d,
                                  "quantity": 2,
                                  "transactionMode": "UPI"
                                }
                                """.formatted(customerId, planterId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalCost").value(300.0))
                .andExpect(jsonPath("$.orderStatus").value("ACTIVE"))
                .andReturn();

        int orderId = readJson(orderResult).get("bookingOrderId").asInt();

        mockMvc.perform(get("/api/orders/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingOrderId").value(orderId))
                .andExpect(jsonPath("$[0].itemType").value("Planter"))
                .andExpect(jsonPath("$[0].itemName").value("Round"))
                .andExpect(jsonPath("$[0].orderStatus").value("ACTIVE"));

        mockMvc.perform(put("/api/orders/{orderId}/cancel", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": %d
                                }
                                """.formatted(customerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingOrderId").value(orderId))
                .andExpect(jsonPath("$.orderStatus").value("CANCELLED"));

        mockMvc.perform(get("/api/orders/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderStatus").value("CANCELLED"));

        mockMvc.perform(get("/api/admins/dashboard")
                        .header("adminUsername", "admin")
                        .header("adminPassword", "admin123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCustomers").value(1))
                .andExpect(jsonPath("$.totalOrders").value(1))
                .andExpect(jsonPath("$.totalPlants").value(1))
                .andExpect(jsonPath("$.totalSeeds").value(1))
                .andExpect(jsonPath("$.totalPlanters").value(1))
                .andExpect(jsonPath("$.activeOrders").value(0))
                .andExpect(jsonPath("$.cancelledOrders").value(1));

        verifyPlanterStockReducedTo(18);
    }

    private JsonNode readJson(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private void seedCatalog() {
        Plant plant = new Plant(30, "Medium", "Rose", "Summer", "Ornamental",
                "Easy", "Warm", "Flower", "Integration test plant", 50, 100.0);
        Seed seed = new Seed("Basil", "Summer", "Daily", "Easy", "Warm",
                "Herb", "Integration test seed", 100, 25.0, 20);
        Planter planter = new Planter(12.5f, 5, 3, 1, "Round", 20, 150, null, null);

        em.getTransaction().begin();
        em.persist(plant);
        em.persist(seed);
        em.persist(planter);
        em.getTransaction().commit();

        planterId = planter.getPlanterId();
        assertTrue(planterId > 0);
    }

    private void verifyPlanterStockReducedTo(int expectedStock) {
        em.clear();
        Planter persistedPlanter = em.find(Planter.class, planterId);
        assertEquals(expectedStock, persistedPlanter.getPlanterStock());
    }

    private void cleanDatabase() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }

        em.getTransaction().begin();
        em.createQuery("DELETE FROM Order").executeUpdate();
        em.createQuery("DELETE FROM Planter").executeUpdate();
        em.createQuery("DELETE FROM Plant").executeUpdate();
        em.createQuery("DELETE FROM Seed").executeUpdate();
        em.createQuery("DELETE FROM Customer").executeUpdate();
        em.createQuery("DELETE FROM Address").executeUpdate();
        em.createQuery("DELETE FROM Admin").executeUpdate();
        em.getTransaction().commit();
        em.clear();
    }
}
