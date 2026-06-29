package com.nursery.config;

import com.nursery.repository.CustomerRepositoryImpl;
import com.nursery.repository.ICustomerRepository;
import com.nursery.repository.IPlantRepository;
import com.nursery.repository.PlantRepositoryImpl;
import com.nursery.service.CustomerServiceImpl;
import com.nursery.service.ICustomerService;
import com.nursery.service.IPlantService;
import com.nursery.service.PlantServiceImpl;
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
}
