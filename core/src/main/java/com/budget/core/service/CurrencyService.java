package com.budget.core.service;

import com.budget.core.dao.CurrencyDao;
import com.budget.core.entity.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by tolik on 9/4/2016.
 */
@Service
public class CurrencyService {

    private final CurrencyDao currencyDao;

    @Autowired
    public CurrencyService(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    public Currency save(Currency currency) {
        return currencyDao.save(currency);
    }

    public List<Currency> findAll() {
        return currencyDao.findAll();
    }
}
