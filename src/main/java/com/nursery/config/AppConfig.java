package com.nursery.config;

import com.nursery.repository.*;
import com.nursery.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the existing Sprint 1 Service/Repository implementations as Spring beans,
 * so they can be injected into REST controllers. None of the underlying classes
 * were changed - they're plain Java classes using JPAUtil/EntityManager directly.
 */
@Configuration
public class AppConfig {

    @Bean
    public ICustomerRepository customerRepository() {
        return new CustomerRepositoryImpl();
    }

    @Bean
    public IAdminRepository adminRepository() {
        return new AdminRepositoryImpl();
    }

    @Bean
    public IAdminService adminService(IAdminRepository adminRepository) {
        return new AdminServiceImpl(adminRepository);
    }

    @Bean
    public IAdminDashboardService adminDashboardService(ICustomerRepository customerRepository,
                                                        IOrderRepository orderRepository,
                                                        IPlantRepository plantRepository,
                                                        ISeedRepository seedRepository,
                                                        IPlanterRepository planterRepository) {
        return new AdminDashboardServiceImpl(
                customerRepository,
                orderRepository,
                plantRepository,
                seedRepository,
                planterRepository
        );
    }

    @Bean
    public ICustomerService customerService(ICustomerRepository customerRepository) {
        return new CustomerServiceImpl(customerRepository);
    }

    @Bean
    public IPlantRepository plantRepository() {
        return new PlantRepositoryImpl();
    }

    @Bean
    public IPlantService plantService(IPlantRepository plantRepository) {
        return new PlantServiceImpl(plantRepository);
    }

    @Bean
    public ISeedRepository seedRepository() {
        return new SeedRepositoryImpl();
    }

    @Bean
    public ISeedService seedService(ISeedRepository seedRepository) {
        return new SeedServiceImpl(seedRepository);
    }

    @Bean
    public IPlanterRepository planterRepository() {
        return new PlanterRepositoryImpl();
    }

    @Bean
    public IPlanterService planterService(IPlanterRepository planterRepository) {
        return new PlanterServiceImpl(planterRepository);
    }

    @Bean
    public IOrderRepository orderRepository() {
        return new OrderRepositoryImpl();
    }

    @Bean
    public IOrderService orderService(IOrderRepository orderRepository,
                                      IPlantRepository plantRepository,
                                      ISeedRepository seedRepository,
                                      IPlanterRepository planterRepository,
                                      ICustomerRepository customerRepository) {
        return new OrderServiceImpl(orderRepository, plantRepository, seedRepository, planterRepository, customerRepository);
    }
}
