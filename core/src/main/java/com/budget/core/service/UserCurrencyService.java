package com.budget.core.service;

import com.budget.core.dao.UserCurrencyDao;
import com.budget.core.entity.Currency;
import com.budget.core.entity.UserCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserCurrencyService {

    private final UserCurrencyDao userCurrencyDao;

    @Autowired
    public UserCurrencyService(UserCurrencyDao userCurrencyDao) {
        this.userCurrencyDao = userCurrencyDao;
    }

    public List<UserCurrency> findAll() {
        return userCurrencyDao.findAll();
    }

    public UserCurrency save(UserCurrency userCurrency) {
        return userCurrencyDao.save(userCurrency);
    }

    public void delete(UserCurrency userCurrency) {
        userCurrencyDao.delete(userCurrency);
    }

    public Optional<UserCurrency> findOne(Long id) {
        return Optional.ofNullable(userCurrencyDao.findOne(id));
    }

    public UserCurrency create(UserCurrency userCurrency) {
        checkExistenceByUserAndCurrency(userCurrency);
        return userCurrencyDao.save(userCurrency);
    }

    private void checkExistenceByUserAndCurrency(UserCurrency userCurrency) {
        if(userCurrencyDao.findByUserIdAndCurrencyId(userCurrency.getUser().getId(), userCurrency.getCurrency().getId()).isPresent()) {
            String exMsg = String.format("Object already exists with user_name=%s, currency_name=$s",
                    userCurrency.getUser().getLogin(), userCurrency.getCurrency().getName());
            throw new IllegalArgumentException(exMsg);
        }
    }
}
