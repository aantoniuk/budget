package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.*;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.assertDeleteCount;
import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.assertInsertCount;
import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.assertSelectCount;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("h2")
@TestExecutionListeners({
        TransactionalTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class
})
public class UserServiceTest {

    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";

    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserCategoryService userCategoryService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserCurrencyService userCurrencyService;

    private User user;

    @BeforeEach
    public void init() {
        user = new User(LOGIN, PASSWORD);
        userService.create(user);

        AssertSqlCount.reset();
    }

    @AfterEach
    public void afterEach() {
        Optional<User> userOptional = userService.findOne(user.getId());
        if(userOptional.isPresent()) {
            userService.delete(user.getId());
        }
    }

    @Test
    public void findByLogin() throws Exception {
        Optional<User> userOptional = userService.findByLogin(user.getLogin());
        assertAll(
                () -> assertTrue(userOptional.isPresent()),
                () -> assertEquals(user, userOptional.get())
        );
        assertSelectCount(1);
    }

    @Test
    public void findOne() throws Exception {
        Optional<User> userOptional = userService.findOne(user.getId());
        assertAll(
                () -> assertTrue(userOptional.isPresent()),
                () -> assertEquals(user, userOptional.get())
        );
        assertSelectCount(1);
    }

    @Test
    public void create() throws Exception {
        String newLogin = LOGIN + this.hashCode();
        User newUser = new User(newLogin, PASSWORD);
        userService.create(newUser);

        assertSelectCount(3);
        assertInsertCount(1);

        Optional<User> newUserOptional = userService.findOne(newUser.getId());

        assertAll(
                () -> assertTrue(newUserOptional.isPresent()),
                () -> assertEquals(newUser, newUserOptional.get())
        );
        userService.delete(newUser.getId());
    }

    @Test
    public void create_duplicate() {
        User newUser = new User(LOGIN, PASSWORD + this.hashCode());

        Throwable exception = expectThrows(IllegalArgumentException.class, () -> userService.create(newUser));
        assertNotNull(exception);
    }

    @Test
    public void create_withCategoryAndCurrency() throws Exception {
        Category category = Category.builder().name("category").type(OperationType.CREDIT).build();
        categoryService.create(category);
        Category subCategory = Category.builder().name("subCategory").parentId(category.getId()).build();
        categoryService.create(subCategory);

        Currency currency = new Currency("USD", 1L);
        currencyService.create(currency);

        AssertSqlCount.reset();

        String newLogin = LOGIN + this.hashCode();
        User newUser = new User(newLogin, PASSWORD);
        userService.create(newUser);

        assertSelectCount(7);
        assertInsertCount(4);

        Optional<User> newUserOptional = userService.findOne(newUser.getId());

        assertAll(
                () -> assertTrue(newUserOptional.isPresent()),
                () -> assertEquals(newUser, newUserOptional.get())
        );

        // check root categories
        List<UserCategory> userCategories = userCategoryService.findByParentId(newUser.getId(), null).collect(Collectors.toList());
        assertAll(
                () -> assertNotNull(userCategories),
                () -> assertEquals(1, userCategories.size()),
                () -> assertEquals(category.getName(), userCategories.get(0).getName()),
                () -> assertEquals(category.getType(), userCategories.get(0).getType()),
                () -> assertEquals(category.getEnable(), userCategories.get(0).getEnable()),
                () -> assertEquals(category.getParentId(), userCategories.get(0).getParentId()),
                () -> assertEquals(newUser.getId(), userCategories.get(0).getUserId())
        );
        // check sub categories
        List<UserCategory> userSubCategories = userCategoryService.findByParentId(newUser.getId(),
                userCategories.get(0).getId()).collect(Collectors.toList());
        assertAll(
                () -> assertNotNull(userSubCategories),
                () -> assertEquals(1, userSubCategories.size()),
                () -> assertEquals(subCategory.getName(), userSubCategories.get(0).getName()),
                () -> assertEquals(subCategory.getType(), userSubCategories.get(0).getType()),
                () -> assertEquals(subCategory.getEnable(), userSubCategories.get(0).getEnable()),
                () -> assertEquals(userCategories.get(0).getId(), userSubCategories.get(0).getParentId()),
                () -> assertEquals(newUser.getId(), userSubCategories.get(0).getUserId())
        );

        List<UserCurrency> userCurrencies = userCurrencyService.findByUserId(newUser.getId()).collect(Collectors.toList());
        // check currency
        assertAll(
                () -> assertNotNull(userCurrencies),
                () -> assertEquals(1, userCurrencies.size()),
                // maybe fix it
                //() -> assertEquals(currency.getId(), userCurrencies.get(0).getCurrencyId()),
                // maybe fix it
                () -> assertEquals(newUser.getId(), userCurrencies.get(0).getUserId())
        );

        userService.delete(newUser.getId());
        categoryService.delete(category.getId());
        currencyService.delete(currency.getId());
    }

    @Test
    public void updatePassword() throws Exception {
        String newPassword = String.valueOf(this.hashCode());
        User updatedUser = userService.updatePassword(user.getId(), newPassword);
        assertEquals(newPassword, updatedUser.getPassword());
    }

    @Test
    public void updateEnable() throws Exception {
        User updatedUser = userService.updateEnable(user.getId(), false);
        assertEquals(false, updatedUser.getEnable());
    }

    @Test
    public void delete() throws Exception {
        userService.delete(user.getId());

        assertSelectCount(3);
        assertDeleteCount(1);

        Optional<User> userOpt = userService.findOne(user.getId());

        assertFalse(userOpt.isPresent());
    }

    @Test
    public void deleteWithCategoriesAndCurrencies() throws Exception {
        Category category = Category.builder().name("category").type(OperationType.CREDIT).build();
        categoryService.create(category);

        Currency currency = new Currency("USD", 1L);
        currencyService.create(currency);

        String newLogin = LOGIN + this.hashCode();
        User newUser = new User(newLogin, PASSWORD);
        userService.create(newUser);

        AssertSqlCount.reset();

        userService.delete(newUser.getId());

        assertSelectCount(4);
        assertDeleteCount(3);

        Optional<User> userOpt = userService.findOne(newUser.getId());
        Stream<UserCategory> categoryStream = userCategoryService.findByParentId(newUser.getId(), null);
        Stream<UserCurrency> currencyStream = userCurrencyService.findByUserId(newUser.getId());

        assertAll(
                () -> assertFalse(userOpt.isPresent()),
                () -> assertEquals(0, categoryStream.count()),
                () -> assertEquals(0, currencyStream.count())
        );

        categoryService.delete(category.getId());
        currencyService.delete(currency.getId());
    }
}