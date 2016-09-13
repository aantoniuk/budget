package com.budget.core.rest;

import com.budget.core.entity.Currency;
import com.budget.core.service.CurrencyService;
import com.google.gson.Gson;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by tolik on 8/21/2016.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(CurrencyController.class)
public class CurrencyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService serviceMock;

    private final static Long CURRENCY_ID_LONG = 946L, CURRENCY_VALUE_LONG = 123L;

    Optional<Currency> currencyOptionalNotNullOne, currencyOptionalNotNullTwo, currencyOptionalNull;

    @Test
    public void findById_CurrencyFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(CURRENCY_ID_LONG)).thenReturn(currencyOptionalNotNullOne);

        mockMvc.perform(MockMvcRequestBuilders.get("/currencies/{id}", CURRENCY_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is((int) currencyOptionalNotNullOne.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(currencyOptionalNotNullOne.get().getName())));

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CURRENCY_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }


    @Test
    public void findById_CurrencyNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(CURRENCY_ID_LONG)).thenReturn(currencyOptionalNull);

        mockMvc.perform(MockMvcRequestBuilders.get("/currencies/{id}", CURRENCY_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CURRENCY_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findAll_CurrenciesFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findAll()).thenReturn(Arrays.asList(currencyOptionalNotNullOne.get(), currencyOptionalNotNullTwo.get()));

        mockMvc.perform(MockMvcRequestBuilders.get("/currencies").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is((int) currencyOptionalNotNullOne.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(currencyOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].value", Matchers.is((int) currencyOptionalNotNullOne.get().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is((int) currencyOptionalNotNullTwo.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is(currencyOptionalNotNullTwo.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].value", Matchers.is((int) currencyOptionalNotNullTwo.get().getValue())));

        Mockito.verify(serviceMock, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findAll_CurrenciesNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/currencies").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(serviceMock, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void deleteById_CurrencyIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(CURRENCY_ID_LONG)).thenReturn(currencyOptionalNotNullOne);

        mockMvc.perform(MockMvcRequestBuilders.delete("/currencies/{id}", CURRENCY_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is((int) currencyOptionalNotNullOne.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(currencyOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value", Matchers.is((int) currencyOptionalNotNullOne.get().getValue())));

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CURRENCY_ID_LONG);
        Mockito.verify(serviceMock, Mockito.times(1)).delete(CURRENCY_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void deleteById_CurrenciesNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(CURRENCY_ID_LONG)).thenReturn(currencyOptionalNull);

        mockMvc.perform(MockMvcRequestBuilders.delete("/currencies/{id}", CURRENCY_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CURRENCY_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateById_CurrencyIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(CURRENCY_ID_LONG)).thenReturn(currencyOptionalNotNullOne);
        currencyOptionalNotNullOne.get().setName("Medicine");
        Gson gson = new Gson();
        String jsonString = gson.toJson(currencyOptionalNotNullOne.get());

        mockMvc.perform(MockMvcRequestBuilders.put("/currencies/{id}", CURRENCY_ID_LONG).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is((int) currencyOptionalNotNullOne.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(currencyOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value", Matchers.is((int) currencyOptionalNotNullOne.get().getValue())));

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CURRENCY_ID_LONG);
        Mockito.verify(serviceMock, Mockito.times(1)).save(org.mockito.Matchers.refEq(currencyOptionalNotNullOne.get()));
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateById_CurrenciesNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(CURRENCY_ID_LONG)).thenReturn(currencyOptionalNull);
        Gson gson = new Gson();
        String jsonString = gson.toJson(currencyOptionalNotNullOne.get());

        mockMvc.perform(MockMvcRequestBuilders.put("/currencies/{id}", CURRENCY_ID_LONG).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CURRENCY_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void createByName_CurrencyIsNotFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findByName(currencyOptionalNotNullOne.get().getName())).thenReturn(currencyOptionalNull);
        Gson gson = new Gson();
        String jsonString = gson.toJson(currencyOptionalNotNullOne.get());

        mockMvc.perform(MockMvcRequestBuilders.post("/currencies").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is((int) currencyOptionalNotNullOne.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(currencyOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value", Matchers.is((int) currencyOptionalNotNullOne.get().getValue())));

        Mockito.verify(serviceMock, Mockito.times(1)).findByName(currencyOptionalNotNullOne.get().getName());
        Mockito.verify(serviceMock, Mockito.times(1)).save(org.mockito.Matchers.refEq(currencyOptionalNotNullOne.get()));
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void createByName_CurrenciesFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findByName(currencyOptionalNotNullOne.get().getName())).thenReturn(currencyOptionalNotNullOne);
        Gson gson = new Gson();
        String jsonString = gson.toJson(currencyOptionalNotNullOne.get());

        mockMvc.perform(MockMvcRequestBuilders.post("/currencies").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        Mockito.verify(serviceMock, Mockito.times(1)).findByName(currencyOptionalNotNullOne.get().getName());
        Mockito.verifyNoMoreInteractions(serviceMock);
    }


    @Before
    public void setUp() throws Exception {
        Currency localCurrency = new Currency();
        localCurrency.setName("USD");
        localCurrency.setId(CURRENCY_ID_LONG);
        localCurrency.setValue(CURRENCY_VALUE_LONG);
        currencyOptionalNotNullOne = Optional.of(localCurrency);

        localCurrency.setName("UAH");
        localCurrency.setId(CURRENCY_ID_LONG + 3);
        localCurrency.setValue(CURRENCY_VALUE_LONG + 123456);
        currencyOptionalNotNullTwo = Optional.of(localCurrency);

        currencyOptionalNull = Optional.ofNullable(null);
    }

    @After
    public void tearDown() throws Exception {

    }
}
