package com.nursery.repository;

import com.nursery.entity.Seed;
import com.nursery.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class SeedRepositoryImpl implements ISeedRepository {

    @Override
    public Seed addSeed(Seed seed) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(seed);
            em.getTransaction().commit();
            return seed;
        } finally {
            em.close();
        }
    }

    @Override
    public Seed updateSeed(Seed seed) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Seed merged = em.merge(seed);
            em.getTransaction().commit();
            return merged;
        } finally {
            em.close();
        }
    }

    @Override
    public Seed deleteSeed(Seed seed) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Seed managed = em.find(Seed.class, seed.getSeedId());
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
    public Seed viewSeed(int seedId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Seed.class, seedId);
        } finally {
            em.close();
        }
    }

    @Override
    public Seed viewSeed(String commonName) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Seed> query = em.createQuery(
                    "SELECT s FROM Seed s WHERE s.commonName = :commonName", Seed.class);
            query.setParameter("commonName", commonName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Seed> viewAllSeeds() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Seed> query = em.createQuery("SELECT s FROM Seed s", Seed.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Seed> viewAllSeeds(String typeOfSeed) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Seed> query = em.createQuery(
                    "SELECT s FROM Seed s WHERE s.typeOfSeeds = :typeOfSeed", Seed.class);
            query.setParameter("typeOfSeed", typeOfSeed);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public long countSeeds() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(s) FROM Seed s", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}
