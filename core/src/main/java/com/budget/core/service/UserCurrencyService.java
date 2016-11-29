package com.budget.core.service;

import com.budget.core.entity.UserCurrency;

import java.util.stream.Stream;

/**
 * Created by serhii.kremeznyi on 2016-11-22.
 */
public interface UserCurrencyService extends AbstractService<UserCurrency> {
    Stream<UserCurrency> findByUserId(Long userId);
    UserCurrency updateCurrencyId(Long userCurrencyId, Long currencyId);
    UserCurrency updateValue(UserCurrency userCurrency) throws IllegalArgumentException, NullPointerException;
    UserCurrency save(UserCurrency userCurrency);
    UserCurrency create(UserCurrency userCurrency);
}
