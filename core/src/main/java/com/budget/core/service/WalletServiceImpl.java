package com.budget.core.service;

import com.budget.core.dao.WalletDao;
import com.budget.core.entity.Wallet;
import com.budget.core.exception.ObjectAlreadyExists;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class WalletServiceImpl extends AbstractServiceImpl<Wallet> {
    private final WalletDao walletDao;

    @Autowired
    public WalletServiceImpl(WalletDao walletDao) {
        this.walletDao = walletDao;
    }

    @Override
    CrudRepository<Wallet, Long> getDao() {
        return walletDao;
    }

    @Transactional
    public Wallet create(Wallet wallet) {
        if (findByNameAndUserCurrencyId(wallet.getName(), wallet.getUserCurrencyId()).findAny().isPresent()) {
            throw new ObjectAlreadyExists("Object Wallet already exists.");
        }
        return walletDao.save(wallet);
    }

    public Stream<Wallet> findByNameAndUserCurrencyId(String name, Long userCurrencyId) {
        return walletDao.findByNameAndUserCurrencyId(name, userCurrencyId);
    }

    public Stream<Wallet> findAll() {
        return walletDao.findAll().stream();
    }

    @Transactional
    public Wallet updateUserCurrencyId(@NonNull Long walletId, @NonNull Long userCurrencyId) {
        Optional<Wallet> walletForUpdate = findOne(walletId);
        if (!walletForUpdate.isPresent()) {
            throw new NullPointerException("Wallet doesn't exist with id: " + walletId);
        } else if (walletForUpdate.get().getUserCurrencyId() == userCurrencyId) {
            throw new IllegalArgumentException("Exactly same Object Wallet already exists.");
        }

        Wallet updatableWallet = walletForUpdate.get();
        updatableWallet.setUserCurrencyId(userCurrencyId);
        return walletDao.save(updatableWallet);
    }

    @Transactional
    public Wallet updateName(@NonNull Long walletId, @NonNull String walletName) {
        Optional<Wallet> walletForUpdate = findOne(walletId);
        if (!walletForUpdate.isPresent()) {
            throw new NullPointerException("Wallet doesn't exist with id: " + walletId);
        } else if (walletForUpdate.get().getName().equals(walletName)) {
            throw new IllegalArgumentException("Exactly same Object Wallet already exists.");
        }

        Wallet updatableWallet = walletForUpdate.get();
        updatableWallet.setName(walletName);
        return walletDao.save(updatableWallet);
    }
}
