package com.budget.core.service;

import com.budget.core.entity.Currency;
import com.budget.core.entity.User;
import com.budget.core.entity.UserCurrency;
import com.budget.core.entity.Wallet;
import com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount;
import org.apache.log4j.Logger;
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

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.assertSelectCount;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class WalletServiceTest {
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
    private Currency localCurrencyOne;

    @BeforeEach
    public void init(TestInfo testInfo) {
        logger.info("==========\nStart of init() method\n==========");
        if (!testInfo.getTags().contains("dontadd")) {

            localUserOne = new User();
            localUserOne.setLogin("papa");
            localUserOne.setEnable(true);
            localUserOne.setPassword("pwd");
            localCurrencyOne = new Currency("XXX", 2L);
            localUserCurrencyOne = new UserCurrency();
            localUserCurrencyOne.setCurrency(localCurrencyOne);
            localUserCurrencyOne.setUser(localUserOne);
            localUserCurrencyOne.setEnable(true);
            localWalletOne = new Wallet("serhii's", true, localUserCurrencyOne);
            localWalletTwo = new Wallet("tolik's", true, localUserCurrencyOne);
            localWalletThree = new Wallet("joint", true, localUserCurrencyOne);
            userService.create(localUserOne);
            currencyService.create(localCurrencyOne);
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
        Optional<Wallet> actualWallet = walletService.findOne(localWalletOne.getId());
        assertAll(
                () -> assertTrue(actualWallet.isPresent(), "Actual Wallet must exists, but it's not that"),
                () -> assertEquals(localWalletOne, actualWallet.get()),
                () -> assertEquals(localUserCurrencyOne, actualWallet.get().getUserCurrency())
        );
        assertSelectCount(0);
    }

    @Test
    public void findOne_notExists() throws Exception {
        assertFalse(walletService.findOne(Long.MAX_VALUE).isPresent(), "Actual Wallet must be empty, but it's not true.");
        assertSelectCount(1);
    }

    @Test
    @Tag("dontadd")
    public void findAll_notExists() throws Exception {
        Stream<Wallet> actualWallets = walletService.findAll();
        assertAll(
                () -> assertNotNull(actualWallets, "FindAll() method returned Null, something went wrong."),
                () -> assertEquals(0, actualWallets.count()));
        assertSelectCount(1);
    }

    @Test
    public void findWalletByUserCurrency_ifExists() throws Exception {
        Supplier<Stream<Wallet>> actualWalletSupplier =
                () -> walletService.findByUserCurrencyId(localUserCurrencyOne.getId());
        assertAll(
                () -> assertNotNull(actualWalletSupplier, "We must have not null Wallet Supplier, but it's empty."),
                () -> assertEquals(3, actualWalletSupplier.get().count(),
                        "You must find prepared 3 Wallets, but really found: " + actualWalletSupplier.get().count()),
                () -> assertEquals(localUserCurrencyOne, actualWalletSupplier.get().findAny().get().getUserCurrency(),
                        "Expected UserCurrency from init() doesn't equal to actual one."),
                () -> assertEquals(Arrays.asList(localWalletOne, localWalletTwo, localWalletThree),
                        actualWalletSupplier.get().collect(Collectors.toList()),
                        "List's of actual Wallets from init() and expected Wallets from here are not equal.")
        );
        // If You delete any get() method from assert's string message, You will decrease select count bu ONE!!!
        assertSelectCount(4);
    }

    @Test
    public void findWalletByUserCurrency_notExists() throws Exception {
        Stream<Wallet> actualWallet = walletService.findByUserCurrencyId(Long.MAX_VALUE);
        assertAll(
                () -> assertNotNull(actualWallet, "FindByUserCurrencyId method returned Null, something went wrong."),
                () -> assertEquals(0, actualWallet.count(), "Wallet count should be 0, but it's not true.")
        );
        assertSelectCount(1);
    }

    @Test
    public void findWalletByNameAndUserCurrency_ifExists() throws Exception {
        Supplier<Stream<Wallet>> actualWalletSupplier =
                () -> walletService.findByNameAndUserCurrencyId(localWalletTwo.getName(), localUserCurrencyOne.getId());
        assertAll(
                () -> assertNotNull(actualWalletSupplier, "We must have not null Wallet Supplier, but it's empty."),
                () -> assertEquals(1, actualWalletSupplier.get().count(),
                        "You must find prepared 1 Wallets with such Name and USerCurrencyId, but really found: "
                                + actualWalletSupplier.get().count()),
                () -> assertEquals(localUserCurrencyOne, actualWalletSupplier.get().findAny().get().getUserCurrency(),
                        "Expected UserCurrency from init() doesn't equal to actual one."),
                () -> assertEquals(localWalletTwo, actualWalletSupplier.get().findAny().get(),
                        "Actual Wallet from init() and expected Wallet from here are not equal.")
        );
        // If You delete any get() method from assert's string message, You will decrease select count bu ONE!!!
        assertSelectCount(4);
    }

    @Test
    public void findWalletByNameAndUserCurrency_notExists() throws Exception {
        Stream<Wallet> actualWallet = walletService.findByNameAndUserCurrencyId(localWalletTwo.getName().toUpperCase(),
                localUserCurrencyOne.getId());
        assertAll(
                () -> assertNotNull(actualWallet, "FindByNameAndUserCurrencyId method returned Null, something went wrong."),
                () -> assertEquals(0, actualWallet.count(), "Wallet count should be 0, but it's not true.")
        );
    }

//    @Test
    public void findUserCurrencyByCurrency_ifExists() throws Exception {
    }

//    @Test
    public void findUserCurrencyByCurrency_notExists() throws Exception {
    }

//    @Test
    public void createUserCurrency_notExists() throws Exception {
    }

//    @Test
    public void createUserCurrency_ifExists() throws Exception {
    }

//    @Test
    public void updateUserCurrency_ifExistsNotSame() throws Exception {
    }

//    @Test
    public void updateUserCurrency_ifExistsSame() throws Exception {
    }

//    @Test
    public void updateUserCurrency_notExists() throws Exception {
    }

//    @Test
    public void deleteUserCurrency_ifExists() throws Exception {
    }

//    @Test
    public void deleteUserCurrency_notExists() throws Exception {
    }
}