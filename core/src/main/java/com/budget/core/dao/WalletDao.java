package com.budget.core.dao;

import com.budget.core.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletDao extends JpaRepository<Wallet, Long>{

    public Wallet findByNameAndUserCurrencyId(String name, long userCurrencyId);
}
