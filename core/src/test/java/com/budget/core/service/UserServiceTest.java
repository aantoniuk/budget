package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.Category;
import com.budget.core.entity.Currency;
import com.budget.core.entity.User;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.assertSelectCount;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private CategoryService categoryService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    public void init() {
        user = new User(LOGIN, PASSWORD);

        AssertSqlCount.reset();
    }

    @AfterEach
    public void afterEach() {
        Optional<User> userOptional = userService.findOne(user.getId());
        if(userOptional.isPresent()) {
            userService.delete(user.getId());
        }
        AssertSqlCount.reset();
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

        assertSelectCount(1);
        assertSelectCount(1);

        Optional<User> newUserOptional = userService.findOne(newUser.getId());

        assertAll(
                () -> assertTrue(newUserOptional.isPresent()),
                () -> assertEquals(user, newUserOptional.get())
        );

        userService.delete(newUser.getId());
    }

    @Test
    public void create_withCategoryAndCurrence() throws Exception {
        Category category = new Category("category", OperationType.CREDIT);
        categoryService.create(category);
        Category subCategory = new Category("subCategory", OperationType.CREDIT);

        Currency currency = new Currency("currency", 1L);
        currencyService.create(currency);

        String newLogin = LOGIN + this.hashCode();
        User newUser = new User(newLogin, PASSWORD);
        userService.create(newUser);

        assertSelectCount(1);
        assertSelectCount(1);

        Optional<User> newUserOptional = userService.findOne(newUser.getId());

        assertAll(
                () -> assertTrue(newUserOptional.isPresent()),
                () -> assertEquals(user, newUserOptional.get())
        );

        userService.delete(newUser.getId());
    }

    @Test
    public void update() throws Exception {

    }

    @Test
    public void delete() throws Exception {

    }
}