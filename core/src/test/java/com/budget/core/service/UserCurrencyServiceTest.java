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

import java.util.Optional;
import java.util.function.Supplier;
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

    private static UserCurrency localUserCurrencyOne, localUserCurrencyTwo;
    private static Currency localCurrencyOne, localCurrencyTwo;
    private static User localUserOne;

    @BeforeEach
    public void init(TestInfo testInfo) {
        if (!testInfo.getTags().contains("dontadd")) {
            localUserOne = new User();
            localUserOne.setLogin("host");
            localUserOne.setEnable(true);
            localUserOne.setPassword("pwd");

            userService.create(localUserOne);

            localCurrencyOne = new Currency("XXX", 761L);
            localCurrencyTwo = new Currency("YYY", 7894561L);

            currencyService.create(localCurrencyOne);
            currencyService.create(localCurrencyTwo);

            localUserCurrencyOne = new UserCurrency();
            localUserCurrencyOne.setUser(localUserOne);
            localUserCurrencyOne.setCurrency(localCurrencyOne);
            localUserCurrencyOne = userCurrencyService.create(localUserCurrencyOne);

            localUserCurrencyTwo = new UserCurrency();
            localUserCurrencyTwo.setUser(localUserOne);
            localUserCurrencyTwo.setCurrency(localCurrencyTwo);
            localUserCurrencyTwo = userCurrencyService.create(localUserCurrencyTwo);
        }
        AssertSqlCount.reset();
    }

    @Test
    public void findOne_ifExists() throws Exception {
        Optional<UserCurrency> expectedUserCurrency = userCurrencyService.findOne(localUserCurrencyOne.getId());

        assertAll(
                () -> assertTrue(expectedUserCurrency.isPresent()),
                () -> assertEquals(expectedUserCurrency.get(), localUserCurrencyOne),
                () -> assertEquals(expectedUserCurrency.get().getCurrency(), localCurrencyOne),
                () -> assertEquals(expectedUserCurrency.get().getUser(), localUserOne)
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

    @Test
    public void findUserCurrencyByUser_ifExists() throws Exception {
        Supplier<Stream<UserCurrency>> userCurrencySupplier = () -> userCurrencyService.findByUser(localUserOne.getId());
        assertAll(
                () -> assertNotNull(userCurrencySupplier),
                () -> assertEquals(2, userCurrencySupplier.get().count()),
                () -> assertEquals(localUserOne, userCurrencySupplier.get().findAny().get().getUser()),
                () -> assertEquals(localCurrencyOne,
                        userCurrencySupplier.get()
                                .filter(c -> c.getCurrency().equals(localCurrencyOne)).findAny().get().getCurrency()),
                () -> assertEquals(localCurrencyTwo,
                        userCurrencySupplier.get()
                                .filter(c -> c.getCurrency().equals(localCurrencyTwo)).findAny().get().getCurrency())
        );
        // Any usage of Stream in assertAll produces new Select to DB
        assertSelectCount(4);
    }

    @Test
    public void findUserCurrencyByUser_notExists() throws Exception {
        Stream<UserCurrency> userCurrencyStream = userCurrencyService.findByUser(Long.MAX_VALUE);
        assertAll( () -> assertEquals(0, userCurrencyStream.count()) );
        assertSelectCount(1);
    }

    @Test
    public void findUserCurrencyByCurrency_ifExists() throws Exception {
        Supplier<Stream<UserCurrency>> userCurrencySupplier = () -> userCurrencyService.findByCurrency(localCurrencyTwo.getId());
        assertAll(
                () -> assertNotNull(userCurrencySupplier),
                () -> assertEquals(1, userCurrencySupplier.get().count()),
                () -> assertEquals(localUserOne, userCurrencySupplier.get().findAny().get().getUser()),
                () -> assertEquals(localCurrencyTwo, userCurrencySupplier.get().findAny().get().getCurrency())
        );
        assertSelectCount(3);
    }

    @Test
    public void findUserCurrencyByCurrency_notExists() throws Exception {
        Stream<UserCurrency> userCurrencyStream = userCurrencyService.findByCurrency(Long.MAX_VALUE);
        assertAll( () -> assertEquals(0, userCurrencyStream.count()));
        assertSelectCount(1);
    }
}