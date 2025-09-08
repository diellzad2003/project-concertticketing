package com.example.repository;

import com.example.common.CrudRepository;
import com.example.domain.BookingItem;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

public class BookingItemRepository implements CrudRepository<BookingItem, Integer> {

    @Inject EntityManager em;

    @Override
    public BookingItem create(BookingItem item) {
        em.persist(item);
        return item;
    }

    @Override
    public BookingItem update(BookingItem item) {
        return em.merge(item);
    }

    @Override
    public void delete(BookingItem item) {
        BookingItem managed = em.contains(item) ? item : em.merge(item);
        em.remove(managed);
    }

    @Override
    public BookingItem findById(Integer id) {
        return em.find(BookingItem.class, id);
    }

    @Override
    public List<BookingItem> findAll() {
        return em.createQuery("SELECT bi FROM BookingItem bi", BookingItem.class)
                .getResultList();
    }
}
