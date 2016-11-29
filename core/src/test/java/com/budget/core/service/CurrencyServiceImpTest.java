package com.budget.core.service;

import com.budget.core.entity.Currency;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount;
import org.junit.jupiter.api.*;
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
@Transactional
class CurrencyServiceImpTest {

    @Autowired
    private CurrencyService currencyService;

    private static Currency currency;

    @BeforeEach
    public void init(TestInfo testInfo) {
        if (!testInfo.getTags().contains("dontadd")) {
            currency = new Currency();
            currency.setName("XXX");
            currency.setValue(753L);
            currency = currencyService.create(currency);
        }
        AssertSqlCount.reset();
    }

    @Test
    public void findOne_ifExists() throws Exception {
        Optional<Currency> expectedCurrency = currencyService.findOne(currency.getId());
        assertAll(
                () -> assertTrue(expectedCurrency.isPresent()),
                () -> assertEquals(expectedCurrency.get(), currency));
        assertSelectCount(0);
    }

    @Test
    public void findOne_notExists() throws Exception {
        assertFalse(currencyService.findOne(Long.MAX_VALUE).isPresent());
        assertSelectCount(1);
    }

    @Test
    public void findByName_ifExists() throws Exception {
        Optional<Currency> currencies = currencyService.findByName(currency.getName());
        assertAll(
                () -> assertTrue(currencies.isPresent()),
                () -> assertEquals(currencies.get(), currency));
        assertSelectCount(1);
    }

    @Test
    public void findByName_notExists() throws Exception {
        currency.setName(currency.getName().toLowerCase());
        Optional<Currency> currencies = currencyService.findByName(currency.getName());
        assertFalse(currencies.isPresent());
        assertSelectCount(1);
    }

    @Test
    @Tag("dontadd")
    public void findAll_notExists() throws Exception {
        Stream<Currency> currencies = currencyService.findAll();
        assertAll(
                () -> assertNotNull(currencies),
                () -> assertEquals(0, currencies.count()));
        assertSelectCount(1);
    }

    @Test
    public void findAll_fewResults() throws Exception {
        Currency secondCurrency = new Currency();
        secondCurrency.setName(currency.getName().toLowerCase());
        secondCurrency.setValue(2L);
        currencyService.create(secondCurrency);
        Stream<Currency> currencies = currencyService.findAll();
        assertAll(
                () -> assertNotNull(currencies),
                () -> assertEquals(currencies.count(), 2));
        assertSelectCount(2);
        assertInsertCount(1);
    }

    @Test
    public void create_notExists() throws Exception {
        Currency localCurrency = new Currency("YYY", 15973L);
        Currency createdCurrency = currencyService.create(localCurrency);
        Optional<Currency> foundCurrency = currencyService.findOne(createdCurrency.getId());
        assertAll(
                () -> assertNotNull(createdCurrency),
                () -> assertEquals(localCurrency, createdCurrency),
                () -> assertTrue(foundCurrency.isPresent()),
                () -> assertEquals(foundCurrency.get(), localCurrency)
        );
        currencyService.delete(createdCurrency.getId());
        assertSelectCount(1);
        assertInsertCount(1);
    }

    @Test
    public void create_ifExists() throws Exception {
        Currency newCurrency = new Currency(currency.getName(), currency.getValue());
        Throwable localException = Assertions.expectThrows(IllegalArgumentException.class,
                () -> currencyService.create(newCurrency));
        assertEquals("Object already exists", localException.getMessage());
        assertSelectCount(1);
    }

    @Test
    public void update_ifExists() throws Exception {
        currency.setValue(currency.getValue() + 3L);
        currencyService.update(currency);
        Optional<Currency> expectedCurrency = currencyService.findOne(currency.getId());
        assertAll(
                () -> assertTrue(expectedCurrency.isPresent()),
                () -> assertEquals(expectedCurrency.get(), currency));

        assertSelectCount(0);
        assertInsertCount(0);
    }

    @Test
    public void update_notExists() throws Exception {
        Currency localCurrency = new Currency("ZZZ", 10657L);
        localCurrency.setId(Long.MAX_VALUE);
        Throwable localException = Assertions.expectThrows(NullPointerException.class,
                () -> currencyService.update(localCurrency));
        assertEquals("Object doesn't exist", localException.getMessage());
        assertSelectCount(1);
    }

    @Test
    public void delete_ifExists() throws Exception {
        currencyService.delete(currency.getId());
        Optional<Currency> expectedCurrency = currencyService.findOne(currency.getId());
        assertAll(
                () -> assertTrue(!expectedCurrency.isPresent()));
        assertSelectCount(0);
        assertDeleteCount(0);
    }

    @Test
    public void delete_notExists() throws Exception {
        Throwable localException = Assertions.expectThrows(NullPointerException.class,
                () -> currencyService.delete(Long.MAX_VALUE));
        assertEquals("Object doesn't exist", localException.getMessage());
        assertSelectCount(1);
        assertDeleteCount(0);
    }
}