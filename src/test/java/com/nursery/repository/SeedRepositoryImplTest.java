package com.nursery.repository;

import com.nursery.entity.Seed;
import com.nursery.util.JPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SeedRepositoryImpl (integration)")
class SeedRepositoryImplTest {

    private ISeedRepository seedRepository;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        seedRepository = new SeedRepositoryImpl();
        em = JPAUtil.getEntityManagerFactory().createEntityManager();
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Seed").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    private Seed seedSeed(String commonName, String typeOfSeeds) {
        Seed seed = new Seed(commonName, "Summer", "Daily", "Easy", "Warm",
                typeOfSeeds, "Test seed", 100, 49.99, 20);
        em.getTransaction().begin();
        em.persist(seed);
        em.getTransaction().commit();
        return seed;
    }

    @Test
    @DisplayName("addSeed persists a new seed and assigns a generated ID")
    void addSeed_newSeed_isPersistedWithGeneratedId() {
        Seed seed = new Seed("Basil", "Summer", "Daily", "Easy", "Warm",
                "Herb", "Aromatic herb seeds", 100, 49.99, 20);

        Seed saved = seedRepository.addSeed(seed);

        assertNotNull(saved);
        assertTrue(saved.getSeedId() > 0);
    }

    @Test
    @DisplayName("viewSeed(int) returns the seed when the ID exists")
    void viewSeedById_existingId_returnsSeed() {
        Seed seeded = seedSeed("Basil", "Herb");

        Seed result = seedRepository.viewSeed(seeded.getSeedId());

        assertNotNull(result);
        assertEquals("Basil", result.getCommonName());
    }

    @Test
    @DisplayName("viewAllSeeds(typeOfSeed) returns only seeds matching that type")
    void viewAllSeedsByType_returnsOnlyMatchingType() {
        seedSeed("Basil", "Herb");
        seedSeed("Tomato", "Vegetable");
        seedSeed("Mint", "Herb");

        List<Seed> result = seedRepository.viewAllSeeds("Herb");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(s -> s.getTypeOfSeeds().equals("Herb")));
    }

    @Test
    @DisplayName("updateSeed persists changes to an existing seed")
    void updateSeed_existingSeed_persistsChanges() {
        Seed seeded = seedSeed("Basil", "Herb");
        seeded.setSeedsCost(59.99);

        Seed updated = seedRepository.updateSeed(seeded);

        assertEquals(59.99, updated.getSeedsCost());
    }

    @Test
    @DisplayName("deleteSeed removes the seed from the database")
    void deleteSeed_existingSeed_removesFromDatabase() {
        Seed seeded = seedSeed("Basil", "Herb");
        int idToDelete = seeded.getSeedId();

        seedRepository.deleteSeed(seeded);

        Seed result = seedRepository.viewSeed(idToDelete);
        assertNull(result);
    }

    @Test
    @DisplayName("countSeeds returns the total number of seeds in the database")
    void countSeeds_returnsTotalSeeds() {
        seedSeed("Basil", "Herb");
        seedSeed("Mint", "Herb");

        long totalSeeds = seedRepository.countSeeds();

        assertEquals(2, totalSeeds);
    }
}
