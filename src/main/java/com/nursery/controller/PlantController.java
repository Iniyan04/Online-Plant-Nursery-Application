package com.nursery.controller;

import com.nursery.entity.Plant;
import com.nursery.service.IPlantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for US-003 (View Plants), US-004 (Add Plant),
 * US-005 (Update Plant), and US-006 (Delete Plant).
 */
@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private final IPlantService plantService;

    public PlantController(IPlantService plantService) {
        this.plantService = plantService;
    }

    /**
     * US-003: View all plants, optionally filtered by type.
     * GET /api/plants
     * GET /api/plants?type=Herb
     */
    @GetMapping
    public ResponseEntity<List<Plant>> viewAllPlants(@RequestParam(required = false) String type) {
        List<Plant> plants = (type == null || type.isBlank())
                ? plantService.viewAllPlants()
                : plantService.viewAllPlants(type);
        return ResponseEntity.ok(plants);
    }

    /**
     * US-003: View a single plant by ID.
     * GET /api/plants/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Plant> viewPlantById(@PathVariable int id) {
        return ResponseEntity.ok(plantService.viewPlant(id));
    }

    /**
     * US-003: View a single plant by common name.
     * GET /api/plants/by-name/{commonName}
     */
    @GetMapping("/by-name/{commonName}")
    public ResponseEntity<Plant> viewPlantByName(@PathVariable String commonName) {
        return ResponseEntity.ok(plantService.viewPlant(commonName));
    }

    /**
     * US-004: Add a new plant.
     * POST /api/plants
     */
    @PostMapping
    public ResponseEntity<Plant> addPlant(@RequestBody Plant plant) {
        Plant saved = plantService.addPlant(plant);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * US-005: Update an existing plant.
     * PUT /api/plants/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Plant> updatePlant(@PathVariable int id, @RequestBody Plant plant) {
        plant.setPlantId(id);
        Plant updated = plantService.updatePlant(plant);
        return ResponseEntity.ok(updated);
    }

    /**
     * US-006: Delete a plant.
     * DELETE /api/plants/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable int id) {
        Plant plant = new Plant();
        plant.setPlantId(id);
        plantService.deletePlant(plant);
        return ResponseEntity.noContent().build();
    }
}
