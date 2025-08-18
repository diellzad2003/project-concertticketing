package com.example.repository;

import com.example.entity.Payment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class PaymentRepository {

    @PersistenceContext
    private EntityManager em;

    public void create(Payment payment) {
        em.persist(payment);
    }

    public Payment findById(Integer id) {
        return em.find(Payment.class, id);
    }

    public List<Payment> findAll() {
        return em.createQuery("SELECT p FROM Payment p", Payment.class).getResultList();
    }

    public Payment update(Payment payment) {
        return em.merge(payment);
    }

    public void delete(Payment payment) {
        if (!em.contains(payment)) {
            payment = em.merge(payment);
        }
        em.remove(payment);
    }
}
