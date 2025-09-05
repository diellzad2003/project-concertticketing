package com.example.repository;

import com.example.common.CrudRepository;
import com.example.domain.Payment;
import jakarta.inject.Inject;              // JSR-330 (works with HK2)
import jakarta.persistence.EntityManager;

import java.util.List;

public class PaymentRepository implements CrudRepository<Payment, Integer> {

    @Inject
    EntityManager em;

    @Override
    public Payment create(Payment payment) {
        try {
            em.getTransaction().begin();
            em.persist(payment);
            em.getTransaction().commit();
            return payment;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public Payment findById(Integer id) {
        return em.find(Payment.class, id);
    }

    @Override
    public List<Payment> findAll() {
        return em.createQuery("SELECT p FROM Payment p", Payment.class)
                .getResultList();
    }

    @Override
    public Payment update(Payment payment) {
        try {
            em.getTransaction().begin();
            Payment merged = em.merge(payment);
            em.getTransaction().commit();
            return merged;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(Payment payment) {
        try {
            em.getTransaction().begin();
            Payment managed = em.contains(payment) ? payment : em.merge(payment);
            em.remove(managed);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }
}
