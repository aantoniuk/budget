package com.budget.core.service;

import com.budget.core.entity.Currency;
import com.budget.core.entity.User;
import com.budget.core.entity.UserCurrency;
import com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.assertSelectCount;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class UserCurrencyServiceTest {
    @Autowired
    private UserCurrencyService userCurrencyService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserService userService;

    private static UserCurrency localUserCurrency;
    private static Currency localCurrency;
    private static User localUser;

    @BeforeEach
    public void init(TestInfo testInfo) {
        if (!testInfo.getTags().contains("dontadd")) {
            localUser = new User();
            localUser.setLogin("host");
            localUser.setEnable(true);
            localUser.setPassword("pwd");
            userService.create(localUser);
            localCurrency = new Currency("XXX", 761L);
            currencyService.create(localCurrency);
            localUserCurrency = new UserCurrency();
            localUserCurrency.setUser(localUser);
            localUserCurrency.setCurrency(localCurrency);
            localUserCurrency = userCurrencyService.create(localUserCurrency);
        }
        AssertSqlCount.reset();
    }

    @Test
    public void findOne_ifExists() throws Exception {
        Optional<UserCurrency> expectedUserCurrency = userCurrencyService.findOne(localUserCurrency.getId());

        assertAll(
                () -> assertTrue(expectedUserCurrency.isPresent()),
                () -> assertEquals(expectedUserCurrency.get(), localUserCurrency),
                () -> assertEquals(expectedUserCurrency.get().getCurrency(), localCurrency),
                () -> assertEquals(expectedUserCurrency.get().getUser(), localUser)
        );
        assertSelectCount(0);
    }

    @Test
    public void findOne_notExists() throws Exception {
        assertFalse(userCurrencyService.findOne(Long.MAX_VALUE).isPresent());
        assertSelectCount(1);
    }

    @Test
    @Tag("dontadd")
    public void findAll_notExists() throws Exception {
        Stream<UserCurrency> userCurrencies = userCurrencyService.findAll();
        assertAll(
                () -> assertNotNull(userCurrencies),
                () -> assertEquals(0, userCurrencies.count()));
        assertSelectCount(1);
    }
}