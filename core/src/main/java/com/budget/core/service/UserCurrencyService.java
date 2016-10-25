package com.budget.core.service;

import com.budget.core.dao.UserCurrencyDao;
import com.budget.core.entity.UserCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserCurrencyService {

    private final UserCurrencyDao userCurrencyDao;

    @Autowired
    public UserCurrencyService(UserCurrencyDao userCurrencyDao) {
        this.userCurrencyDao = userCurrencyDao;
    }

    public Stream<UserCurrency> findAll() {
        return userCurrencyDao.findAll();
    }

    public UserCurrency save(UserCurrency userCurrency) {
        return userCurrencyDao.save(userCurrency);
    }

    public void delete(Long id) {
        if(id == null) {
            throw new NullPointerException("Id cannot be null");
        }
        if(!findOne(id).isPresent()) {
            throw new NullPointerException("Object doesn't exist");
        }
        userCurrencyDao.delete(id);
    }

    public Optional<UserCurrency> findOne(Long id) {
        return userCurrencyDao.findOne(id);
    }

    public UserCurrency create(UserCurrency userCurrency) {
        checkExistenceByUserAndCurrency(userCurrency);
        return userCurrencyDao.save(userCurrency);
    }

    public UserCurrency update(UserCurrency userCurrency) {
        if(!findOne(userCurrency.getId()).isPresent()) {
            throw new NullPointerException("Object doesn't exist");
        }
        return userCurrencyDao.save(userCurrency);
    }

    public Stream<UserCurrency> findByUser(Long userId) {
        return userCurrencyDao.findByUserId(userId);
    }

    public Stream<UserCurrency> findByCurrency(Long currencyId) {
        return userCurrencyDao.findByCurrencyId(currencyId);
    }

    private void checkExistenceByUserAndCurrency(UserCurrency userCurrency) {
        if(userCurrencyDao.findByUserIdAndCurrencyId(userCurrency.getUser().getId(), userCurrency.getCurrency().getId()).isPresent()) {
            String exMsg = String.format("Object already exists with user_name=%s, currency_name=$s",
                    userCurrency.getUser().getLogin(), userCurrency.getCurrency().getName());
            throw new IllegalArgumentException(exMsg);
        }
    }
}
