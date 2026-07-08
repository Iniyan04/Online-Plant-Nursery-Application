package com.nursery.repository;

import com.nursery.entity.Seed;
import java.util.List;

public interface ISeedRepository {

    Seed addSeed(Seed seed);

    Seed updateSeed(Seed seed);

    Seed deleteSeed(Seed seed);

    Seed viewSeed(int seedId);

    Seed viewSeed(String commonName);

    List<Seed> viewAllSeeds();

    List<Seed> viewAllSeeds(String typeOfSeed);

    long countSeeds();
}
