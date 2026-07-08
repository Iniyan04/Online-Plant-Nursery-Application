package com.nursery.controller;

import com.nursery.entity.Planter;
import com.nursery.service.IAdminService;
import com.nursery.service.IPlanterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for US-009 (View Planters) and US-010 (Manage Planters CRUD).
 */
@RestController
@RequestMapping("/api/planters")
public class PlanterController {

    private final IPlanterService planterService;
    private final IAdminService adminService;

    public PlanterController(IPlanterService planterService, IAdminService adminService) {
        this.planterService = planterService;
        this.adminService = adminService;
    }

    /**
     * US-009: View all planters, optionally filtered by cost range.
     * GET /api/planters
     * GET /api/planters?minCost=10&maxCost=100
     */
    @GetMapping
    public ResponseEntity<List<Planter>> viewAllPlanters(
            @RequestParam(required = false) Double minCost,
            @RequestParam(required = false) Double maxCost) {
        List<Planter> planters = (minCost != null && maxCost != null)
                ? planterService.viewAllPlanters(minCost, maxCost)
                : planterService.viewAllPlanters();
        return ResponseEntity.ok(planters);
    }

    /**
     * US-009: View a single planter by ID.
     * GET /api/planters/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Planter> viewPlanterById(@PathVariable int id) {
        return ResponseEntity.ok(planterService.viewPlanter(id));
    }

    /**
     * US-009: View a single planter by shape.
     * GET /api/planters/by-shape/{planterShape}
     */
    @GetMapping("/by-shape/{planterShape}")
    public ResponseEntity<Planter> viewPlanterByShape(@PathVariable String planterShape) {
        return ResponseEntity.ok(planterService.viewPlanter(planterShape));
    }

    /**
     * US-010: Add a new planter.
     * POST /api/planters
     */
    @PostMapping
    public ResponseEntity<Planter> addPlanter(@RequestHeader("adminUsername") String adminUsername,
                                              @RequestHeader("adminPassword") String adminPassword,
                                              @RequestBody Planter planter) {
        authorizeAdmin(adminUsername, adminPassword);
        Planter saved = planterService.addPlanter(planter);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * US-010: Update an existing planter.
     * PUT /api/planters/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Planter> updatePlanter(@RequestHeader("adminUsername") String adminUsername,
                                                 @RequestHeader("adminPassword") String adminPassword,
                                                 @PathVariable int id,
                                                 @RequestBody Planter planter) {
        authorizeAdmin(adminUsername, adminPassword);
        planter.setPlanterId(id);
        Planter updated = planterService.updatePlanter(planter);
        return ResponseEntity.ok(updated);
    }

    /**
     * US-010: Delete a planter.
     * DELETE /api/planters/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlanter(@RequestHeader("adminUsername") String adminUsername,
                                              @RequestHeader("adminPassword") String adminPassword,
                                              @PathVariable int id) {
        authorizeAdmin(adminUsername, adminPassword);
        Planter planter = new Planter();
        planter.setPlanterId(id);
        planterService.deletePlanter(planter);
        return ResponseEntity.noContent().build();
    }

    private void authorizeAdmin(String adminUsername, String adminPassword) {
        adminService.validateAdmin(adminUsername, adminPassword);
    }
}
