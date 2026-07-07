package com.nursery.repository;

import com.nursery.entity.Planter;
import com.nursery.entity.Plant;
import com.nursery.entity.Seed;
import com.nursery.util.JPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PlanterRepositoryImpl (integration)")
class PlanterRepositoryImplTest {

    private IPlanterRepository planterRepository;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        planterRepository = new PlanterRepositoryImpl();
        em = JPAUtil.getEntityManagerFactory().createEntityManager();
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Planter").executeUpdate();
        em.createQuery("DELETE FROM Plant").executeUpdate();
        em.createQuery("DELETE FROM Seed").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @DisplayName("addPlanter persists a new planter and assigns a generated ID")
    void addPlanter_newPlanter_isPersistedWithGeneratedId() {
        Planter planter = new Planter(12.5f, 5, 3, 1, "Round", 20, 150, null, null);

        Planter saved = planterRepository.addPlanter(planter);

        assertNotNull(saved);
        assertTrue(saved.getPlanterId() > 0);
    }

    @Test
    @DisplayName("viewAllPlanters(minCost, maxCost) returns planters within the cost range")
    void viewAllPlantersByCost_returnsMatchingPlanters() {
        planterRepository.addPlanter(new Planter(10f, 3, 2, 1, "Square", 10, 100, null, null));
        planterRepository.addPlanter(new Planter(12f, 5, 3, 2, "Round", 15, 200, null, null));
        planterRepository.addPlanter(new Planter(15f, 8, 4, 3, "Oval", 5, 300, null, null));

        List<Planter> result = planterRepository.viewAllPlanters(100, 250);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("addPlanter links to existing plant and seed by ID")
    void addPlanter_withPlantAndSeed_linksExistingEntities() {
        Plant plant = new Plant(30, "Medium", "Rose", "Summer", "Ornamental",
                "Easy", "Warm", "Flower", "Test plant", 50, 99.99);
        em.getTransaction().begin();
        em.persist(plant);
        Seed seed = new Seed("Rose Seed", "Summer", "Daily", "Easy", "Warm",
                "Flower", "Rose seeds", 100, 29.99, 10);
        em.persist(seed);
        em.getTransaction().commit();

        Plant plantRef = new Plant();
        plantRef.setPlantId(plant.getPlantId());
        Seed seedRef = new Seed();
        seedRef.setSeedId(seed.getSeedId());
        Planter planter = new Planter(12.5f, 5, 3, 1, "Round", 20, 150, plantRef, seedRef);

        Planter saved = planterRepository.addPlanter(planter);

        assertNotNull(saved.getPlants());
        assertEquals("Rose", saved.getPlants().getCommonName());
        assertNotNull(saved.getSeeds());
        assertEquals("Rose Seed", saved.getSeeds().getCommonName());
    }

    @Test
    @DisplayName("deletePlanter removes the planter from the database")
    void deletePlanter_existingPlanter_removesFromDatabase() {
        Planter planter = planterRepository.addPlanter(
                new Planter(12.5f, 5, 3, 1, "Round", 20, 150, null, null));
        int idToDelete = planter.getPlanterId();

        planterRepository.deletePlanter(planter);

        Planter result = planterRepository.viewPlanter(idToDelete);
        assertNull(result);
    }
}
