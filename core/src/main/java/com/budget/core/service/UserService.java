package com.budget.core.service;

import com.budget.core.dao.UserDao;
import com.budget.core.entity.Category;
import com.budget.core.entity.User;
import com.budget.core.entity.UserCategory;
import com.budget.core.entity.UserCurrency;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService extends AbstractService<User> {

    private final UserDao userDao;
    private final CategoryService categoryService;
    private final UserCategoryService userCategoryService;
    private final CurrencyService currencyService;
    private final UserCurrencyService userCurrencyService;

    @Autowired
    public UserService(UserDao userDao, CategoryService categoryService, UserCategoryService userCategoryService,
                       CurrencyService currencyService, UserCurrencyService userCurrencyService) {
        this.userDao = userDao;
        this.categoryService = categoryService;
        this.userCategoryService = userCategoryService;
        this.currencyService = currencyService;
        this.userCurrencyService = userCurrencyService;
    }

    public Optional<User> findByLogin(String login) {
        return userDao.findByLogin(login);
    }

    @Transactional
    public User create(User user) {
        Optional userOptional = findByLogin(user.getLogin());
        if(userOptional.isPresent()) {
            throw new IllegalArgumentException("User already exists with login:" + user.getLogin());
            // throw new NullPointerException("User already exists with login:" + user.getLogin());
        }
        User savedUser = userDao.save(user);

        createUserCurrencies(savedUser);
        createUserCategories(savedUser.getId());

        return savedUser;
    }

    @Transactional
    public User updatePassword(@NonNull Long userId, @NonNull String password) {
        Optional<User> optionalUser = findOne(userId);
        if (!optionalUser.isPresent()) {
            //throw new IllegalArgumentException("User doesn't exist with id:" + user.getId() + " and login: " + user.getLogin());
            throw new NullPointerException("User doesn't exist with id:" + userId);
        }
        User updatableUser = optionalUser.get();
        updatableUser.setPassword(password);

        return userDao.save(updatableUser);
    }

    @Transactional
    public User updateEnable(@NonNull Long userId, @NonNull Boolean enable) {
        Optional<User> optionalUser = findOne(userId);
        if (!optionalUser.isPresent()) {
            //throw new IllegalArgumentException("User doesn't exist with id:" + user.getId() + " and login: " + user.getLogin());
            throw new NullPointerException("User doesn't exist with id:" + userId);
        }
        User updatableUser = optionalUser.get();
        updatableUser.setEnable(enable);

        return userDao.save(updatableUser);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Optional<User> userOptional = findOne(id);
        if (!userOptional.isPresent()) {
            // throw new IllegalArgumentException("User doesn't exist with id:" + id);
            throw new NullPointerException("User doesn't exist with id:" + id);
        }
        deleteUserCategories(id);
        deleteUserCurrencies(id);

        userDao.delete(id);
    }

    private void deleteUserCategories(Long userId) {
        userCategoryService.findByParentId(userId, null).
                forEach(item -> userCategoryService.delete(item.getId()));
    }

    private void deleteUserCurrencies(Long userId) {
        userCurrencyService.findAllByUserId(userId).forEach(item -> userCurrencyService.delete(item.getId()));
    }

    private void createUserCurrencies(User user) {
        currencyService.findAll().forEach(item -> {
            UserCurrency userCurrency = new UserCurrency();
            userCurrency.setUser(user);
            userCurrency.setCurrency(item);

            userCurrencyService.save(userCurrency);
        });
    }

    private void createUserCategories(Long userId) {
        categoryService.findByParentId(null).
                forEach(item -> createUserCategory(userId, item, null));
    }

    private void createUserCategory(Long userId, Category category, UserCategory parentUserCategory) {
        UserCategory userCategory = UserCategory.builder().name(category.getName()).type(category.getType()).userId(userId).build();
        if(parentUserCategory != null) {
            userCategory.setParentId(parentUserCategory.getId());
        }
        userCategoryService.create(userCategory);
        category.getChildren().forEach(item -> createUserCategory(userId, item, userCategory));
    }

    @Override
    CrudRepository<User, Long> getDao() {
        return userDao;
    }
}

