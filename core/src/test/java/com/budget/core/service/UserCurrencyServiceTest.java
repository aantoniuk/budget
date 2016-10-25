package com.budget.core.service;

import com.budget.core.entity.Currency;
import com.budget.core.entity.User;
import com.budget.core.entity.UserCurrency;
import com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.*;
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

    private static final Logger logger = Logger.getLogger(UserCurrencyServiceTest.class);

    @BeforeEach
    public void init(TestInfo testInfo) {
        logger.info("==========\nStart of init() method\n==========");
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
        logger.info("==========\nEnd of init() method\n==========");
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

    @Test
    public void createUserCurrency_notExists() throws Exception {
        Currency currencyForCreation = new Currency("ZZZ", 5556L);
        currencyService.create(currencyForCreation);
        UserCurrency userCurrencyForCreation = new UserCurrency();
        userCurrencyForCreation.setUser(localUserOne);
        userCurrencyForCreation.setCurrency(currencyForCreation);

        UserCurrency actualUserCurrency = userCurrencyService.create(userCurrencyForCreation);
        assertAll(
                () -> assertNotNull(actualUserCurrency),
                () -> assertEquals(userCurrencyForCreation, actualUserCurrency),
                () -> assertEquals(currencyForCreation, userCurrencyForCreation.getCurrency()),
                () -> assertEquals(localUserOne, userCurrencyForCreation.getUser())
        );

        assertSelectCount(2);
        assertInsertCount(2);
    }

    @Test
    public void createUserCurrency_ifExists() throws Exception {
        UserCurrency userCurrencyForCreation = new UserCurrency();
        userCurrencyForCreation.setUser(localUserCurrencyOne.getUser());
        userCurrencyForCreation.setCurrency(localUserCurrencyOne.getCurrency());

        Throwable localException = Assertions.expectThrows(IllegalArgumentException.class,
                () -> userCurrencyService.create(userCurrencyForCreation));
        assertEquals(String.format("Object already exists with user_name=%s, currency_name=$s",
                localUserCurrencyOne.getUser().getLogin(), localUserCurrencyOne.getCurrency().getName()),
                localException.getMessage());

        assertInsertCount(0);
        assertSelectCount(1);
    }

    @Test
    public void updateUserCurrency_ifExistsNotSame() throws Exception {
        long userCurrencyCountBefore = userCurrencyService.findAll().count();
        Currency currencyForUpdate = new Currency("ABC", 6565L);
        currencyService.create(currencyForUpdate);
        localUserCurrencyTwo.setCurrency(currencyForUpdate);
        UserCurrency userCurrencyActual = userCurrencyService.update(localUserCurrencyTwo);
        long userCurrencyCountAfter = userCurrencyService.findAll().count();

        assertAll(
                () -> assertNotNull(userCurrencyActual),
                () -> assertEquals(localUserCurrencyTwo, userCurrencyActual),
                () -> assertEquals(localUserOne, userCurrencyActual.getUser()),
                () -> assertEquals(currencyForUpdate, userCurrencyActual.getCurrency()),
                () -> assertEquals(userCurrencyCountBefore, userCurrencyCountAfter)
        );

        assertSelectCount(3);
        assertInsertCount(1);
        assertUpdateCount(0);
    }

    @Test
    public void updateUserCurrency_ifExistsSame() throws Exception {
        long userCurrencyCountBefore = userCurrencyService.findAll().count();
        UserCurrency userCurrencyForUpdate = new UserCurrency();
        userCurrencyForUpdate.setCurrency(localCurrencyTwo);
        userCurrencyForUpdate.setUser(localUserOne);
        UserCurrency userCurrencyActual = userCurrencyService.update(localUserCurrencyTwo);
        long userCurrencyCountAfter = userCurrencyService.findAll().count();

        assertAll(
                () -> assertNotNull(userCurrencyActual),
                () -> assertEquals(localUserCurrencyTwo, userCurrencyActual),
                () -> assertEquals(localUserOne, userCurrencyActual.getUser()),
                () -> assertEquals(localCurrencyTwo, userCurrencyActual.getCurrency()),
                () -> assertEquals(userCurrencyCountBefore, userCurrencyCountAfter)
        );

        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
    }

    @Test
    public void updateUserCurrency_notExists() throws Exception {
        long userCurrencyCountBefore = userCurrencyService.findAll().count();

        Currency currencyForUpdate = new Currency("DEF", 6L);
        UserCurrency userCurrencyForUpdate = new UserCurrency();
        userCurrencyForUpdate.setUser(localUserOne);
        userCurrencyForUpdate.setCurrency(currencyForUpdate);

        Throwable localException = Assertions.expectThrows(NullPointerException.class,
                () -> userCurrencyService.update(userCurrencyForUpdate));
        long userCurrencyCountAfter = userCurrencyService.findAll().count();

        assertAll(
                () -> assertEquals(userCurrencyCountBefore, userCurrencyCountAfter),
                () -> assertEquals("Object doesn't exist", localException.getMessage())
        );

        assertSelectCount(3);
        assertInsertCount(0);
        assertUpdateCount(0);
    }

    @Test
    public void deleteUserCurrency_ifExists() throws Exception {
        Long localId = localUserCurrencyTwo.getId();
        long userCurrencyCountBefore = userCurrencyService.findAll().count();
        userCurrencyService.delete(localId);
        long userCurrencyCountAfter = userCurrencyService.findAll().count();

        Optional<UserCurrency> deletedUserCurrency = userCurrencyService.findOne(localId);
        assertAll(
                () -> assertFalse(deletedUserCurrency.isPresent())
        );

        Optional<Currency> expectedCurrency = currencyService.findOne(localCurrencyTwo.getId());
        assertAll(
                () -> assertTrue(expectedCurrency.isPresent()),
                () -> assertEquals(localCurrencyTwo, expectedCurrency.get())
        );

        Optional<User> expectedUser = userService.findOne(localUserOne.getId());
        assertAll(
                () -> assertTrue(expectedUser.isPresent()),
                () -> assertEquals(localUserOne, expectedUser.get())
        );

        assertEquals(userCurrencyCountBefore, userCurrencyCountAfter);
        assertDeleteCount(0);
        assertSelectCount(2);
    }

    @Test
    public void deleteUserCurrency_notExists() throws Exception {
        long userCurrencyCountBefore = userCurrencyService.findAll().count();
        Throwable localException = Assertions.expectThrows(NullPointerException.class,
                () -> userCurrencyService.delete(Long.MAX_VALUE));
        assertEquals("Object doesn't exist", localException.getMessage());
        long userCurrencyCountAfter = userCurrencyService.findAll().count();

        assertEquals(userCurrencyCountBefore, userCurrencyCountAfter);
        assertSelectCount(3);
    }
}