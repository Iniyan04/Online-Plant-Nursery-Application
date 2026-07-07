package com.nursery.service;

import com.nursery.entity.Seed;
import com.nursery.exception.InvalidSeedDataException;
import com.nursery.exception.SeedNotFoundException;
import com.nursery.repository.ISeedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SeedServiceImpl (US-007 to US-008)")
class SeedServiceImplTest {

    @Mock
    private ISeedRepository seedRepository;

    private ISeedService seedService;

    @BeforeEach
    void setUp() {
        seedService = new SeedServiceImpl(seedRepository);
    }

    private Seed validSeed() {
        return new Seed("Basil", "Summer", "Daily", "Easy", "Warm",
                "Herb", "Aromatic herb seeds", 100, 49.99, 20);
    }

    @Test
    @DisplayName("addSeed saves and returns the seed when data is valid")
    void addSeed_withValidData_returnsSavedSeed() {
        Seed seed = validSeed();
        when(seedRepository.addSeed(seed)).thenReturn(seed);

        Seed result = seedService.addSeed(seed);

        assertNotNull(result);
        assertEquals("Basil", result.getCommonName());
        verify(seedRepository, times(1)).addSeed(seed);
    }

    @Test
    @DisplayName("addSeed throws InvalidSeedDataException when commonName is blank")
    void addSeed_withBlankCommonName_throwsException() {
        Seed seed = validSeed();
        seed.setCommonName("  ");

        assertThrows(InvalidSeedDataException.class, () -> seedService.addSeed(seed));
        verify(seedRepository, never()).addSeed(any());
    }

    @Test
    @DisplayName("addSeed throws InvalidSeedDataException when typeOfSeeds is blank")
    void addSeed_withBlankTypeOfSeeds_throwsException() {
        Seed seed = validSeed();
        seed.setTypeOfSeeds("  ");

        assertThrows(InvalidSeedDataException.class, () -> seedService.addSeed(seed));
        verify(seedRepository, never()).addSeed(any());
    }

    @Test
    @DisplayName("addSeed throws InvalidSeedDataException when seedsCost is negative")
    void addSeed_withNegativeCost_throwsException() {
        Seed seed = validSeed();
        seed.setSeedsCost(-5.0);

        assertThrows(InvalidSeedDataException.class, () -> seedService.addSeed(seed));
        verify(seedRepository, never()).addSeed(any());
    }

    @Test
    @DisplayName("updateSeed throws SeedNotFoundException when the seed does not exist")
    void updateSeed_nonExistentSeed_throwsException() {
        Seed seed = validSeed();
        seed.setSeedId(999);
        when(seedRepository.viewSeed(999)).thenReturn(null);

        assertThrows(SeedNotFoundException.class, () -> seedService.updateSeed(seed));
        verify(seedRepository, never()).updateSeed(any());
    }

    @Test
    @DisplayName("deleteSeed throws SeedNotFoundException when the seed does not exist")
    void deleteSeed_nonExistentSeed_throwsException() {
        Seed seed = validSeed();
        seed.setSeedId(999);
        when(seedRepository.viewSeed(999)).thenReturn(null);

        assertThrows(SeedNotFoundException.class, () -> seedService.deleteSeed(seed));
        verify(seedRepository, never()).deleteSeed(any());
    }

    @Test
    @DisplayName("viewSeed(int) throws SeedNotFoundException when ID does not exist")
    void viewSeedById_nonExistentId_throwsException() {
        when(seedRepository.viewSeed(999)).thenReturn(null);

        assertThrows(SeedNotFoundException.class, () -> seedService.viewSeed(999));
    }

    @Test
    @DisplayName("viewAllSeeds returns the full list from the repository")
    void viewAllSeeds_returnsAllSeeds() {
        List<Seed> seeds = Collections.singletonList(validSeed());
        when(seedRepository.viewAllSeeds()).thenReturn(seeds);

        List<Seed> result = seedService.viewAllSeeds();

        assertEquals(1, result.size());
        verify(seedRepository, times(1)).viewAllSeeds();
    }

    @Test
    @DisplayName("viewAllSeeds(typeOfSeed) returns the filtered list from the repository")
    void viewAllSeedsByType_returnsFilteredSeeds() {
        List<Seed> seeds = Collections.singletonList(validSeed());
        when(seedRepository.viewAllSeeds("Herb")).thenReturn(seeds);

        List<Seed> result = seedService.viewAllSeeds("Herb");

        assertEquals(1, result.size());
        verify(seedRepository, times(1)).viewAllSeeds("Herb");
    }
}
