package com.nursery.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    private static final String PERSISTENCE_UNIT_NAME = "nurseryPU";
    private static EntityManagerFactory factory;

    private JPAUtil() {
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return factory;
    }

    public static void close() {
        if (factory != null) {
            factory.close();
        }
    }
}
