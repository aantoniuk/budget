package com.budget.core.service;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public abstract class AbstractServiceImpl<T> implements AbstractService<T> {
    abstract CrudRepository<T, Long> getDao();

    public Optional<T> findOne(Long id) {
        return Optional.ofNullable(getDao().findOne(id));
    }

    public void delete(Long id) {
        if(id == null) {
            throw new NullPointerException("Id cannot be null");
        }
        if(!findOne(id).isPresent()) {
            throw new NullPointerException("Object doesn't exist");
        }
        getDao().delete(id);
    }
}
