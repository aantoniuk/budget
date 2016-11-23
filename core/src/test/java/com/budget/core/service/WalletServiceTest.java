package com.budget.core.service;

import com.budget.core.entity.Currency;
import com.budget.core.entity.User;
import com.budget.core.entity.UserCurrency;
import com.budget.core.entity.Wallet;
import com.budget.core.exception.ObjectAlreadyExists;
import com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("h2")
class WalletServiceTest {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private WalletServiceImpl walletService;
    @Autowired
    private UserCurrencyServiceImplImpl userCurrencyServiceImpl;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private CurrencyService currencyService;

    private static final Logger logger = Logger.getLogger(WalletServiceTest.class);

    private Wallet localWalletOne, localWalletTwo, localWalletThree;
    private UserCurrency localUserCurrencyOne, localUserCurrencyTwo;
    private User localUserOne;
    private Currency localCurrencyOne, localCurrencyTwo;

    @BeforeEach
    public void init(TestInfo testInfo) {
        logger.info("==========\nStart of beforeEach() method\n==========");
        if (!testInfo.getTags().contains("dontadd")) {
            localUserOne = new User();
            localUserOne.setLogin("papa");
            localUserOne.setEnable(true);
            localUserOne.setPassword("pwd");
            userService.create(localUserOne);

            localCurrencyOne = new Currency("XXX", 2L);
            localCurrencyTwo = new Currency("YYY", 159L);
            currencyService.create(localCurrencyOne);
            currencyService.create(localCurrencyTwo);

            localUserCurrencyOne = new UserCurrency();
            localUserCurrencyOne.setCurrencyId(localCurrencyOne.getId());
            localUserCurrencyOne.setUserId(localUserOne.getId());
            localUserCurrencyOne.setValue(494.4f);
            localUserCurrencyOne.setEnable(true);
            // FIXME do we need to automate UserCurrency creation via Hibernate or Service layer?
            userCurrencyServiceImpl.create(localUserCurrencyOne);
            localUserCurrencyTwo = new UserCurrency();
            localUserCurrencyTwo.setCurrencyId(localCurrencyTwo.getId());
            localUserCurrencyTwo.setUserId(localUserOne.getId());
            localUserCurrencyTwo.setValue(9999.9f);
            userCurrencyServiceImpl.create(localUserCurrencyTwo);

            localWalletOne = new Wallet("serhii's", localUserCurrencyOne.getId());
            localWalletTwo = new Wallet("tolik's",  localUserCurrencyOne.getId());
            localWalletThree = new Wallet("joint", localUserCurrencyOne.getId());

            walletService.create(localWalletOne);
            walletService.create(localWalletTwo);
            walletService.create(localWalletThree);
        }
        AssertSqlCount.reset();
        logger.info("==========\nEnd of beforeEach() method BEFOREEACH\n==========");
    }

    @AfterEach
    public void afterEach(TestInfo testInfo) {
        logger.info("==========\nStart of afterEach() method\n==========");
        if (!testInfo.getTags().contains("dontadd")) {
            List listForCleaning = Arrays.asList(localWalletOne, localWalletThree, localWalletThree, localCurrencyOne,
                    localCurrencyOne, localCurrencyTwo, localUserOne);
            Stream<Wallet> walletForDeletion = walletService.findAll();
            walletForDeletion.map(Wallet::getId).forEach(walletService::delete);

            Stream<UserCurrency> userCurrencyForDeletion = userCurrencyServiceImpl.findByUserId(localUserOne.getId());
            userCurrencyForDeletion.map(UserCurrency::getId).forEach(userCurrencyServiceImpl::delete);

            Stream<Currency> currencyForDeletion = currencyService.findAll();
            currencyForDeletion.map(Currency::getId).forEach(currencyService::delete);

            userService.delete(localUserOne.getId());
        }
        logger.info("==========\nEnd of afterEach() method\n==========");
    }

    @Test
    public void findOne_ifExists() throws Exception {
        Optional<Wallet> actualWallet = walletService.findOne(localWalletOne.getId());
        assertTrue(actualWallet.isPresent(), "Actual WalletOne must exists, but it's not that");
        Long localUserCurrencyId = actualWallet.get().getUserCurrencyId();
        assertAll(
                () -> assertEquals(localUserCurrencyOne, userCurrencyServiceImpl.findOne(localUserCurrencyId).get(),
                        "Expected UserCurrencyOne from init() and actual from test are not equal"),
                () -> assertEquals(localUserOne,
                        userService.findOne(userCurrencyServiceImpl.findOne(localUserCurrencyId).get().getUserId()).get(),
                        "Expected UserOne from init() and actual from test are not equal"),
                () -> assertEquals(localCurrencyOne,
                        currencyService.findOne(userCurrencyServiceImpl.findOne(localUserCurrencyId).get().getCurrencyId()).get(),
                        "Expected CurrencyOne from init() and actual from test are not equal")
        );
        assertSelectCount(6);
    }

    @Test
    public void findOne_notExists() {
        assertFalse(walletService.findOne(Long.MAX_VALUE).isPresent(), "Actual Wallet must be empty, but it's not true.");
        assertSelectCount(1);
    }

    @Test
    @Tag("dontadd")
    public void finalAll_notExists() {
        assertEquals(0, walletService.findAll().count(), "FindAll() must be empty at all, but it's not true.");
        assertSelectCount(1);
    }

    @Test
    public void findWalletByNameAmdUserCurrency_ifExists() {
        Supplier<Stream<Wallet>> actualWalletSupplier = () -> walletService.findByNameAndUserCurrencyId(
                localWalletOne.getName(), localUserCurrencyOne.getId());
        assertEquals(1, actualWalletSupplier.get().count(), "You must find prepared 1 Wallet, but really found: " +
                actualWalletSupplier.get().count());
        Long localUserCurrencyId = actualWalletSupplier.get().findAny().get().getUserCurrencyId();

        assertAll(
                () -> assertEquals(localWalletOne, actualWalletSupplier.get().findAny().get(),
                        "Expected WalletOne from init() and actual from test are not equal"),
                () -> assertEquals(localUserOne,
                        userService.findOne(userCurrencyServiceImpl.findOne(localUserCurrencyId).get().getUserId()).get(),
                        "Expected UserOne from init() and actual from test are not equal"),
                () -> assertEquals(localCurrencyOne,
                        currencyService.findOne(userCurrencyServiceImpl.findOne(localUserCurrencyId).get().getCurrencyId()).get(),
                        "Expected CurrencyOne from init() and actual from test are not equal")
        );
        assertSelectCount(8);
    }

    @Test
    public void findWalletByNameAndUserAndCurrency_notExists() throws Exception {
        Stream<Wallet> actualWallet = walletService.findByNameAndUserCurrencyId(localWalletOne.getName().toUpperCase(), localUserCurrencyOne.getId());
        assertEquals(0, actualWallet.count(), "Wallet count should be 0, but it's not true.");
        assertSelectCount(1);
    }

    @Test
    public void createWallet_notExists() {
        long walletCountBefore = walletService.findAll().count();
        Wallet walletForCreation = new Wallet("walletForCreation", localUserCurrencyOne.getId());
        Wallet actualWallet = walletService.create(walletForCreation);
        long walletCountAfter = walletService.findAll().count();

        assertAll(
                () -> assertNotNull(actualWallet, "Wallet is Null after creation, but must no be Null."),
                () -> assertEquals(walletForCreation, actualWallet, "Wallet before creation and really created Wallet are not equal."),
                () -> assertEquals(localUserOne,
                        userService.findOne(userCurrencyServiceImpl.findOne(actualWallet.getUserCurrencyId()).get().getUserId()).get(),
                        "Expected UserOne and Wallet's User after creation are not equal."),
                () -> assertEquals(localCurrencyOne,
                        currencyService.findOne(userCurrencyServiceImpl.findOne(actualWallet.getUserCurrencyId()).get().getCurrencyId()).get(),
                        "Expected CurrencyOne and Wallet's User after creation are not equal."),
                () -> assertEquals(walletCountBefore + 1, walletCountAfter, "Number of Wallet should be 1 more bigger, but it's not that.")
        );
        assertSelectCount(7);
        assertInsertCount(1);

    }

    @Test
    public void createWallet_ifExists() throws Exception {
        long walletCountBefore = walletService.findAll().count();
        Wallet walletForCreation = new Wallet(localWalletOne.getName(), localWalletOne.getUserCurrencyId());
        Throwable localException = Assertions.expectThrows(ObjectAlreadyExists.class, () -> walletService.create(walletForCreation));
        long walletCountAfter = walletService.findAll().count();

        assertAll(
                () -> assertEquals("Object Wallet already exists.", localException.getMessage(),
                        "Expected throw message and really thrown are not equal."),
                () -> assertEquals(walletCountBefore, walletCountAfter,
                        "Expected WalletCount and count after update one are not equal.")
        );

        assertSelectCount(3);
        assertInsertCount(0);
    }

    @Test
    public void updateWalletByUserCurrencyId_ifExistsNotSame() throws Exception {
        long walletCountBefore = walletService.findAll().count();
        localWalletThree.setUserCurrencyId(localUserCurrencyTwo.getId());
        Wallet actualWallet = walletService.updateUserCurrencyId(localWalletThree.getId(), localWalletThree.getUserCurrencyId());
        long walletCountAfter = walletService.findAll().count();
        assertAll(
                () -> assertNotNull(actualWallet, "Updated wallet is Null, but must not be that"),
                () -> assertEquals(localWalletThree, actualWallet, "Expected Wallet and updated one are not equal."),
                () -> assertEquals(localUserCurrencyTwo, userCurrencyServiceImpl.findOne(actualWallet.getUserCurrencyId()).get()),
                () -> assertEquals(localUserOne,
                        userService.findOne(userCurrencyServiceImpl.findOne(actualWallet.getUserCurrencyId()).get().getUserId()).get(),
                        "Expected UserOne and Wallet's User after update are not the same"),
                () -> assertEquals(localCurrencyTwo,
                        currencyService.findOne(userCurrencyServiceImpl.findOne(actualWallet.getUserCurrencyId()).get().getCurrencyId()).get(),
                        "Expected CurrencyOne and Wallet's Currency after update are not the same")
        );

        assertSelectCount(8);
        assertInsertCount(0);
        assertUpdateCount(1);
    }

    @Test
    public void updateWalletByUserCurrencyId_ifExistsSame() throws Exception {
        long walletCountBefore = walletService.findAll().count();
        Throwable localException = Assertions.expectThrows(IllegalArgumentException.class,
                () -> walletService.updateUserCurrencyId(localWalletThree.getId(), localWalletThree.getUserCurrencyId()));
        long walletCountAfter = walletService.findAll().count();

        assertAll(
                () -> assertEquals("Exactly same Object Wallet already exists.", localException.getMessage(),
                        "Expected throw message and really thrown are not equal."),
                () -> assertEquals(walletCountBefore, walletCountAfter,
                        "Expected Wallet Count before and after update one are not equal.")
        );
        assertSelectCount(3);
        assertInsertCount(0);
        assertUpdateCount(0);
    }

    @Test
    public void updateWalletByUserCurrencyId_notExists() throws Exception {
        long walletCountBefore = walletService.findAll().count();
        Throwable localException = Assertions.expectThrows(NullPointerException.class,
                () -> walletService.updateUserCurrencyId(Long.MAX_VALUE, localUserCurrencyTwo.getId()));
        long walletCountAfter = walletService.findAll().count();

        assertAll(
                () -> assertEquals("Wallet doesn't exist with id: " + Long.MAX_VALUE, localException.getMessage(),
                        "Expected throw message and really thrown are not equal."),
                () -> assertEquals(walletCountBefore, walletCountAfter,
                        "Expected Wallet Count before and after update one are not equal.")
        );

        assertSelectCount(3);
        assertInsertCount(0);
        assertUpdateCount(0);
    }

    @Test
    public void updateWalletByName_ifExistsNotSame() throws Exception {
        long walletCountBefore = walletService.findAll().count();
        localWalletThree.setName(localWalletThree.getName().toUpperCase());
        Wallet actualWallet = walletService.updateName(localWalletThree.getId(), localWalletThree.getName());
        long walletCountAfter = walletService.findAll().count();
        assertAll(
                () -> assertNotNull(actualWallet, "Updated wallet is Null, but must not be that"),
                () -> assertEquals(localWalletThree, actualWallet, "Expected Wallet and updated one are not equal."),
                () -> assertEquals(localUserCurrencyOne, userCurrencyServiceImpl.findOne(actualWallet.getUserCurrencyId()).get()),
                () -> assertEquals(localUserOne,
                        userService.findOne(userCurrencyServiceImpl.findOne(actualWallet.getUserCurrencyId()).get().getUserId()).get(),
                        "Expected UserOne and Wallet's User after update are not the same"),
                () -> assertEquals(localCurrencyOne,
                        currencyService.findOne(userCurrencyServiceImpl.findOne(actualWallet.getUserCurrencyId()).get().getCurrencyId()).get(),
                        "Expected CurrencyOne and Wallet's Currency after update are not the same")
        );

        assertSelectCount(8);
        assertInsertCount(0);
        assertUpdateCount(1);
    }

    @Test
    public void updateWalletByName_ifExistsSame() throws Exception {
        long walletCountBefore = walletService.findAll().count();
        Throwable localException = Assertions.expectThrows(IllegalArgumentException.class,
                () -> walletService.updateName(localWalletThree.getId(), localWalletThree.getName()));
        long walletCountAfter = walletService.findAll().count();

        assertAll(
                () -> assertEquals("Exactly same Object Wallet already exists.", localException.getMessage(),
                        "Expected throw message and really thrown are not equal."),
                () -> assertEquals(walletCountBefore, walletCountAfter,
                        "Expected Wallet Count before and after update one are not equal.")
        );
        assertSelectCount(3);
        assertInsertCount(0);
        assertUpdateCount(0);
    }

    @Test
    public void updateWalletByName_notExists() throws Exception {
        long walletCountBefore = walletService.findAll().count();
        Throwable localException = Assertions.expectThrows(NullPointerException.class,
                () -> walletService.updateName(Long.MAX_VALUE, localWalletThree.getName()));
        long walletCountAfter = walletService.findAll().count();

        assertAll(
                () -> assertEquals("Wallet doesn't exist with id: " + Long.MAX_VALUE, localException.getMessage(),
                        "Expected throw message and really thrown are not equal."),
                () -> assertEquals(walletCountBefore, walletCountAfter,
                        "Expected Wallet Count before and after update one are not equal.")
        );

        assertSelectCount(3);
        assertInsertCount(0);
        assertUpdateCount(0);
    }

    @Test
    public void deleteWallet_ifExists() throws Exception {
        long walletCountBefore = walletService.findAll().count();
        Long localWalletId = localWalletTwo.getId();
        Long localUserCurrencyId = localWalletTwo.getUserCurrencyId();
        walletService.delete(localWalletId);
        long walletCountAfter = walletService.findAll().count();

        Optional<Wallet> actualWallet = walletService.findOne(localWalletId);
        Optional<UserCurrency> actualUserCurrency = userCurrencyServiceImpl.findOne(localUserCurrencyOne.getId());

        assertAll(
                () -> assertFalse(actualWallet.isPresent(), "Deleted object exists, wtf?"),
                () -> assertTrue(actualUserCurrency.isPresent(), "UserCurrency disappeared after Wallet deletion method."),
                () -> assertEquals(localUserOne,
                        userService.findOne(userCurrencyServiceImpl.findOne(localUserCurrencyId).get().getUserId()).get(),
                        "User disappeared or changed after Wallet deletion method."),
                () -> assertEquals(localCurrencyOne,
                        currencyService.findOne(userCurrencyServiceImpl.findOne(localUserCurrencyId).get().getCurrencyId()).get(),
                        "Currency disappeared or changed after Wallet deletion method.")
        );

        assertSelectCount(10);
        assertInsertCount(0);
        assertDeleteCount(1);
    }

    @Test
    public void deleteWallet_notExists() throws Exception {
        Wallet walletForDelete = new Wallet("walletForDelete", localUserCurrencyTwo.getId());
        Throwable localException = Assertions.expectThrows(NullPointerException.class,
                () -> walletService.delete(walletForDelete.getId()));
        assertEquals("Object doesn't exist", localException.getMessage());

        assertSelectCount(1);
        assertDeleteCount(0);
    }
}