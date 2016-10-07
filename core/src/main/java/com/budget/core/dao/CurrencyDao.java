package com.budget.core.dao;

import com.budget.core.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface CurrencyDao extends JpaRepository<Currency, Long>{
    Stream<Currency> findByName(String name);
}
