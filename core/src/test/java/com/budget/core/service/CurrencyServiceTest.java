package com.budget.core.service;

import com.budget.core.entity.Currency;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("h2")
class CurrencyServiceTest {

    @Autowired
    private CurrencyService currencyService;

    private static Currency currency;

    @BeforeEach
    public void init() {
        if(currency == null) {
            currency = new Currency();
            currency.setName("XXX");
            currency.setValue(753L);

            currency = currencyService.create(currency);
        }
    }

    @AfterEach
    public void afterEach() {
        if(currency != null) {
            currencyService.delete(currency.getId());
            currency = null;
        }
    }

    @Test
    public void findOne_ifExists() throws Exception {
        Optional<Currency> expectedCurrency = currencyService.findOne(currency.getId());
        assertAll(
                () -> assertTrue(expectedCurrency.isPresent()),
                () -> assertEquals(expectedCurrency.get(), currency));
    }

    @Test
    public void findOne_notExists() throws Exception {
        assertFalse(currencyService.findOne(Long.MAX_VALUE).isPresent());
    }

    @Test
    public void findByName_ifExists() throws Exception {
        Stream<Currency> currencies = currencyService.findByName(currency.getName());
        assertAll(
                () -> assertNotNull(currencies),
                () -> assertEquals(currencies.findFirst().get(), currency));
    }

    @Test
    public void findByName_notExists() throws Exception {
        currency.setName(currency.getName().toLowerCase());
        Stream<Currency> currencies = currencyService.findByName(currency.getName());
        assertFalse(currencies.findFirst().isPresent());
    }

    @Test
    public void findAll_notExists() throws Exception {
        currencyService.delete(currency.getId());
        List<Currency> currencies = currencyService.findAll();
        currency = null;
        assertAll(
                () -> assertNotNull(currencies),
                () -> assertEquals(currencies.size(), 0));
    }

    @Test
    public void findAll_fewResults() throws Exception {
        Currency secondCurrency = new Currency();
        secondCurrency.setName(currency.getName().toLowerCase());
        secondCurrency.setValue(2L);
        currencyService.create(secondCurrency);

        List<Currency> currencies = currencyService.findAll();
        assertAll(
                () -> assertNotNull(currencies),
                () -> assertEquals(currencies.size(), 2));
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
    }

    @Test
    public void create_ifExists() throws Exception {
        Currency newCurrency = new Currency(currency.getName(), currency.getValue());
        Throwable localException = Assertions.expectThrows(IllegalArgumentException.class,
                () -> currencyService.create(newCurrency));
        assertEquals("Object already exists", localException.getMessage());
    }

    @Test
    public void update_ifExists() throws Exception {
        currency.setName(currency.getName().toLowerCase());
        currency.setValue(currency.getValue() + 3L);
        currencyService.update(currency);

        Optional<Currency> expectedCurrency = currencyService.findOne(currency.getId());
        assertAll(
                () -> assertTrue(expectedCurrency.isPresent()),
                () -> assertEquals(expectedCurrency.get(), currency));
    }

    @Test
    public void update_notExists() throws Exception {
        Currency localCurrency = new Currency("YYY", 10657L);
        Throwable localException = Assertions.expectThrows(NullPointerException.class,
                () -> currencyService.update(localCurrency));
        assertEquals("Object doesn't exist", localException.getMessage());
    }

    @Test
    public void delete_ifExists() throws Exception {
        currencyService.delete(currency.getId());
        Optional<Currency> expectedCurrency = currencyService.findOne(currency.getId());
        currency = null;
        assertAll(
                () -> assertTrue(!expectedCurrency.isPresent()));
    }

    @Test
    public void delete_notExists() throws Exception {
        Throwable localException = Assertions.expectThrows(NullPointerException.class,
                () -> currencyService.delete(Long.MAX_VALUE));
        assertEquals("Object doesn't exist", localException.getMessage());
    }
}