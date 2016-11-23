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

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.*;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("h2")
class UserCurrencyServiceImplTest {
    @Autowired
    private UserCurrencyServiceImplImpl userCurrencyServiceImpl;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserServiceImpl userService;

    private static UserCurrency localUserCurrencyOne, localUserCurrencyTwo;
    private static Currency localCurrencyOne, localCurrencyTwo;
    private static User localUserOne;

    private static final Logger logger = Logger.getLogger(UserCurrencyServiceImplTest.class);

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        logger.info("==========\nStart of beforeEach() method\n==========");
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
            localUserCurrencyOne.setUserId(localUserOne.getId());
            localUserCurrencyOne.setValue(26.5f);
            localUserCurrencyOne.setCurrencyId(localCurrencyOne.getId());
            localUserCurrencyOne = userCurrencyServiceImpl.create(localUserCurrencyOne);

            localUserCurrencyTwo = new UserCurrency();
            localUserCurrencyTwo.setUserId(localUserOne.getId());
            localUserCurrencyTwo.setValue(13.9f);
            localUserCurrencyTwo.setCurrencyId(localCurrencyTwo.getId());
            localUserCurrencyTwo = userCurrencyServiceImpl.create(localUserCurrencyTwo);
        }
        AssertSqlCount.reset();
        logger.info("==========\nEnd of beforeEach() method BEFOREEACH\n==========");
    }

    @AfterEach
    public void afterEach() {
        logger.info("==========\nStart of afterEach() method\n==========");
        Stream<UserCurrency> userCurrencyForDeletion = userCurrencyServiceImpl.findByUserId(localUserOne.getId());
        userCurrencyForDeletion.map(UserCurrency::getId).forEach(userCurrencyServiceImpl::delete);

        Stream<Currency> currencyForDeletion = currencyService.findAll();
        currencyForDeletion.map(Currency::getId).forEach(currencyService::delete);

        userService.delete(localUserOne.getId());
        logger.info("==========\nEnd of afterEach() method\n==========");
    }

    @Test
    public void findOne_ifExists() throws Exception {
        Optional<UserCurrency> actualUserCurrency = userCurrencyServiceImpl.findOne(localUserCurrencyOne.getId());

        assertAll(
                () -> assertTrue(actualUserCurrency.isPresent(), "Actual UserCurrency must exists, but it's not that"),
                () -> assertEquals(localUserCurrencyOne, actualUserCurrency.get()),
                () -> assertEquals(localCurrencyOne, currencyService.findOne(actualUserCurrency.get().getCurrencyId()).get()),
                () -> assertEquals(localUserOne, userService.findOne(actualUserCurrency.get().getUserId()).get())
        );
        assertSelectCount(3);
    }

    @Test
    public void findOne_notExists() throws Exception {
        assertFalse(userCurrencyServiceImpl.findOne(Long.MAX_VALUE).isPresent(), "Actual UserCurrency must be empty, but it's not true.");
        assertSelectCount(1);
    }

    @Test
    public void findAllByUserId_ifExists() throws Exception {
        Stream<UserCurrency> userCurrencies = userCurrencyServiceImpl.findByUserId(localUserOne.getId());
        assertEquals(Arrays.asList(localUserCurrencyOne, localUserCurrencyTwo), userCurrencies.collect(toList()),
                        "Expected and actual Lists of UserCurrencyOne and UserCurrencyTwo are not equal.");
        assertSelectCount(1);
    }

    @Test
    public void findAllByUserId_notExists() throws Exception {
        Stream<UserCurrency> userCurrencies = userCurrencyServiceImpl.findByUserId(new Long(Integer.MAX_VALUE));
        assertEquals(0, userCurrencies.count());
        assertSelectCount(1);
    }

    @Test
    public void createUserCurrency_notExists() throws Exception {
        Currency currencyForCreation = new Currency("ZZZ", 5556L);
        currencyService.create(currencyForCreation);
        UserCurrency userCurrencyForCreation = new UserCurrency();
        userCurrencyForCreation.setUserId(localUserOne.getId());
        userCurrencyForCreation.setCurrencyId(currencyForCreation.getId());
        userCurrencyForCreation.setValue(666.6f);

        UserCurrency actualUserCurrency = userCurrencyServiceImpl.create(userCurrencyForCreation);
        assertAll(
                () -> assertNotNull(actualUserCurrency, "Result of create() operation is empty."),
                () -> assertEquals(userCurrencyForCreation, actualUserCurrency,
                        "Expected UserCurrency and created one are not equal."),
                () -> assertEquals(currencyForCreation, currencyService.findOne(userCurrencyForCreation.getCurrencyId()).get(),
                        "Expected Currency and created one are not equal."),
                () -> assertEquals(localUserOne, userService.findOne(userCurrencyForCreation.getUserId()).get(),
                        "Expected User and user from create() result are not equal.")
        );

        assertSelectCount(4);
        assertInsertCount(2);
    }

    @Test
    public void createUserCurrency_ifExists() throws Exception {
        UserCurrency userCurrencyForCreation = new UserCurrency();
        userCurrencyForCreation.setUserId(localUserCurrencyOne.getUserId());
        userCurrencyForCreation.setValue(localUserCurrencyOne.getValue());
        userCurrencyForCreation.setCurrencyId(localUserCurrencyOne.getCurrencyId());

        Throwable localException = Assertions.expectThrows(IllegalArgumentException.class,
                () -> userCurrencyServiceImpl.create(userCurrencyForCreation));
        assertEquals(String.format("Object already exists with userId=%s, currencyId=%s",
                localUserCurrencyOne.getUserId(), localUserCurrencyOne.getCurrencyId()),
                localException.getMessage());

        assertInsertCount(0);
        assertSelectCount(1);
    }

    @Test
    public void updateUserCurrencyWithCurrency_ifExistsNotSame() throws Exception {
        long userCurrencyCountBefore = userCurrencyServiceImpl.findByUserId(localUserOne.getId()).count();
        Currency currencyForUpdate = new Currency("ABC", 6565L);
        currencyService.create(currencyForUpdate);
        localUserCurrencyTwo.setCurrencyId(currencyForUpdate.getId());
        UserCurrency userCurrencyActual =
                userCurrencyServiceImpl.updateCurrencyId(localUserCurrencyTwo.getId(), currencyForUpdate.getId());
        long userCurrencyCountAfter = userCurrencyServiceImpl.findByUserId(localUserOne.getId()).count();

        assertAll(
                () -> assertNotNull(userCurrencyActual, "Result of update() byCurrency method is empty."),
                () -> assertEquals(localUserCurrencyTwo, userCurrencyActual,
                        "Expected UserCurrency and updated one are not equal."),
                () -> assertEquals(localUserOne, userService.findOne(userCurrencyActual.getUserId()).get(),
                        "Expected User and User from update result are not equal."),
                () -> assertEquals(currencyForUpdate, currencyService.findOne(userCurrencyActual.getCurrencyId()).get(),
                        "Expected Currency and Currency from update result are not equal."),
                () -> assertEquals(userCurrencyCountBefore, userCurrencyCountAfter,
                        "Expected UserCurrencyCount and count after update one are not equal.")
        );

        assertSelectCount(6);
        assertInsertCount(1);
        assertUpdateCount(1);
    }

    @Test
    public void updateUserCurrencyWithCurrency_ifExistsSame() throws Exception {
        long userCurrencyCountBefore = userCurrencyServiceImpl.findByUserId(localUserOne.getId()).count();
        Currency currencyForUpdate = localCurrencyTwo;
        localUserCurrencyTwo.setCurrencyId(currencyForUpdate.getId());
        Throwable localException = Assertions.expectThrows(IllegalArgumentException.class,
                () -> userCurrencyServiceImpl.updateCurrencyId(localUserCurrencyTwo.getId(), currencyForUpdate.getId()));
        long userCurrencyCountAfter = userCurrencyServiceImpl.findByUserId(localUserOne.getId()).count();

        assertAll(
                () -> assertEquals("Exact same object already exists. Nothing to update.", localException.getMessage(),
                        "Expected throw message and really thrown are not equal."),
                () -> assertEquals(userCurrencyCountBefore, userCurrencyCountAfter,
                        "Expected UserCurrencyCount and count after update one are not equal.")
        );

        assertSelectCount(3);
        assertInsertCount(0);
        assertUpdateCount(0);
    }

    @Test
    public void updateUserCurrencyWithCurrency_notExists() throws Exception {
        long userCurrencyCountBefore = userCurrencyServiceImpl.findByUserId(localUserOne.getId()).count();
        Throwable localException = Assertions.expectThrows(NullPointerException.class,
                () -> userCurrencyServiceImpl.updateCurrencyId(Long.MAX_VALUE, localCurrencyTwo.getId()));
        long userCurrencyCountAfter = userCurrencyServiceImpl.findByUserId(localUserOne.getId()).count();

        assertAll(
                () -> assertEquals(userCurrencyCountBefore, userCurrencyCountAfter,
                        "Expected UserCurrencyCount and count after update one are not equal."),
                () -> assertEquals("UserCurrency doesn't exist with id: " + Long.MAX_VALUE, localException.getMessage(),
                        "Expected throw message and really thrown one are not equal.")
        );

        assertSelectCount(3);
        assertInsertCount(0);
        assertUpdateCount(0);
    }

    @Test
    public void updateUserCurrencyWithValue_ifExistsNotSame() throws Exception {
        long userCurrencyCountBefore = userCurrencyServiceImpl.findByUserId(localUserOne.getId()).count();
        localUserCurrencyTwo.setValue(localUserCurrencyTwo.getValue() * 1.16f);
        UserCurrency userCurrencyActual = userCurrencyServiceImpl.updateValue(localUserCurrencyTwo);
        long userCurrencyCountAfter = userCurrencyServiceImpl.findByUserId(localUserOne.getId()).count();

        assertAll(
                () -> assertNotNull(userCurrencyActual, "Result of update() by Value method is empty."),
                () -> assertEquals(localUserCurrencyTwo, userCurrencyActual,
                        "Expected UserCurrency and updated one are not equal."),
                () -> assertEquals(localUserOne, userService.findOne(userCurrencyActual.getUserId()).get(),
                        "Expected User and User from update result are not equal."),
                () -> assertEquals(userCurrencyCountBefore, userCurrencyCountAfter,
                        "Expected UserCurrencyCount and count after update one are not equal.")
        );
        assertSelectCount(4);
        assertInsertCount(0);
        assertUpdateCount(1);
    }

    @Test
    public void updateUserCurrencyWithValue_ifExistsSame() throws Exception {
        long userCurrencyCountBefore = userCurrencyServiceImpl.findByUserId(localUserOne.getId()).count();
        localUserCurrencyTwo.setValue(localUserCurrencyTwo.getValue());
        Throwable localException = Assertions.expectThrows(IllegalArgumentException.class,
                () -> userCurrencyServiceImpl.updateValue(localUserCurrencyTwo));
        long userCurrencyCountAfter = userCurrencyServiceImpl.findByUserId(localUserOne.getId()).count();

        assertAll(
                () -> assertEquals("Exact same object already exists. Nothing to update.", localException.getMessage(),
                        "Expected throw message and really thrown are not equal."),
                () -> assertEquals(userCurrencyCountBefore, userCurrencyCountAfter,
                        "Expected UserCurrencyCount and count after update one are not equal.")
        );

        assertSelectCount(3);
        assertInsertCount(0);
        assertUpdateCount(0);
    }

    @Test
    public void updateUserCurrencyWithValue_notExists() throws Exception {
        long userCurrencyCountBefore = userCurrencyServiceImpl.findByUserId(localUserOne.getId()).count();
        Throwable localException = Assertions.expectThrows(NullPointerException.class,
                () -> userCurrencyServiceImpl.updateCurrencyId(Long.MAX_VALUE, localCurrencyTwo.getId()));
        long userCurrencyCountAfter = userCurrencyServiceImpl.findByUserId(localUserOne.getId()).count();

        assertAll(
                () -> assertEquals(userCurrencyCountBefore, userCurrencyCountAfter,
                        "Expected UserCurrencyCount and count after update one are not equal."),
                () -> assertEquals("UserCurrency doesn't exist with id: " + Long.MAX_VALUE, localException.getMessage(),
                        "Expected throw message and really thrown one are not equal.")
        );

        assertSelectCount(3);
        assertInsertCount(0);
        assertUpdateCount(0);
    }

    @Test
    public void deleteUserCurrency_ifExists() throws Exception {
        Long localId = localUserCurrencyTwo.getId();
        long userCurrencyCountBefore = userCurrencyServiceImpl.findByUserId(localUserOne.getId()).count();
        userCurrencyServiceImpl.delete(localId);
        long userCurrencyCountAfter = userCurrencyServiceImpl.findByUserId(localUserOne.getId()).count();

        Optional<UserCurrency> deletedUserCurrency = userCurrencyServiceImpl.findOne(localId);
        assertFalse(deletedUserCurrency.isPresent(), "Deleted object exists after deletion method.");

        Optional<Currency> expectedCurrency = currencyService.findOne(localCurrencyTwo.getId());
        assertAll(
                () -> assertTrue(expectedCurrency.isPresent(), "Currency disappeared after UserCurrency deletion."),
                () -> assertEquals(localCurrencyTwo, expectedCurrency.get(),
                        "Expected Currency and remained after UserCurrency deletion are not equal.")
        );

        Optional<User> expectedUser = userService.findOne(localUserOne.getId());
        assertAll(
                () -> assertTrue(expectedUser.isPresent(), "User disappeared after UserCurrency deletion method."),
                () -> assertEquals(localUserOne, expectedUser.get(),
                        "Expected User and remained after UserCurrency deletion are not equal")
        );

        assertEquals(userCurrencyCountBefore - 1, userCurrencyCountAfter,
                "Before and After UserCurrency counts are not equal after UserCurrency deletion method.");
        assertDeleteCount(1);
        assertSelectCount(6);
    }

    @Test
    public void deleteUserCurrency_notExists() throws Exception {
        long userCurrencyCountBefore = userCurrencyServiceImpl.findByUserId(localUserOne.getId()).count();
        Throwable localException = Assertions.expectThrows(NullPointerException.class,
                () -> userCurrencyServiceImpl.delete(Long.MAX_VALUE));
        assertEquals("UserCurrency doesn't exist with id: " + Long.MAX_VALUE, localException.getMessage());
        long userCurrencyCountAfter = userCurrencyServiceImpl.findByUserId(localUserOne.getId()).count();

        assertEquals(userCurrencyCountBefore, userCurrencyCountAfter,
                "Before and After UserCurrency counts are not equal after UserCurrency deletion method.");
        assertSelectCount(3);
        assertDeleteCount(0);
    }
}