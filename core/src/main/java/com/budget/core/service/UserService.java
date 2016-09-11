package com.budget.core.service;

import com.budget.core.dao.UserDao;
import com.budget.core.entity.Category;
import com.budget.core.entity.User;
import com.budget.core.entity.UserCategory;
import com.budget.core.entity.UserCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return Optional.ofNullable(userDao.findByLogin(login));
    }

    @Transactional
    public User create(User user) {

        Optional userOpt = findByLogin(user.getLogin());
        if(userOpt.isPresent()) {
            throw new IllegalArgumentException("User already exists with login:" + user.getLogin());
        }

        User savedUser = userDao.save(user);

        createUserCurrencies(savedUser);
        createUserCategories(savedUser);

        return savedUser;
    }

    private void createUserCurrencies(User user) {
        currencyService.findAll().forEach(item->{
            UserCurrency userCurrency = new UserCurrency();
            userCurrency.setUser(user);
            userCurrency.setCurrency(item);

            userCurrencyService.save(userCurrency);
        });
    }

    private void createUserCategories(User user) {
        categoryService.findAll().stream().
                filter(item -> item.getParent() == null).
                forEach(item -> createUserCategory(user, item, null));
    }

    private void createUserCategory(User user, Category category, UserCategory parentUserCategory) {
        UserCategory userCategory = new UserCategory();
        userCategory.setUser(user);
        userCategory.setName(category.getName());
        userCategory.setParent(parentUserCategory);

        UserCategory savedUserCategory = userCategoryService.save(userCategory);
        category.getChildren().forEach(item -> createUserCategory(user, item, savedUserCategory));
    }
}
