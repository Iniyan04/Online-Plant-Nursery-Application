package com.nursery.repository;

import com.nursery.entity.Address;
import com.nursery.entity.Customer;
import com.nursery.util.JPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CustomerRepositoryImpl (US-001 Login, US-002 Registration).
 * These tests run against a REAL H2 in-memory database via Hibernate -
 * no mocks - to verify the actual JPQL query and persistence behavior.
 */
@DisplayName("CustomerRepositoryImpl (integration)")
class CustomerRepositoryImplTest {

    private ICustomerRepository customerRepository;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        customerRepository = new CustomerRepositoryImpl();
        em = JPAUtil.getEntityManagerFactory().createEntityManager();

        // Seed one known customer directly via EntityManager
        Address address = new Address("12", "Green Park", "Bengaluru", "Karnataka", 560001);
        Customer customer = new Customer("Jane Doe", "jane@example.com", "janedoe", "secret123", address);

        em.getTransaction().begin();
        em.persist(customer);
        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        // Clean up so each test starts from a known state.
        // Customer first (it references Address), then any leftover Address rows.
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Customer").executeUpdate();
        em.createQuery("DELETE FROM Address").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @DisplayName("Returns the Customer when username and password match a stored record")
    void validateCustomer_withCorrectCredentials_returnsCustomer() {
        Customer result = customerRepository.validateCustomer("janedoe", "secret123");

        assertNotNull(result);
        assertEquals("janedoe", result.getUsername());
        assertEquals("Jane Doe", result.getCustomerName());
    }

    @Test
    @DisplayName("Returns null when the password does not match")
    void validateCustomer_withWrongPassword_returnsNull() {
        Customer result = customerRepository.validateCustomer("janedoe", "wrongpass");

        assertNull(result);
    }

    @Test
    @DisplayName("Returns null when the username does not exist")
    void validateCustomer_withUnknownUsername_returnsNull() {
        Customer result = customerRepository.validateCustomer("ghost", "secret123");

        assertNull(result);
    }

    // ----------------------------------------------------------------
    // US-002: Customer Registration
    // ----------------------------------------------------------------

    @Test
    @DisplayName("findByUsername returns the Customer when the username exists")
    void findByUsername_existingUsername_returnsCustomer() {
        Customer result = customerRepository.findByUsername("janedoe");

        assertNotNull(result);
        assertEquals("Jane Doe", result.getCustomerName());
    }

    @Test
    @DisplayName("findByUsername returns null when the username does not exist")
    void findByUsername_unknownUsername_returnsNull() {
        Customer result = customerRepository.findByUsername("nosuchuser");

        assertNull(result);
    }

    @Test
    @DisplayName("addCustomer persists a new customer that can then be found by username")
    void addCustomer_newCustomer_isPersistedAndRetrievable() {
        Address newAddress = new Address("45", "Lake View", "Chennai", "Tamil Nadu", 600001);
        Customer newCustomer = new Customer("Alex Roy", "alex@example.com", "alexroy", "pass456", newAddress);

        Customer saved = customerRepository.addCustomer(newCustomer);

        assertNotNull(saved);
        assertTrue(saved.getCustomerId() > 0, "Saved customer should have a generated ID");

        Customer fetched = customerRepository.findByUsername("alexroy");
        assertNotNull(fetched);
        assertEquals("Alex Roy", fetched.getCustomerName());
    }

    @Test
    @DisplayName("updateCustomer persists changed email for an existing customer")
    void updateCustomer_existingCustomer_persistsChanges() {
        Customer existing = customerRepository.findByUsername("janedoe");
        existing.setCustomerEmail("newmail@example.com");

        Customer updated = customerRepository.updateCustomer(existing);

        assertEquals("newmail@example.com", updated.getCustomerEmail());
        Customer refetched = customerRepository.findByUsername("janedoe");
        assertEquals("newmail@example.com", refetched.getCustomerEmail());
    }

    @Test
    @DisplayName("deleteCustomer removes the customer from the database")
    void deleteCustomer_existingCustomer_removesFromDatabase() {
        Customer existing = customerRepository.findByUsername("janedoe");

        customerRepository.deleteCustomer(existing);

        Customer refetched = customerRepository.findByUsername("janedoe");
        assertNull(refetched);
    }

    @Test
    @DisplayName("countCustomers returns the total number of customers in the database")
    void countCustomers_returnsTotalCustomers() {
        Address newAddress = new Address("45", "Lake View", "Chennai", "Tamil Nadu", 600001);
        customerRepository.addCustomer(new Customer("Alex Roy", "alex@example.com", "alexroy", "pass456", newAddress));

        long totalCustomers = customerRepository.countCustomers();

        assertEquals(2, totalCustomers);
    }
}
