package com.budget.core.service;

import com.budget.core.entity.Wallet;

import java.util.stream.Stream;

/**
 * Created by serhii.kremeznyi on 2016-11-24.
 */
public interface WalletService extends AbstractService<Wallet> {
    Stream<Wallet> findByNameAndUserCurrencyId(String name, Long userCurrencyId);
    Wallet updateUserCurrencyId(Long walletId, Long userCurrencyId);
    Wallet updateName(Long walletId, String walletName);
    Wallet create(Wallet wallet);
    Stream<Wallet> findAll();
}
