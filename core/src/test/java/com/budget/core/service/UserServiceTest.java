package com.budget.core.service;

import com.budget.core.entity.User;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount;
import org.junit.Test;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("h2")
@TestExecutionListeners({
        TransactionalTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class
})
@Transactional
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

    @Test
    public void findByLogin() throws Exception {

    }

    @Test
    public void findOne() throws Exception {

    }

    @Test
    public void save() throws Exception {

    }

    @Test
    public void delete() throws Exception {

    }

    @Test
    public void create() throws Exception {

    }

}