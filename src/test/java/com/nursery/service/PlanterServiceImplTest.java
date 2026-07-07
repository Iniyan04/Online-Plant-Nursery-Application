package com.nursery.service;

import com.nursery.entity.Planter;
import com.nursery.exception.InvalidPlanterDataException;
import com.nursery.exception.PlanterNotFoundException;
import com.nursery.repository.IPlanterRepository;
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
@DisplayName("PlanterServiceImpl (US-009 to US-010)")
class PlanterServiceImplTest {

    @Mock
    private IPlanterRepository planterRepository;

    private IPlanterService planterService;

    @BeforeEach
    void setUp() {
        planterService = new PlanterServiceImpl(planterRepository);
    }

    private Planter validPlanter() {
        return new Planter(12.5f, 5, 3, 1, "Round", 20, 150, null, null);
    }

    @Test
    @DisplayName("addPlanter saves and returns the planter when data is valid")
    void addPlanter_withValidData_returnsSavedPlanter() {
        Planter planter = validPlanter();
        when(planterRepository.addPlanter(planter)).thenReturn(planter);

        Planter result = planterService.addPlanter(planter);

        assertNotNull(result);
        assertEquals("Round", result.getPlanterShape());
        verify(planterRepository, times(1)).addPlanter(planter);
    }

    @Test
    @DisplayName("addPlanter throws InvalidPlanterDataException when planterShape is blank")
    void addPlanter_withBlankShape_throwsException() {
        Planter planter = validPlanter();
        planter.setPlanterShape("  ");

        assertThrows(InvalidPlanterDataException.class, () -> planterService.addPlanter(planter));
        verify(planterRepository, never()).addPlanter(any());
    }

    @Test
    @DisplayName("updatePlanter throws PlanterNotFoundException when the planter does not exist")
    void updatePlanter_nonExistentPlanter_throwsException() {
        Planter planter = validPlanter();
        planter.setPlanterId(999);
        when(planterRepository.viewPlanter(999)).thenReturn(null);

        assertThrows(PlanterNotFoundException.class, () -> planterService.updatePlanter(planter));
        verify(planterRepository, never()).updatePlanter(any());
    }

    @Test
    @DisplayName("viewPlanter(int) throws PlanterNotFoundException when ID does not exist")
    void viewPlanterById_nonExistentId_throwsException() {
        when(planterRepository.viewPlanter(999)).thenReturn(null);

        assertThrows(PlanterNotFoundException.class, () -> planterService.viewPlanter(999));
    }

    @Test
    @DisplayName("viewAllPlanters returns the full list from the repository")
    void viewAllPlanters_returnsAllPlanters() {
        List<Planter> planters = Collections.singletonList(validPlanter());
        when(planterRepository.viewAllPlanters()).thenReturn(planters);

        List<Planter> result = planterService.viewAllPlanters();

        assertEquals(1, result.size());
        verify(planterRepository, times(1)).viewAllPlanters();
    }

    @Test
    @DisplayName("viewAllPlanters(minCost, maxCost) throws when minCost exceeds maxCost")
    void viewAllPlantersByCost_invalidRange_throwsException() {
        assertThrows(InvalidPlanterDataException.class,
                () -> planterService.viewAllPlanters(100, 50));
        verify(planterRepository, never()).viewAllPlanters(anyDouble(), anyDouble());
    }
}
