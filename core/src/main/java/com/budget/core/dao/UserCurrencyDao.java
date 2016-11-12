package com.budget.core.dao;

import com.budget.core.entity.UserCurrency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface UserCurrencyDao extends JpaRepository<UserCurrency, Long> {

    Stream<UserCurrency> findByUserId(Long userId);

    Optional<UserCurrency> findByUserIdAndCurrencyId(Long userId, Long currencyId);

}
