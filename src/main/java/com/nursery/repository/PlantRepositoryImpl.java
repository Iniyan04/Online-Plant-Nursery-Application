package com.nursery.repository;

import com.nursery.entity.Plant;
import com.nursery.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class PlantRepositoryImpl implements IPlantRepository {

    @Override
    public Plant addPlant(Plant plant) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(plant);
            em.getTransaction().commit();
            return plant;
        } finally {
            em.close();
        }
    }

    @Override
    public Plant updatePlant(Plant plant) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Plant merged = em.merge(plant);
            em.getTransaction().commit();
            return merged;
        } finally {
            em.close();
        }
    }

    @Override
    public Plant deletePlant(Plant plant) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Plant managed = em.find(Plant.class, plant.getPlantId());
            if (managed != null) {
                em.remove(managed);
            }
            em.getTransaction().commit();
            return managed;
        } finally {
            em.close();
        }
    }

    @Override
    public Plant viewPlant(int plantId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Plant.class, plantId);
        } finally {
            em.close();
        }
    }

    @Override
    public Plant viewPlant(String commonName) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Plant> query = em.createQuery(
                    "SELECT p FROM Plant p WHERE p.commonName = :commonName", Plant.class);
            query.setParameter("commonName", commonName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Plant> viewAllPlants() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Plant> query = em.createQuery("SELECT p FROM Plant p", Plant.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Plant> viewAllPlants(String typeOfPlant) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Plant> query = em.createQuery(
                    "SELECT p FROM Plant p WHERE p.typeOfPlant = :typeOfPlant", Plant.class);
            query.setParameter("typeOfPlant", typeOfPlant);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
