package com.budget.core.service;

import com.budget.core.dao.UserCurrencyDao;
import com.budget.core.entity.UserCurrency;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserCurrencyServiceImpl extends AbstractServiceImpl<UserCurrency> implements UserCurrencyService {

    private final UserCurrencyDao userCurrencyDao;

    @Autowired
    public UserCurrencyServiceImpl(UserCurrencyDao userCurrencyDao) {
        this.userCurrencyDao = userCurrencyDao;
    }

    @Override
    CrudRepository<UserCurrency, Long> getDao() {
        return userCurrencyDao;
    }

    public Stream<UserCurrency> findByUserId(Long userId) {
        return userCurrencyDao.findByUserId(userId);
    }

    @Transactional
    public UserCurrency create(UserCurrency userCurrency) throws IllegalArgumentException {
        checkExistence(userCurrency);
        return userCurrencyDao.save(userCurrency);
    }

    public UserCurrency save(UserCurrency userCurrency) {
        return userCurrencyDao.save(userCurrency);
    }

    @Transactional
    public void delete(Long userCurrencyId) {
        Optional<UserCurrency> userCurrencyForDeletion = findOne(userCurrencyId);
        if (!userCurrencyForDeletion.isPresent()) {
            throw new NullPointerException("UserCurrency doesn't exist with id: " + userCurrencyId);
        }
        userCurrencyDao.delete(userCurrencyId);
    }

    @Transactional
    public UserCurrency updateCurrencyId(@NonNull Long userCurrencyId, @NonNull Long currencyId) {
        Optional<UserCurrency> userCurrencyForUpdate = findOne(userCurrencyId);
        if (!userCurrencyForUpdate.isPresent()) {
            throw new NullPointerException("UserCurrency doesn't exist with id: " + userCurrencyId);
        } else if (userCurrencyForUpdate.get().getCurrencyId() == currencyId) {
            throw new IllegalArgumentException("Exact same object already exists. Nothing to update.");
        }
        UserCurrency updatableCurrency = userCurrencyForUpdate.get();
        updatableCurrency.setCurrencyId(currencyId);
        return userCurrencyDao.save(updatableCurrency);
    }

    @Transactional
    public UserCurrency updateValue(@NotNull UserCurrency userCurrency) {
        Optional<UserCurrency> userCurrencyForUpdate = findOne(userCurrency.getId());
        if (!userCurrencyForUpdate.isPresent()) {
            throw new NullPointerException("UserCurrency doesn't exist with id: " + userCurrency.getId());
        } else if (userCurrencyForUpdate.get().equals(userCurrency)) {
            throw new IllegalArgumentException("Exact same object already exists. Nothing to update.");
        }
        return userCurrencyDao.save(userCurrency);
    }

    private void checkExistence(UserCurrency userCurrency) {
        if (userCurrencyDao.findByUserIdAndCurrencyId(userCurrency.getUserId(), userCurrency.getCurrencyId()).isPresent()) {
            String exMsg = String.format("Object already exists with userId=%s, currencyId=%s",
                    userCurrency.getUserId(), userCurrency.getCurrencyId());
            throw new IllegalArgumentException(exMsg);
        }
    }
}
