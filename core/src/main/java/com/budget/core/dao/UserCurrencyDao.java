package com.budget.core.dao;

import com.budget.core.entity.UserCurrency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface UserCurrencyDao extends JpaRepository<UserCurrency, Long> {

    Stream<UserCurrency> findAllByUserId(Long userId);

    Optional<UserCurrency> findByUserIdAndCurrencyId(Long userId, Long currencyId);

//    Optional<UserCurrency> findByUserIdAndCurrencyId(Long userId, Long currencyId);
//
//    Stream<UserCurrency> findByUserId(Long userId);
//
//    Stream<UserCurrency> findByCurrencyId(Long currencyId);
//
//    UserCurrency save(UserCurrency currency);
//
//    Stream<UserCurrency> findAll();
//
//    void delete(Long id);
//
//    Optional<UserCurrency> findOne(Long id);
}
