package com.nursery.controller;

import com.nursery.entity.Seed;
import com.nursery.service.ISeedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for US-007 (View Seeds) and US-008 (Manage Seeds CRUD).
 */
@RestController
@RequestMapping("/api/seeds")
public class SeedController {

    private final ISeedService seedService;

    public SeedController(ISeedService seedService) {
        this.seedService = seedService;
    }

    /**
     * US-007: View all seeds, optionally filtered by type.
     * GET /api/seeds
     * GET /api/seeds?type=Vegetable
     */
    @GetMapping
    public ResponseEntity<List<Seed>> viewAllSeeds(@RequestParam(required = false) String type) {
        List<Seed> seeds = (type == null || type.isBlank())
                ? seedService.viewAllSeeds()
                : seedService.viewAllSeeds(type);
        return ResponseEntity.ok(seeds);
    }

    /**
     * US-007: View a single seed by ID.
     * GET /api/seeds/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Seed> viewSeedById(@PathVariable int id) {
        return ResponseEntity.ok(seedService.viewSeed(id));
    }

    /**
     * US-007: View a single seed by common name.
     * GET /api/seeds/by-name/{commonName}
     */
    @GetMapping("/by-name/{commonName}")
    public ResponseEntity<Seed> viewSeedByName(@PathVariable String commonName) {
        return ResponseEntity.ok(seedService.viewSeed(commonName));
    }

    /**
     * US-008: Add a new seed.
     * POST /api/seeds
     */
    @PostMapping
    public ResponseEntity<Seed> addSeed(@RequestBody Seed seed) {
        Seed saved = seedService.addSeed(seed);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * US-008: Update an existing seed.
     * PUT /api/seeds/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Seed> updateSeed(@PathVariable int id, @RequestBody Seed seed) {
        seed.setSeedId(id);
        Seed updated = seedService.updateSeed(seed);
        return ResponseEntity.ok(updated);
    }

    /**
     * US-008: Delete a seed.
     * DELETE /api/seeds/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeed(@PathVariable int id) {
        Seed seed = new Seed();
        seed.setSeedId(id);
        seedService.deleteSeed(seed);
        return ResponseEntity.noContent().build();
    }
}
