package com.budget.core.dao;

import com.budget.core.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.stream.Stream;

public interface CurrencyDao extends JpaRepository<Currency, Long> {
    Optional<Currency> findByName(String name);
}
