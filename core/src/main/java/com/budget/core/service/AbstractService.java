package com.budget.core.service;

import java.util.Optional;

/**
 * Created by serhii.kremeznyi on 2016-11-22.
 */
public interface AbstractService<T> {
    Optional<T> findOne(Long id);
    void delete(Long id);
}
