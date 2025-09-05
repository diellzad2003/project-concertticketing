package com.example.repository;

import com.example.common.CrudRepository;
import com.example.domain.BookingItem;
import jakarta.inject.Inject;              // JSR-330 (works with HK2)
import jakarta.persistence.EntityManager;
import java.util.List;

public class BookingItemRepository implements CrudRepository<BookingItem, Integer> {

    @Inject
    EntityManager em;

    @Override
    public BookingItem create(BookingItem item) {
        try {
            em.getTransaction().begin();
            em.persist(item);
            em.getTransaction().commit();
            return item;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public BookingItem findById(Integer id) {
        return em.find(BookingItem.class, id);
    }

    @Override
    public List<BookingItem> findAll() {
        return em.createQuery("SELECT b FROM BookingItem b", BookingItem.class)
                .getResultList();
    }

    @Override
    public BookingItem update(BookingItem item) {
        try {
            em.getTransaction().begin();
            BookingItem merged = em.merge(item);
            em.getTransaction().commit();
            return merged;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(BookingItem item) {
        try {
            em.getTransaction().begin();
            BookingItem managed = em.contains(item) ? item : em.merge(item);
            em.remove(managed);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }
}
