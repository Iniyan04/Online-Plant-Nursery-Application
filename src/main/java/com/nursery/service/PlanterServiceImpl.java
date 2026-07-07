package com.nursery.service;

import com.nursery.entity.Planter;
import com.nursery.exception.InvalidPlanterDataException;
import com.nursery.exception.PlanterNotFoundException;
import com.nursery.repository.IPlanterRepository;

import java.util.List;

public class PlanterServiceImpl implements IPlanterService {

    private final IPlanterRepository planterRepository;

    public PlanterServiceImpl(IPlanterRepository planterRepository) {
        this.planterRepository = planterRepository;
    }

    @Override
    public Planter addPlanter(Planter planter) {
        validatePlanterData(planter);
        return planterRepository.addPlanter(planter);
    }

    @Override
    public Planter updatePlanter(Planter planter) {
        validatePlanterData(planter);

        Planter existing = planterRepository.viewPlanter(planter.getPlanterId());
        if (existing == null) {
            throw new PlanterNotFoundException(
                    "No planter found with ID " + planter.getPlanterId() + " to update");
        }

        return planterRepository.updatePlanter(planter);
    }

    @Override
    public Planter deletePlanter(Planter planter) {
        if (planter == null) {
            throw new InvalidPlanterDataException("Planter must not be null");
        }

        Planter existing = planterRepository.viewPlanter(planter.getPlanterId());
        if (existing == null) {
            throw new PlanterNotFoundException(
                    "No planter found with ID " + planter.getPlanterId() + " to delete");
        }

        return planterRepository.deletePlanter(planter);
    }

    @Override
    public Planter viewPlanter(int planterId) {
        Planter planter = planterRepository.viewPlanter(planterId);
        if (planter == null) {
            throw new PlanterNotFoundException("No planter found with ID " + planterId);
        }
        return planter;
    }

    @Override
    public Planter viewPlanter(String planterShape) {
        Planter planter = planterRepository.viewPlanter(planterShape);
        if (planter == null) {
            throw new PlanterNotFoundException("No planter found with shape '" + planterShape + "'");
        }
        return planter;
    }

    @Override
    public List<Planter> viewAllPlanters() {
        return planterRepository.viewAllPlanters();
    }

    @Override
    public List<Planter> viewAllPlanters(double minCost, double maxCost) {
        if (minCost < 0 || maxCost < 0) {
            throw new InvalidPlanterDataException("Cost range values must not be negative");
        }
        if (minCost > maxCost) {
            throw new InvalidPlanterDataException("Minimum cost must not exceed maximum cost");
        }
        return planterRepository.viewAllPlanters(minCost, maxCost);
    }

    private void validatePlanterData(Planter planter) {
        if (planter == null) {
            throw new InvalidPlanterDataException("Planter must not be null");
        }
        if (isBlank(planter.getPlanterShape())) {
            throw new InvalidPlanterDataException("Planter shape must not be empty");
        }
        if (planter.getPlanterCost() < 0) {
            throw new InvalidPlanterDataException("Planter cost must not be negative");
        }
        if (planter.getPlanterStock() < 0) {
            throw new InvalidPlanterDataException("Planter stock must not be negative");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
