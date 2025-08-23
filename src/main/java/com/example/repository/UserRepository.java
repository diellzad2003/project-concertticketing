package com.example.repository;

import com.example.common.CrudRepository;
import com.example.domain.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class UserRepository implements CrudRepository<User, Integer> {

    @PersistenceContext
    private EntityManager em;

    public User create(User user) {
        em.persist(user);
        return user;
    }

    public User findById(Integer id) {
        return em.find(User.class, id);
    }

    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public User update(User user) {
        return em.merge(user);
    }

    public void delete(User user) {
        if (!em.contains(user)) {
            user = em.merge(user);
        }
        em.remove(user);
    }

    public User findByEmail(String email) {
        List<User> result = em.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }
}
