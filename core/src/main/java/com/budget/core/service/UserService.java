package com.budget.core.service;

import com.budget.core.dao.UserDao;
import com.budget.core.entity.Category;
import com.budget.core.entity.User;
import com.budget.core.entity.UserCategory;
import com.budget.core.entity.UserCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

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

    public Optional<User> findOne(Long id) {
        return userDao.findOne(id);
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
        createUserCategories(savedUser);
        return savedUser;
    }

    @Transactional
    public User update(User user) {
        Optional<User> optionalUser = findOne(user.getId());
        if (!optionalUser.isPresent()) {
            //throw new IllegalArgumentException("User doesn't exist with id:" + user.getId() + " and login: " + user.getLogin());
            throw new NullPointerException("User doesn't exist with id:" + user.getId() + " and login: " + user.getLogin());
        }
        optionalUser.get().setLogin(user.getLogin());
        optionalUser.get().setEnable(user.isEnable());

        return userDao.save(optionalUser.get());
    }

    @Transactional
    public User delete(long id) {
        Optional<User> userOptional = findOne(id);
        if (!userOptional.isPresent()) {
            // throw new IllegalArgumentException("User doesn't exist with id:" + id);
            throw new NullPointerException("User doesn't exist with id:" + id);
        }

        userDao.delete(id);

        deleteUserCategories(userOptional.get());
        deleteUserCurrencies(userOptional.get());

        return userOptional.get();
    }

    private void deleteUserCategories(User user) {
        userCategoryService.findByParentId(user.getId(), null).
                forEach(item -> userCategoryService.delete(item.getId()));
    }

    private void deleteUserCurrencies(User user) {
        userCurrencyService.findByUser(user.getId()).forEach(item -> userCurrencyService.delete(item.getId()));
    }

    private void createUserCurrencies(User user) {
        currencyService.findAll().forEach(item -> {
            UserCurrency userCurrency = new UserCurrency();
            userCurrency.setUser(user);
            userCurrency.setCurrency(item);

            userCurrencyService.save(userCurrency);
        });
    }

    private void createUserCategories(User user) {
        categoryService.findByParentId(null).
                forEach(item -> createUserCategory(user, item, null));
    }

    private void createUserCategory(User user, Category category, UserCategory parentUserCategory) {
        UserCategory userCategory = new UserCategory(category.getName(), category.getType(), user.getId());
        userCategory.setParentId(parentUserCategory.getId());

        userCategoryService.create(userCategory);
        category.getChildren().forEach(item -> createUserCategory(user, item, userCategory));
    }
}

