package com.example.repository;

import com.example.common.CrudRepository;
import com.example.domain.User;
import jakarta.inject.Inject;              // JSR-330 (works with HK2)
import jakarta.persistence.EntityManager;
import java.util.List;

// No CDI annotations here (no @ApplicationScoped)

public class UserRepository implements CrudRepository<User, Integer> {

    @Inject
    EntityManager em; // provided by your HK2 Factory via ApplicationConfig binder

    @Override
    public User create(User user) {
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return user;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public User findById(Integer id) {
        return em.find(User.class, id);
    }

    @Override
    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    public User update(User user) {
        try {
            em.getTransaction().begin();
            User merged = em.merge(user);
            em.getTransaction().commit();
            return merged;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(User user) {
        try {
            em.getTransaction().begin();
            User managed = user;
            if (!em.contains(user)) {
                managed = em.merge(user);
            }
            em.remove(managed);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    public User findByEmail(String email) {
        List<User> result = em.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }
}
