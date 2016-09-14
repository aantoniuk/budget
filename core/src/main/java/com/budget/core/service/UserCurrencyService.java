package com.budget.core.service;

import com.budget.core.dao.UserCurrencyDao;
import com.budget.core.entity.UserCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
