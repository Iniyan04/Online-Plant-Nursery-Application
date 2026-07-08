package com.nursery.repository;

import com.nursery.entity.Planter;
import com.nursery.entity.Plant;
import com.nursery.entity.Seed;
import com.nursery.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class PlanterRepositoryImpl implements IPlanterRepository {

    @Override
    public Planter addPlanter(Planter planter) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            linkAssociations(em, planter);
            em.persist(planter);
            em.getTransaction().commit();
            return planter;
        } finally {
            em.close();
        }
    }

    @Override
    public Planter updatePlanter(Planter planter) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            linkAssociations(em, planter);
            Planter merged = em.merge(planter);
            em.getTransaction().commit();
            return merged;
        } finally {
            em.close();
        }
    }

    @Override
    public Planter deletePlanter(Planter planter) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Planter managed = em.find(Planter.class, planter.getPlanterId());
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
    public Planter viewPlanter(int planterId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Planter.class, planterId);
        } finally {
            em.close();
        }
    }

    @Override
    public Planter viewPlanter(String planterShape) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Planter> query = em.createQuery(
                    "SELECT p FROM Planter p WHERE p.planterShape = :planterShape", Planter.class);
            query.setParameter("planterShape", planterShape);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Planter> viewAllPlanters() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Planter> query = em.createQuery("SELECT p FROM Planter p", Planter.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Planter> viewAllPlanters(double minCost, double maxCost) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Planter> query = em.createQuery(
                    "SELECT p FROM Planter p WHERE p.planterCost >= :minCost AND p.planterCost <= :maxCost",
                    Planter.class);
            query.setParameter("minCost", (int) minCost);
            query.setParameter("maxCost", (int) maxCost);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public long countPlanters() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(p) FROM Planter p", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    private void linkAssociations(EntityManager em, Planter planter) {
        if (planter.getPlants() != null && planter.getPlants().getPlantId() > 0) {
            Plant plant = em.find(Plant.class, planter.getPlants().getPlantId());
            planter.setPlants(plant);
        }
        if (planter.getSeeds() != null && planter.getSeeds().getSeedId() > 0) {
            Seed seed = em.find(Seed.class, planter.getSeeds().getSeedId());
            planter.setSeeds(seed);
        }
    }
}
