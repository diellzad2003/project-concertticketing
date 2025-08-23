package com.example.common;

import com.example.common.CrudRepository;
import jakarta.transaction.Transactional;
import java.util.List;


public abstract class AbstractService<T, ID> {

    protected abstract CrudRepository<T, ID> getRepository();

    @Transactional
    public T create(T entity) {
        return getRepository().create(entity);
    }

    public T findById(ID id) {
        return getRepository().findById(id);
    }

    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Transactional
    public T update(T entity) {
        return getRepository().update(entity);
    }

    @Transactional
    public void delete(T entity) {
        getRepository().delete(entity);
    }
}
