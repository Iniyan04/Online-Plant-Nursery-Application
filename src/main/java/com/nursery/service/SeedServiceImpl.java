package com.nursery.service;

import com.nursery.entity.Seed;
import com.nursery.exception.InvalidSeedDataException;
import com.nursery.exception.SeedNotFoundException;
import com.nursery.repository.ISeedRepository;

import java.util.List;

public class SeedServiceImpl implements ISeedService {

    private final ISeedRepository seedRepository;

    public SeedServiceImpl(ISeedRepository seedRepository) {
        this.seedRepository = seedRepository;
    }

    @Override
    public Seed addSeed(Seed seed) {
        validateSeedData(seed);
        return seedRepository.addSeed(seed);
    }

    @Override
    public Seed updateSeed(Seed seed) {
        validateSeedData(seed);

        Seed existing = seedRepository.viewSeed(seed.getSeedId());
        if (existing == null) {
            throw new SeedNotFoundException(
                    "No seed found with ID " + seed.getSeedId() + " to update");
        }

        return seedRepository.updateSeed(seed);
    }

    @Override
    public Seed deleteSeed(Seed seed) {
        if (seed == null) {
            throw new InvalidSeedDataException("Seed must not be null");
        }

        Seed existing = seedRepository.viewSeed(seed.getSeedId());
        if (existing == null) {
            throw new SeedNotFoundException(
                    "No seed found with ID " + seed.getSeedId() + " to delete");
        }

        return seedRepository.deleteSeed(seed);
    }

    @Override
    public Seed viewSeed(int seedId) {
        Seed seed = seedRepository.viewSeed(seedId);
        if (seed == null) {
            throw new SeedNotFoundException("No seed found with ID " + seedId);
        }
        return seed;
    }

    @Override
    public Seed viewSeed(String commonName) {
        Seed seed = seedRepository.viewSeed(commonName);
        if (seed == null) {
            throw new SeedNotFoundException("No seed found with common name '" + commonName + "'");
        }
        return seed;
    }

    @Override
    public List<Seed> viewAllSeeds() {
        return seedRepository.viewAllSeeds();
    }

    @Override
    public List<Seed> viewAllSeeds(String typeOfSeed) {
        return seedRepository.viewAllSeeds(typeOfSeed);
    }

    private void validateSeedData(Seed seed) {
        if (seed == null) {
            throw new InvalidSeedDataException("Seed must not be null");
        }
        if (isBlank(seed.getCommonName())) {
            throw new InvalidSeedDataException("Seed common name must not be empty");
        }
        if (isBlank(seed.getTypeOfSeeds())) {
            throw new InvalidSeedDataException("Seed type must not be empty");
        }
        if (seed.getSeedsCost() < 0) {
            throw new InvalidSeedDataException("Seed cost must not be negative");
        }
        if (seed.getSeedsStock() < 0) {
            throw new InvalidSeedDataException("Seed stock must not be negative");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
