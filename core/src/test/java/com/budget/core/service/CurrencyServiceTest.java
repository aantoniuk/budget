package com.budget.core.service;

import com.budget.core.entity.Currency;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    @Test
    public void findOne() throws Exception {
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
    public void findByName() throws Exception {
        Stream<Currency> currencies = currencyService.findByName(currency.getName());
        assertAll(
                () -> assertNotNull(currencies),
                () -> assertEquals(currencies.findFirst().get(), currency));
    }

    @Test
    public void findByType_notExists() throws Exception {
        currency.setName(currency.getName().toLowerCase());
        Stream<Currency> currencies = currencyService.findByName(currency.getName());
        assertFalse(currencies.findFirst().isPresent());
    }

    @Test
    public void findByName_fewResults() throws Exception {
        Currency secondCurrency = new Currency();
        secondCurrency.setName(currency.getName());
        secondCurrency.setValue(2L);
        currencyService.create(secondCurrency);

        Stream<Currency> currencies = currencyService.findByName(secondCurrency.getName());
        assertAll(
                () -> assertNotNull(currencies),
                () -> assertEquals(currencies.count(), 2));
    }

    @Test
    @Ignore
    public void create() throws Exception {
    }

    @Test
    public void update_Exists() throws Exception {
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
    @Ignore
    public void delete() throws Exception {
    }
}