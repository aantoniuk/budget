package com.budget.core.service;

import com.budget.core.entity.Currency;
import com.budget.core.entity.Wallet;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by serhii.kremeznyi on 2016-11-24.
 */
public interface CurrencyService extends AbstractService<Currency> {
    Currency create(Currency currency);
    Stream<Currency> findAll();
    Optional<Currency> findByName(String name);
    Currency update(Currency currency);
}
