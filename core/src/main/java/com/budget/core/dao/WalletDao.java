package com.budget.core.dao;

import com.budget.core.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface WalletDao extends JpaRepository<Wallet, Long> {
    Stream<Wallet> findByNameAndUserCurrencyId(String name, Long userCurrencyId);
}
