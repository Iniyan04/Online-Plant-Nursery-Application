package com.nursery.repository;

import com.nursery.entity.Admin;
import com.nursery.util.JPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("AdminRepositoryImpl (integration)")
class AdminRepositoryImplTest {

    private IAdminRepository adminRepository;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        adminRepository = new AdminRepositoryImpl();
        em = JPAUtil.getEntityManagerFactory().createEntityManager();
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Admin").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @DisplayName("addAdmin persists a new admin and assigns a generated ID")
    void addAdmin_newAdmin_isPersistedWithGeneratedId() {
        Admin saved = adminRepository.addAdmin(new Admin("admin", "admin123"));

        assertNotNull(saved);
        assertNotNull(saved.getAdminId());
    }

    @Test
    @DisplayName("validateAdmin returns the admin when credentials match")
    void validateAdmin_validCredentials_returnsAdmin() {
        Admin admin = new Admin("admin", "admin123");
        em.getTransaction().begin();
        em.persist(admin);
        em.getTransaction().commit();

        Admin result = adminRepository.validateAdmin("admin", "admin123");

        assertNotNull(result);
        assertEquals("admin", result.getAdminUsername());
    }

    @Test
    @DisplayName("findByUsername returns null when admin does not exist")
    void findByUsername_unknownUsername_returnsNull() {
        Admin result = adminRepository.findByUsername("missing");

        assertNull(result);
    }
}
