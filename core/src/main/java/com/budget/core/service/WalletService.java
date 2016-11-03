package com.budget.core.service;

import com.budget.core.dao.WalletDao;
import com.budget.core.entity.Wallet;
import com.budget.core.exception.ObjectAlreadyExists;
import com.budget.core.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class WalletService {

    private final WalletDao walletDao;

    @Autowired
    public WalletService(WalletDao walletDao) {
        this.walletDao = walletDao;
    }

    public Optional<Wallet> findOne(long id) {
        return walletDao.findOne(id);
    }

    public Stream<Wallet> findByNameAndUserIdAndCurrencyId(String name, Long userId, Long currencyId) {
        return walletDao.findByNameAndUserIdAndCurrencyId(name, userId, currencyId);
    }

    public Stream<Wallet> findAll() {
        return walletDao.findAll();
    }

    public Wallet create(Wallet wallet) {
        if (findByNameAndUserIdAndCurrencyId(wallet.getName(), wallet.getUser().getId(), wallet.getCurrency().getId())
                .findAny().isPresent()) {
            throw new ObjectAlreadyExists("Object Wallet already exists.");
        }
        return walletDao.save(wallet);
    }

    public Wallet update(Wallet wallet) {
        if(findByNameAndUserIdAndCurrencyId(wallet.getName(), wallet.getUser().getId(), wallet.getCurrency().getId())
                .findAny().isPresent()) {
            throw new IllegalArgumentException("Exactly same Object Wallet already exists.");
        } else if (!findOne(wallet.getId()).isPresent()) {
            throw new ObjectNotFoundException("Object Wallet not found.");
        }
        return walletDao.save(wallet);
    }

    public void delete(Wallet wallet) {
        if(!findOne(wallet.getId()).isPresent()) {
            throw new NullPointerException("Object doesn't exist");
        }
        walletDao.delete(wallet.getId());
    }
}
