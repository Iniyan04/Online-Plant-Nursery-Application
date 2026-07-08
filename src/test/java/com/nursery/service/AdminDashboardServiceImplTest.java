package com.nursery.service;

import com.nursery.dto.DashboardResponse;
import com.nursery.repository.ICustomerRepository;
import com.nursery.repository.IOrderRepository;
import com.nursery.repository.IPlanterRepository;
import com.nursery.repository.IPlantRepository;
import com.nursery.repository.ISeedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminDashboardServiceImpl")
class AdminDashboardServiceImplTest {

    @Mock
    private ICustomerRepository customerRepository;

    @Mock
    private IOrderRepository orderRepository;

    @Mock
    private IPlantRepository plantRepository;

    @Mock
    private ISeedRepository seedRepository;

    @Mock
    private IPlanterRepository planterRepository;

    private IAdminDashboardService adminDashboardService;

    @BeforeEach
    void setUp() {
        adminDashboardService = new AdminDashboardServiceImpl(
                customerRepository,
                orderRepository,
                plantRepository,
                seedRepository,
                planterRepository
        );
    }

    @Test
    @DisplayName("getDashboardData aggregates counts from all repositories")
    void getDashboardData_returnsAggregatedCounts() {
        when(customerRepository.countCustomers()).thenReturn(10L);
        when(orderRepository.countOrders()).thenReturn(20L);
        when(plantRepository.countPlants()).thenReturn(6L);
        when(seedRepository.countSeeds()).thenReturn(8L);
        when(planterRepository.countPlanters()).thenReturn(4L);
        when(orderRepository.countOrdersByStatus(OrderServiceImpl.ORDER_STATUS_ACTIVE)).thenReturn(14L);
        when(orderRepository.countOrdersByStatus(OrderServiceImpl.ORDER_STATUS_CANCELLED)).thenReturn(6L);

        DashboardResponse response = adminDashboardService.getDashboardData();

        assertEquals(10L, response.getTotalCustomers());
        assertEquals(20L, response.getTotalOrders());
        assertEquals(6L, response.getTotalPlants());
        assertEquals(8L, response.getTotalSeeds());
        assertEquals(4L, response.getTotalPlanters());
        assertEquals(14L, response.getActiveOrders());
        assertEquals(6L, response.getCancelledOrders());
    }
}
