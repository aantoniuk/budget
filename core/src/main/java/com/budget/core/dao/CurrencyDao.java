package com.budget.core.dao;

import com.budget.core.entity.Currency;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.stream.Stream;

public interface CurrencyDao extends Repository<Currency, Long> {

    Stream<Currency> findByName(String name);

    Currency save(Currency currency);

    Optional<Currency> findOne(Long id);

    Stream<Currency> findAll();

    void delete(Long id);

}
