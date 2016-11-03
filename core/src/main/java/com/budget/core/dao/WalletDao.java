package com.budget.core.dao;

import com.budget.core.entity.Wallet;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.stream.Stream;

public interface WalletDao extends Repository<Wallet, Long> {

    Stream<Wallet> findByNameAndUserIdAndCurrencyId(String name, Long userId, Long currencyId);

    // Stream<Wallet> findByNameAndUserId(String name, Long userId);

    //Stream<Wallet> findByNameAndUserCurrencyId(String name, Long userCurrencyId);

    Wallet save(Wallet wallet);

    Stream<Wallet> findAll();

    void delete(Long id);

    Optional<Wallet> findOne(Long id);
}
