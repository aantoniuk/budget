package com.budget.core.service;

import com.budget.core.dao.WalletDao;
import com.budget.core.entity.Wallet;
import com.budget.core.exception.ObjectAlreadyExists;
import com.budget.core.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

    private final WalletDao walletDao;

    @Autowired
    public WalletService(WalletDao walletDao) {
        this.walletDao = walletDao;
    }

    public Optional<Wallet> findOne(long id) {
        return Optional.ofNullable(walletDao.findOne(id));
    }

    public Optional<Wallet> findByNameAndUserCurrencyId(String name, long userCurrencyId) {
        return Optional.ofNullable(walletDao.findByNameAndUserCurrencyId(name, userCurrencyId));
    }

    public List<Wallet> findAll() {
        return walletDao.findAll();
    }

    public Wallet create(Wallet wallet) {
        if (findByNameAndUserCurrencyId(wallet.getName(), wallet.getUserCurrency().getId()).isPresent()) {
            throw new ObjectAlreadyExists("");
        }
        return walletDao.save(wallet);
    }

    public Wallet update(Wallet wallet) {
        if(!findByNameAndUserCurrencyId(wallet.getName(), wallet.getUserCurrency().getId()).isPresent()) {
            throw new ObjectNotFoundException("");
        }
        return walletDao.save(wallet);
    }
}
