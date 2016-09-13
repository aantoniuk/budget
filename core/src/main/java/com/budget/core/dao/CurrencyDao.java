package com.budget.core.dao;

import com.budget.core.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyDao extends JpaRepository<Currency, Long>{
    Currency findByName(String name);
}
