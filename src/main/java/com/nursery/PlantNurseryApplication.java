package com.nursery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * Spring Boot entry point.
 *
 * HibernateJpaAutoConfiguration is excluded because this project manages its
 * own EntityManagerFactory via JPAUtil + persistence.xml (Sprint 1 style),
 * rather than using Spring Data JPA's auto-configured DataSource/EntityManager.
 */
@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class PlantNurseryApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlantNurseryApplication.class, args);
    }
}
