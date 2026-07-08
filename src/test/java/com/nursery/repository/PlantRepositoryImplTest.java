package com.nursery.repository;

import com.nursery.entity.Plant;
import com.nursery.util.JPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for PlantRepositoryImpl (US-003 to US-006).
 * These run against a REAL H2 in-memory database via Hibernate.
 */
@DisplayName("PlantRepositoryImpl (integration)")
class PlantRepositoryImplTest {

    private IPlantRepository plantRepository;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        plantRepository = new PlantRepositoryImpl();
        em = JPAUtil.getEntityManagerFactory().createEntityManager();
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Plant").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    private Plant seedPlant(String commonName, String typeOfPlant) {
        Plant plant = new Plant(30, "Medium", commonName, "Summer", "Medicinal",
                "Easy", "Warm", typeOfPlant, "A test plant", 50, 99.99);
        em.getTransaction().begin();
        em.persist(plant);
        em.getTransaction().commit();
        return plant;
    }

    @Test
    @DisplayName("addPlant persists a new plant and assigns a generated ID")
    void addPlant_newPlant_isPersistedWithGeneratedId() {
        Plant plant = new Plant(30, "Medium", "Tulsi", "Summer", "Medicinal",
                "Easy", "Warm", "Herb", "A fragrant medicinal herb", 50, 99.99);

        Plant saved = plantRepository.addPlant(plant);

        assertNotNull(saved);
        assertTrue(saved.getPlantId() > 0, "Saved plant should have a generated ID");
    }

    @Test
    @DisplayName("viewPlant(int) returns the plant when the ID exists")
    void viewPlantById_existingId_returnsPlant() {
        Plant seeded = seedPlant("Rose", "Flower");

        Plant result = plantRepository.viewPlant(seeded.getPlantId());

        assertNotNull(result);
        assertEquals("Rose", result.getCommonName());
    }

    @Test
    @DisplayName("viewPlant(int) returns null when the ID does not exist")
    void viewPlantById_nonExistentId_returnsNull() {
        Plant result = plantRepository.viewPlant(99999);

        assertNull(result);
    }

    @Test
    @DisplayName("viewPlant(String) returns the plant when the common name exists")
    void viewPlantByName_existingName_returnsPlant() {
        seedPlant("Mint", "Herb");

        Plant result = plantRepository.viewPlant("Mint");

        assertNotNull(result);
        assertEquals("Herb", result.getTypeOfPlant());
    }

    @Test
    @DisplayName("viewPlant(String) returns null when the common name does not exist")
    void viewPlantByName_nonExistentName_returnsNull() {
        Plant result = plantRepository.viewPlant("Nonexistent Plant");

        assertNull(result);
    }

    @Test
    @DisplayName("viewAllPlants returns every plant in the database")
    void viewAllPlants_returnsAllSeededPlants() {
        seedPlant("Rose", "Flower");
        seedPlant("Tulsi", "Herb");

        List<Plant> result = plantRepository.viewAllPlants();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("viewAllPlants(typeOfPlant) returns only plants matching that type")
    void viewAllPlantsByType_returnsOnlyMatchingType() {
        seedPlant("Rose", "Flower");
        seedPlant("Tulsi", "Herb");
        seedPlant("Mint", "Herb");

        List<Plant> result = plantRepository.viewAllPlants("Herb");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getTypeOfPlant().equals("Herb")));
    }

    @Test
    @DisplayName("updatePlant persists changes to an existing plant")
    void updatePlant_existingPlant_persistsChanges() {
        Plant seeded = seedPlant("Rose", "Flower");
        seeded.setPlantCost(150.0);

        Plant updated = plantRepository.updatePlant(seeded);

        assertEquals(150.0, updated.getPlantCost());

        Plant refetched = plantRepository.viewPlant(seeded.getPlantId());
        assertEquals(150.0, refetched.getPlantCost());
    }

    @Test
    @DisplayName("deletePlant removes the plant from the database")
    void deletePlant_existingPlant_removesFromDatabase() {
        Plant seeded = seedPlant("Rose", "Flower");
        int idToDelete = seeded.getPlantId();

        plantRepository.deletePlant(seeded);

        Plant result = plantRepository.viewPlant(idToDelete);
        assertNull(result, "Plant should no longer exist after deletion");
    }

    @Test
    @DisplayName("countPlants returns the total number of plants in the database")
    void countPlants_returnsTotalPlants() {
        seedPlant("Rose", "Flower");
        seedPlant("Tulsi", "Herb");

        long totalPlants = plantRepository.countPlants();

        assertEquals(2, totalPlants);
    }
}
