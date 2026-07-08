package com.nursery.service;

import com.nursery.dto.DashboardResponse;
import com.nursery.repository.ICustomerRepository;
import com.nursery.repository.IOrderRepository;
import com.nursery.repository.IPlanterRepository;
import com.nursery.repository.IPlantRepository;
import com.nursery.repository.ISeedRepository;

public class AdminDashboardServiceImpl implements IAdminDashboardService {

    private final ICustomerRepository customerRepository;
    private final IOrderRepository orderRepository;
    private final IPlantRepository plantRepository;
    private final ISeedRepository seedRepository;
    private final IPlanterRepository planterRepository;

    public AdminDashboardServiceImpl(ICustomerRepository customerRepository,
                                     IOrderRepository orderRepository,
                                     IPlantRepository plantRepository,
                                     ISeedRepository seedRepository,
                                     IPlanterRepository planterRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.plantRepository = plantRepository;
        this.seedRepository = seedRepository;
        this.planterRepository = planterRepository;
    }

    @Override
    public DashboardResponse getDashboardData() {
        return new DashboardResponse(
                customerRepository.countCustomers(),
                orderRepository.countOrders(),
                plantRepository.countPlants(),
                seedRepository.countSeeds(),
                planterRepository.countPlanters(),
                orderRepository.countOrdersByStatus(OrderServiceImpl.ORDER_STATUS_ACTIVE),
                orderRepository.countOrdersByStatus(OrderServiceImpl.ORDER_STATUS_CANCELLED)
        );
    }
}
