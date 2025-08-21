package com.example.repository;

import com.example.domain.BookingItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class BookingItemRepository {

    @PersistenceContext
    private EntityManager em;

    public void create(BookingItem item) {
        em.persist(item);
    }

    public BookingItem findById(Integer id) {
        return em.find(BookingItem.class, id);
    }

    public List<BookingItem> findAll() {
        return em.createQuery("SELECT b FROM BookingItem b", BookingItem.class)
                .getResultList();
    }

    public BookingItem update(BookingItem item) {
        return em.merge(item);
    }

    public void delete(BookingItem item) {
        if (!em.contains(item)) {
            item = em.merge(item);
        }
        em.remove(item);
    }
}
