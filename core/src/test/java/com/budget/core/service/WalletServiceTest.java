package com.budget.core.service;

import com.budget.core.entity.Currency;
import com.budget.core.entity.User;
import com.budget.core.entity.UserCurrency;
import com.budget.core.entity.Wallet;
import com.budget.core.exception.ObjectAlreadyExists;
import com.budget.core.exception.ObjectNotFoundException;
import com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.assertDeleteCount;
import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.assertInsertCount;
import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.assertSelectCount;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class WalletServiceTest {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private WalletService walletService;
    @Autowired
    private UserCurrencyService userCurrencyService;
    @Autowired
    private UserService userService;
    @Autowired
    private CurrencyService currencyService;

    private static final Logger logger = Logger.getLogger(WalletServiceTest.class);

    private Wallet localWalletOne, localWalletTwo, localWalletThree;
    private UserCurrency localUserCurrencyOne;
    private User localUserOne;
    private Currency localCurrencyOne, localCurrencyTwo;

    @BeforeEach
    public void init(TestInfo testInfo) {
        logger.info("==========\nStart of init() method\n==========");
        if (!testInfo.getTags().contains("dontadd")) {
            localUserOne = new User();
            localUserOne.setLogin("papa");
            localUserOne.setEnable(true);
            localUserOne.setPassword("pwd");

            localCurrencyOne = new Currency("XXX", 2L);
            localCurrencyTwo = new Currency("YYY", 159L);

            localUserCurrencyOne = new UserCurrency();
            localUserCurrencyOne.setCurrency(localCurrencyOne);
            localUserCurrencyOne.setUser(localUserOne);
            localUserCurrencyOne.setEnable(true);

            localWalletOne = new Wallet("serhii's", localUserOne, localCurrencyOne);
            localWalletTwo = new Wallet("tolik's",  localUserOne, localCurrencyOne);
            localWalletThree = new Wallet("joint", localUserOne, localCurrencyOne);

            userService.create(localUserOne);
            currencyService.create(localCurrencyOne);
            currencyService.create(localCurrencyTwo);
            // FIXME do we need to automate UserCurrency creation via Hibernate or Service layer?
            userCurrencyService.create(localUserCurrencyOne);

            walletService.create(localWalletOne);
            walletService.create(localWalletTwo);
            walletService.create(localWalletThree);
        }
        AssertSqlCount.reset();
        logger.info("==========\nEnd of init() method\n==========");
    }

    @Test
    public void findOne_ifExists() throws Exception {
        List listForCleaning = Arrays.asList(localWalletOne, localUserOne, localCurrencyOne);
        clearListOfObjectViaEntityManagerAndClearAssertSqlCount(listForCleaning);
        Optional<Wallet> actualWallet = walletService.findOne(localWalletOne.getId());
        assertAll(
                () -> assertTrue(actualWallet.isPresent(), "Actual Wallet must exists, but it's not that"),
                () -> assertEquals(localWalletOne, actualWallet.get()),
                () -> assertEquals(localUserOne, actualWallet.get().getUser()),
                () -> assertEquals(localCurrencyOne, actualWallet.get().getCurrency())
        );
        assertSelectCount(0 + listForCleaning.size());
    }


    @Test
    public void findOne_notExists() throws Exception {
        List listForCleaning = Arrays.asList(localWalletOne);
        clearListOfObjectViaEntityManagerAndClearAssertSqlCount(listForCleaning);
        assertFalse(walletService.findOne(Long.MAX_VALUE).isPresent(), "Actual Wallet must be empty, but it's not true.");
        assertSelectCount(1 + listForCleaning.size());
    }

    @Test
    @Tag("dontadd")
    public void findAll_notExists() throws Exception {
        Stream<Wallet> actualWallets = walletService.findAll();
        assertAll(
                () -> assertEquals(0, actualWallets.count()));
        assertSelectCount(1);
    }

    @Test
    public void findWalletByNameAndUserAndCurrency_ifExists() throws Exception {
        List listForCleaning = Arrays.asList(localWalletOne, localWalletTwo, localWalletThree, localUserOne, localCurrencyOne);
        clearListOfObjectViaEntityManagerAndClearAssertSqlCount(listForCleaning);

        Supplier<Stream<Wallet>> actualWalletSupplier = () -> walletService.findByNameAndUserIdAndCurrencyId(
                        localWalletTwo.getName(), localUserOne.getId(), localCurrencyOne.getId());
        assertAll(
                () -> assertNotNull(actualWalletSupplier, "We must have not null Wallet Supplier, but it's empty."),
                () -> assertEquals(1, actualWalletSupplier.get().count(),
                        "You must find prepared 1 Wallet, but really found: " + actualWalletSupplier.get().count()),
                () -> assertEquals(localUserOne, actualWalletSupplier.get().findAny().get().getUser(),
                        "Expected UserOne from init() doesn't equal to actual one."),
                () -> assertEquals(localCurrencyOne, actualWalletSupplier.get().findAny().get().getCurrency(),
                        "Expected CurrencyOne from init() doesn't equal to actual one."),
                () -> assertEquals(localWalletTwo, actualWalletSupplier.get().findAny().get(),
                        "You must find WalletTwo, but it's not that.")
        );
        // If You delete any get() method from assert's string message, You will decrease select count bu ONE!!!
        assertSelectCount(5 + listForCleaning.size());
    }

    @Test
    public void findWalletByNameAndUserAndCurrency_notExists() throws Exception {
        List listForCleaning = Arrays.asList(localWalletOne, localWalletTwo, localWalletThree, localUserOne, localCurrencyOne);
        clearListOfObjectViaEntityManagerAndClearAssertSqlCount(listForCleaning);
        Stream<Wallet> actualWallet = walletService.findByNameAndUserIdAndCurrencyId(
                localWalletTwo.getName().toUpperCase(), localUserOne.getId(), localCurrencyOne.getId()
        );
        assertAll(
                () -> assertEquals(0, actualWallet.count(), "Wallet count should be 0, but it's not true.")
        );
        assertSelectCount(1 + listForCleaning.size());
    }

    @Test
    public void createWallet_notExists() throws Exception {
        List listForCleaning = Arrays.asList(localWalletOne, localWalletTwo, localWalletThree, localUserOne, localCurrencyOne, localCurrencyTwo);
        clearListOfObjectViaEntityManagerAndClearAssertSqlCount(listForCleaning);

        long walletCountBefore = walletService.findAll().count();

        Wallet walletForCreation = new Wallet("walletForCreation_notExists", localUserOne, localCurrencyTwo);
        Wallet actualWallet = walletService.create(walletForCreation);

        assertAll(
                () -> assertNotNull(actualWallet, "Wallet is Null after creation, but must no be Null."),
                () -> assertEquals(walletForCreation, actualWallet, "Wallet before creation and really created Wallet are not equal."),
                () -> assertEquals(localUserOne, actualWallet.getUser(), "Expected UserOne and Wallet's User after creation are not equal."),
                () -> assertEquals(localCurrencyTwo, actualWallet.getCurrency(), "Expected CurrencyTwo and Wallet's Currency after creation are not equal.")
        );

        long walletCountAfter = walletService.findAll().count();

        assertSelectCount(3 + listForCleaning.size());
        assertInsertCount(1);
        assertEquals(walletCountBefore + 1, walletCountAfter);
    }

    @Test
    public void createWallet_ifExists() throws Exception {
        List listForCleaning = Arrays.asList(localWalletOne, localWalletTwo, localWalletThree, localUserOne, localCurrencyOne, localCurrencyTwo);
        clearListOfObjectViaEntityManagerAndClearAssertSqlCount(listForCleaning);

        long walletCountBefore = walletService.findAll().count();
        Wallet walletForCreation = new Wallet(localWalletOne.getName(), localUserOne, localCurrencyOne);
        Throwable localException = Assertions.expectThrows(ObjectAlreadyExists.class,
                () -> walletService.create(walletForCreation));
        long walletCountAfter = walletService.findAll().count();

        assertAll(
                () -> assertEquals("Object Wallet already exists.", localException.getMessage(),
                        "Expected throw message and really thrown are not equal."),
                () -> assertEquals(walletCountBefore, walletCountAfter,
                        "Expected WalletCount and count after update one are not equal.")
        );

        assertSelectCount(3 + listForCleaning.size());
        assertInsertCount(0);
    }


    @Test
    public void updateWallet_ifExistsNotSame() throws Exception {
        List listForCleaning = Arrays.asList(localWalletOne, localWalletTwo, localWalletThree, localUserOne, localCurrencyOne, localCurrencyTwo);
        clearListOfObjectViaEntityManagerAndClearAssertSqlCount(listForCleaning);

        long walletCountBefore = walletService.findAll().count();
        localWalletThree.setCurrency(localCurrencyTwo);
        Wallet actualWallet = walletService.update(localWalletThree);
        long walletCountAfter = walletService.findAll().count();

        assertAll(
                () -> assertNotNull(actualWallet, "Updated wallet is Null, but must not be that"),
                () -> assertEquals(localWalletThree, actualWallet, "Expected Wallet and updated one are not equal."),
                () -> assertEquals(localCurrencyTwo, actualWallet.getCurrency(), "Expected CurrencyTwo and Wallet's Currency after update are not the same"),
                () -> assertEquals(localUserOne, actualWallet.getUser(), "Expected UserOne and Wallet's User after update are not the same"),
                () -> assertEquals(walletCountBefore, walletCountAfter, "Wallet's count before and after update are not the same, but must be.")
        );

        assertSelectCount(3 + listForCleaning.size());
        assertInsertCount(0);
    }

    @Test
    public void updateWallet_ifExistsSame() throws Exception {
        List listForCleaning = Arrays.asList(localWalletOne, localWalletTwo, localWalletThree, localUserOne, localCurrencyOne, localCurrencyTwo);
        clearListOfObjectViaEntityManagerAndClearAssertSqlCount(listForCleaning);
        long walletCountBefore = walletService.findAll().count();
        localWalletThree.setCurrency(localWalletThree.getCurrency());
        Throwable localException = Assertions.expectThrows(IllegalArgumentException.class,
                () -> walletService.update(localWalletThree));
        long walletCountAfter = walletService.findAll().count();

        assertAll(
                () -> assertEquals("Exactly same Object Wallet already exists.", localException.getMessage(),
                        "Expected throw message and really thrown are not equal."),
                () -> assertEquals(walletCountBefore, walletCountAfter,
                        "Expected Wallet Count before and after update one are not equal.")
        );

        assertSelectCount(3 + listForCleaning.size());
        assertInsertCount(0);

    }

    @Test
    public void updateWallet_notExists() throws Exception {
        List listForCleaning = Arrays.asList(localWalletOne, localWalletTwo, localWalletThree, localUserOne, localCurrencyOne, localCurrencyTwo);
        clearListOfObjectViaEntityManagerAndClearAssertSqlCount(listForCleaning);
        long walletCountBefore = walletService.findAll().count();
        Wallet walletForUpdate = new Wallet("unknown", localUserOne, new Currency("ZZZ", 846L));
        Throwable localException = Assertions.expectThrows(ObjectNotFoundException.class,
                () -> walletService.update(walletForUpdate));
        long walletCountAfter = walletService.findAll().count();

        assertAll(
                () -> assertEquals("Object Wallet not found.", localException.getMessage(),
                        "Expected throw message and really thrown are not equal."),
                () -> assertEquals(walletCountBefore, walletCountAfter,
                        "Expected Wallet Count before and after update one are not equal.")
        );

        assertSelectCount(4 + listForCleaning.size());
        assertInsertCount(0);
    }

    @Test
    public void deleteWallet_ifExists() throws Exception {
        Long localWalletId = localWalletOne.getId();
        List listForCleaning = Arrays.asList(localWalletOne, localWalletTwo, localWalletThree, localUserOne, localCurrencyOne, localCurrencyTwo);
        clearListOfObjectViaEntityManagerAndClearAssertSqlCount(listForCleaning);
        walletService.delete(localWalletOne);

        Optional<Wallet> actualWallet = walletService.findOne(localWalletId);
        Optional<User> actualUser = userService.findOne(localUserOne.getId());
        Optional<Currency> actualCurrency = currencyService.findOne(localCurrencyOne.getId());

        assertAll(
                () -> assertFalse(actualWallet.isPresent(), "Deleted object exists, wtf?"),
                () -> assertTrue(actualUser.isPresent(), "User disappeared after UserCurrency deletion method."),
                () -> assertEquals(localUserOne, actualUser.get(), "Expected User and remained after Wallet deletion are not equal"),
                () -> assertTrue(actualCurrency.isPresent(), "Currency disappeared after UserCurrency deletion."),
                () -> assertEquals(localCurrencyOne, actualCurrency.get(), "Expected Currency and remained after Wallet deletion are not equal.")
        );

        assertSelectCount(0 + listForCleaning.size());
        assertInsertCount(0);
        assertDeleteCount(0);
    }

    @Test
    public void deleteWallet_notExists() throws Exception {
        Wallet walletForDelete = new Wallet("walletForDelete", localUserOne, new Currency("KYJ", 338L));
        Throwable localException = Assertions.expectThrows(NullPointerException.class,
                () -> walletService.delete(walletForDelete));
        assertEquals("Object doesn't exist", localException.getMessage());

        assertSelectCount(1);
        assertDeleteCount(0);
    }

    public void clearListOfObjectViaEntityManagerAndClearAssertSqlCount(List<Object> listOfObjects) {
        logger.info("==========\nStart of clearListOfObjectViaEntityManagerAndClearAssertSqlCount() method\n==========");
        listOfObjects.stream().forEach(entityManager::refresh);
        logger.info("==========\nEnd of clearListOfObjectViaEntityManagerAndClearAssertSqlCount() method\n==========");
    }
}