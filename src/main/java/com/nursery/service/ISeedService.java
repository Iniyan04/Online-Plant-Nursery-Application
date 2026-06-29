package com.nursery.service;

import com.nursery.entity.Seed;
import java.util.List;

public interface ISeedService {

    Seed addSeed(Seed seed);

    Seed updateSeed(Seed seed);

    Seed deleteSeed(Seed seed);

    Seed viewSeed(int seedId);

    Seed viewSeed(String commonName);

    List<Seed> viewAllSeeds();

    List<Seed> viewAllSeeds(String typeOfSeed);
}
