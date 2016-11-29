package com.budget.core.service;

import com.budget.core.dao.CurrencyDao;
import com.budget.core.entity.Currency;
import com.budget.core.entity.UserCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class CurrencyServiceImp extends AbstractServiceImpl<Currency> implements CurrencyService {

    private final CurrencyDao currencyDao;

    @Autowired
    public CurrencyServiceImp(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    @Override
    CrudRepository<Currency, Long> getDao() {
        return currencyDao;
    }

    public Currency save(Currency currency) {
        return currencyDao.save(currency);
    }

    public Optional<Currency> findOne(Long id) {
        return Optional.ofNullable(currencyDao.findOne(id));
    }

    public Currency create(Currency currency) {
        if(findByName(currency.getName()).isPresent()) {
            throw new IllegalArgumentException("Object already exists");
        }
        return currencyDao.save(currency);
    }

    public Stream<Currency> findAll() {
        return currencyDao.findAll().stream();
    }

    public void delete(Long id) {
        if(!findOne(id).isPresent()) {
            throw new NullPointerException("Object doesn't exist");
        }
        currencyDao.delete(id);
    }

    public Currency update(Currency currency) {
        if(!findOne(currency.getId()).isPresent()) {
            throw new NullPointerException("Object doesn't exist");
        }
        return currencyDao.save(currency);
    }

    public Optional<Currency> findByName(String name) {
        return currencyDao.findByName(name);
    }
}
