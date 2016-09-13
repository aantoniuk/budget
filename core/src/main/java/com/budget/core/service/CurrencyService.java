package com.budget.core.service;

import com.budget.core.dao.CurrencyDao;
import com.budget.core.entity.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<Currency> findOne(Long id) {
        return Optional.ofNullable(currencyDao.findOne(id));
    }

    public List<Currency> findAll() {
        return currencyDao.findAll();
    }

    public void delete(Long id) {
        currencyDao.delete(id);
    }

    public Optional<Currency> findByName(String name) {
        return Optional.ofNullable(currencyDao.findByName(name));
    }
}
