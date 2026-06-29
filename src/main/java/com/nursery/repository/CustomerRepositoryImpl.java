package com.nursery.repository;

import com.nursery.entity.Customer;
import com.nursery.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class CustomerRepositoryImpl implements ICustomerRepository {

    @Override
    public Customer addCustomer(Customer customer) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(customer);
            em.getTransaction().commit();
            return customer;
        } finally {
            em.close();
        }
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Customer merged = em.merge(customer);
            em.getTransaction().commit();
            return merged;
        } finally {
            em.close();
        }
    }

    @Override
    public Customer deleteCustomer(Customer customer) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Customer managed = em.find(Customer.class, customer.getCustomerId());
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
    public Customer viewCustomer(int customerId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Customer.class, customerId);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Customer> viewAllCustomers() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery("SELECT c FROM Customer c", Customer.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Customer validateCustomer(String userName, String password) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                    "SELECT c FROM Customer c WHERE c.username = :username AND c.password = :password",
                    Customer.class);
            query.setParameter("username", userName);
            query.setParameter("password", password);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public Customer findByUsername(String userName) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                    "SELECT c FROM Customer c WHERE c.username = :username",
                    Customer.class);
            query.setParameter("username", userName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}
