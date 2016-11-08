package com.budget.core.service;

import com.budget.core.dao.UserCurrencyDao;
import com.budget.core.entity.Currency;
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
public class UserCurrencyService extends AbstractService<UserCurrency> {

    private final UserCurrencyDao userCurrencyDao;

    @Autowired
    public UserCurrencyService(UserCurrencyDao userCurrencyDao) {
        this.userCurrencyDao = userCurrencyDao;
    }

    @Override
    CrudRepository<UserCurrency, Long> getDao() {
        return userCurrencyDao;
    }

    public Stream<UserCurrency> findAllByUserId(Long userId) {
        return userCurrencyDao.findAllByUserId(userId);
    }

    @Transactional
    public UserCurrency create(UserCurrency userCurrency) {
        checkExistenceByUserCurrency(userCurrency);
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
    public UserCurrency updateCurrency(@NonNull Long userCurrencyId, @NonNull Currency currency) {
        Optional<UserCurrency> userCurrencyForUpdate = findOne(userCurrencyId);
        if (!userCurrencyForUpdate.isPresent()) {
            throw new NullPointerException("UserCurrency doesn't exist with id: " + userCurrencyId);
        } else if (userCurrencyForUpdate.get().getCurrency().equals(currency)) {
            throw new IllegalArgumentException("Exact same object already exists. Nothing to update.");
        }

        UserCurrency updatableCurrency = userCurrencyForUpdate.get();
        updatableCurrency.setCurrency(currency);
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

    private void checkExistenceByUserCurrency(UserCurrency userCurrency) {
        if(userCurrencyDao.findByUserIdAndCurrencyId(userCurrency.getUser().getId(), userCurrency.getCurrency().getId()).isPresent()) {
            String exMsg = String.format("Object already exists with user_name=%s, currency_name=$s",
                    userCurrency.getUser().getLogin(), userCurrency.getCurrency().getName());
            throw new IllegalArgumentException(exMsg);
        }
    }
}
