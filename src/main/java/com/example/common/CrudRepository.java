package com.example.common;

import java.util.List;

public interface CrudRepository<T, ID> {
    T create(T entity);
    T findById(ID id);
    List<T> findAll();
    T update(T entity);
    void delete(T entity);
}
