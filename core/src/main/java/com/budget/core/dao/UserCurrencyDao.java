package com.budget.core.dao;

import com.budget.core.entity.UserCurrency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCurrencyDao extends JpaRepository<UserCurrency, Long>{
    Optional<UserCurrency> findByUserIdAndCurrencyId(Long userId, Long currencyId);
}
