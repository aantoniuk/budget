package com.budget.core.service;

import com.budget.core.entity.BaseCategory;
import com.budget.core.entity.Wallet;

import java.util.stream.Stream;

public interface BaseCategoryService<T extends BaseCategory> extends AbstractService<T> {
    //Stream<BaseCategory> findByNameAndUserCurrencyId(String name, Long userCurrencyId);
    //Wallet updateUserCurrencyId(Long walletId, Long userCurrencyId);
    //Wallet updateName(Long walletId, String walletName);
    T create(T baseCategory);
    // Stream<T> findAll();
}
