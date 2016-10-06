package com.budget.core.service;

import com.budget.core.dao.CurrencyDao;
import com.budget.core.entity.Category;
import com.budget.core.entity.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

    public Currency create(Currency currency) {
        return currencyDao.save(currency);
    }

    public List<Currency> findAll() {
        return currencyDao.findAll();
    }

    public void delete(Long id) {
        currencyDao.delete(id);
    }

    public Currency update(Currency currency) {
        if(!findOne(currency.getId()).isPresent()) {
            throw new NullPointerException("Object doesn't exist");
        }
        return currencyDao.save(currency);
    }

    public Stream<Currency> findByName(String name) {
        return currencyDao.findByName(name);
    }
}
