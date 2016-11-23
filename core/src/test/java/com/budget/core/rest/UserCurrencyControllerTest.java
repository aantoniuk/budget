package com.budget.core.rest;

import com.budget.core.entity.Currency;
import com.budget.core.entity.User;
import com.budget.core.entity.UserCurrency;
import com.budget.core.service.UserCurrencyService;
import com.google.gson.Gson;
import org.hamcrest.Matchers;
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
import java.util.Optional;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@WebMvcTest(UserCurrencyController.class)
public class UserCurrencyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserCurrencyService serviceMock;

    private final static Long USERCURRENCY_ONE_ID = 909091L, USERCURRENCY_TWO_ID = Long.MAX_VALUE,
            CURRENCY_ONE_ID = 654L, CURRENCY_TWO_ID = Long.MAX_VALUE,
            CURRENCY_ONE_VALUE = 2L, CURRENCY_TWO_VALUE = 666L,
            USER_ID = Long.MAX_VALUE;
    private final static Float USERCURRENCY_ONE_VALUE = 66.6F, USERCURRENCY_TWO_VALUE = Float.MAX_VALUE;
    private final static String USER_LOGIN = "vasyok", USER_PSWD = "qwerty123456", CURRENCY_ONE_NAME = "GBP", CURRENCY_TWO_NAME = "USD";

    private Optional<UserCurrency> userCurrencyOptionalNotNullOne, userCurrencyOptionalNotNullTwo, userCurrencyOptionalNull;
    private User localUserOne;

    @Test
    public void findById_UserCurrencyFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(USERCURRENCY_ONE_ID)).thenReturn(userCurrencyOptionalNotNullOne);
        mockMvc.perform(MockMvcRequestBuilders.get("/usercurrencies/{id}", USERCURRENCY_ONE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(userCurrencyOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value", Matchers.hasToString(userCurrencyOptionalNotNullOne.get().getValue().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(userCurrencyOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currencyId", Matchers.hasToString(userCurrencyOptionalNotNullOne.get().getCurrencyId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.hasToString(userCurrencyOptionalNotNullOne.get().getUserId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USERCURRENCY_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findById_UserCurrencyNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(USERCURRENCY_ONE_ID)).thenReturn(userCurrencyOptionalNull);
        mockMvc.perform(MockMvcRequestBuilders.get("/usercurrencies/{id}", USERCURRENCY_ONE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USERCURRENCY_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findByUserId_UserCurrenciesFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.doReturn(Stream.of(userCurrencyOptionalNotNullOne.get(), userCurrencyOptionalNotNullTwo.get()))
                .when(serviceMock).findByUserId(localUserOne.getId());
        mockMvc.perform(MockMvcRequestBuilders.get("/usercurrencies/by_uid/{id}", localUserOne.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.hasToString(userCurrencyOptionalNotNullOne.get().getId().toString().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].value", Matchers.hasToString(userCurrencyOptionalNotNullOne.get().getValue().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].enable", Matchers.hasToString(userCurrencyOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].currencyId", Matchers.hasToString(userCurrencyOptionalNotNullOne.get().getCurrencyId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userId", Matchers.hasToString(userCurrencyOptionalNotNullOne.get().getUserId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.hasToString(userCurrencyOptionalNotNullTwo.get().getId().toString().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].value", Matchers.hasToString(userCurrencyOptionalNotNullTwo.get().getValue().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].enable", Matchers.hasToString(userCurrencyOptionalNotNullTwo.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].currencyId", Matchers.hasToString(userCurrencyOptionalNotNullTwo.get().getCurrencyId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].userId", Matchers.hasToString(userCurrencyOptionalNotNullTwo.get().getUserId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).findByUserId(localUserOne.getId());
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findByUserId_UserCurrenciesNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.doReturn(Stream.of()).when(serviceMock).findByUserId(localUserOne.getId());
        mockMvc.perform(MockMvcRequestBuilders.get("/usercurrencies/by_uid/{id}", localUserOne.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findByUserId(localUserOne.getId());
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void deleteById_UserCurrencyIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(USERCURRENCY_ONE_ID)).thenReturn(userCurrencyOptionalNotNullOne);
        mockMvc.perform(MockMvcRequestBuilders.delete("/usercurrencies/{id}", USERCURRENCY_ONE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(userCurrencyOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value", Matchers.hasToString(userCurrencyOptionalNotNullOne.get().getValue().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(userCurrencyOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currencyId", Matchers.hasToString(userCurrencyOptionalNotNullOne.get().getCurrencyId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.hasToString(userCurrencyOptionalNotNullOne.get().getUserId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USERCURRENCY_ONE_ID);
        Mockito.verify(serviceMock, Mockito.times(1)).delete(USERCURRENCY_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void deleteById_UserCurrenciesNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(USERCURRENCY_ONE_ID)).thenReturn(userCurrencyOptionalNull);
        mockMvc.perform(MockMvcRequestBuilders.delete("/usercurrencies/{id}", USERCURRENCY_ONE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USERCURRENCY_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateCurrencyId_UserCurrencyIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(USERCURRENCY_TWO_ID)).thenReturn(userCurrencyOptionalNotNullTwo);
        Long newId = userCurrencyOptionalNotNullTwo.get().getCurrencyId() - 1;
        Gson gson = new Gson();
        String jsonString = gson.toJson(userCurrencyOptionalNotNullTwo.get());
        UserCurrency localUserCurrency = new UserCurrency();
        localUserCurrency.setId(USERCURRENCY_TWO_ID);
        localUserCurrency.setValue(USERCURRENCY_TWO_VALUE);
        localUserCurrency.setCurrencyId(newId);
        localUserCurrency.setUserId(localUserOne.getId());
        Mockito.when(serviceMock.updateCurrencyId(USERCURRENCY_TWO_ID, newId)).thenReturn(localUserCurrency);
        mockMvc.perform(MockMvcRequestBuilders.put("/usercurrencies/{id}/cur_id/{cur_id}", USERCURRENCY_TWO_ID, newId).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(localUserCurrency.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value", Matchers.hasToString(localUserCurrency.getValue().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(localUserCurrency.getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currencyId", Matchers.hasToString(localUserCurrency.getCurrencyId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.hasToString(localUserCurrency.getUserId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USERCURRENCY_TWO_ID);
        Mockito.verify(serviceMock, Mockito.times(1)).updateCurrencyId(USERCURRENCY_TWO_ID, newId);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateCurrencyId_UserCurrenciesNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(USERCURRENCY_TWO_ID)).thenReturn(userCurrencyOptionalNull);
        Long newId = userCurrencyOptionalNotNullTwo.get().getCurrencyId() - 1;
        Gson gson = new Gson();
        String jsonString = gson.toJson(userCurrencyOptionalNotNullTwo.get());
        mockMvc.perform(MockMvcRequestBuilders.put("/usercurrencies/{id}/cur_id/{cur_id}", USERCURRENCY_TWO_ID, newId).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USERCURRENCY_TWO_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateCurrencyId_UserCurrenciesFound_ExactlySame_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(USERCURRENCY_TWO_ID)).thenReturn(userCurrencyOptionalNotNullTwo);
        Gson gson = new Gson();
        String jsonString = gson.toJson(userCurrencyOptionalNotNullTwo.get());
        Long localId = userCurrencyOptionalNotNullTwo.get().getCurrencyId();
        mockMvc.perform(MockMvcRequestBuilders.put(
                "/usercurrencies/{id}/cur_id/{cur_id}", USERCURRENCY_TWO_ID, localId).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isConflict());
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USERCURRENCY_TWO_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateValue_UserCurrencyIsFound_NotSame_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(USERCURRENCY_TWO_ID)).thenReturn(userCurrencyOptionalNotNullTwo);
        Float newValue = userCurrencyOptionalNotNullOne.get().getValue() + 1;
        Gson gson = new Gson();
        String jsonString = gson.toJson(userCurrencyOptionalNotNullTwo.get());

        UserCurrency localUserCurrency = new UserCurrency();
        localUserCurrency.setId(USERCURRENCY_TWO_ID);
        localUserCurrency.setValue(newValue);
        localUserCurrency.setCurrencyId(userCurrencyOptionalNotNullTwo.get().getCurrencyId());
        localUserCurrency.setUserId(userCurrencyOptionalNotNullTwo.get().getUserId());

        Mockito.when(serviceMock.updateValue(localUserCurrency)).thenReturn(localUserCurrency);
        mockMvc.perform(MockMvcRequestBuilders.put("/usercurrencies/{id}/value/{value}", USERCURRENCY_TWO_ID, newValue).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(localUserCurrency.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value", Matchers.hasToString(localUserCurrency.getValue().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(localUserCurrency.getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currencyId", Matchers.hasToString(localUserCurrency.getCurrencyId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.hasToString(localUserCurrency.getUserId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USERCURRENCY_TWO_ID);
        Mockito.verify(serviceMock, Mockito.times(1)).updateValue(localUserCurrency);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateValue_UserCurrenciesNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(USERCURRENCY_TWO_ID)).thenReturn(userCurrencyOptionalNull);
        Float newValue = userCurrencyOptionalNotNullTwo.get().getValue() - 100;
        Gson gson = new Gson();
        String jsonString = gson.toJson(userCurrencyOptionalNotNullTwo.get());
        mockMvc.perform(MockMvcRequestBuilders.put("/usercurrencies/{id}/value/{value}", USERCURRENCY_TWO_ID, newValue).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USERCURRENCY_TWO_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateValue_UserCurrenciesFound_ExactlySame_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(USERCURRENCY_TWO_ID)).thenReturn(userCurrencyOptionalNotNullTwo);
        Gson gson = new Gson();
        String jsonString = gson.toJson(userCurrencyOptionalNotNullTwo.get());
        Float localValue = userCurrencyOptionalNotNullTwo.get().getValue();
        mockMvc.perform(MockMvcRequestBuilders.put(
                "/usercurrencies/{id}/value/{value}", USERCURRENCY_TWO_ID, localValue).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isConflict());
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USERCURRENCY_TWO_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void createUserCurrency_UserCurrencyIsNotFound_ShouldReturnRightResponseEntity() throws Exception {
        Currency localCurrencyThree = new Currency(CURRENCY_TWO_NAME.toLowerCase(), CURRENCY_TWO_VALUE + 2);
        localCurrencyThree.setId(CURRENCY_TWO_ID  - 2);
        UserCurrency localUserCurrencyThree = new UserCurrency();
        localUserCurrencyThree.setId(USERCURRENCY_ONE_ID + 1000);
        localUserCurrencyThree.setValue(USERCURRENCY_ONE_VALUE * 2);
        localUserCurrencyThree.setUserId(localUserOne.getId());
        localUserCurrencyThree.setCurrencyId(localCurrencyThree.getId());

        Mockito.when(serviceMock.save(localUserCurrencyThree)).thenReturn(localUserCurrencyThree);
        Gson gson = new Gson();
        String jsonString = gson.toJson(localUserCurrencyThree);
        mockMvc.perform(MockMvcRequestBuilders.post("/usercurrencies").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(localUserCurrencyThree.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value", Matchers.hasToString(localUserCurrencyThree.getValue().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(localUserCurrencyThree.getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currencyId", Matchers.hasToString(localUserCurrencyThree.getCurrencyId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.hasToString(localUserCurrencyThree.getUserId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).save(localUserCurrencyThree);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void createUserCurrency_UserCurrenciesFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.save(userCurrencyOptionalNotNullTwo.get())).thenThrow(new IllegalArgumentException());
        Gson gson = new Gson();
        String jsonString = gson.toJson(userCurrencyOptionalNotNullTwo.get());
        mockMvc.perform(MockMvcRequestBuilders.post("/usercurrencies").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isConflict());
        Mockito.verify(serviceMock, Mockito.times(1)).save(userCurrencyOptionalNotNullTwo.get());
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Before
    public void setUp() throws Exception {
        localUserOne = new User(USER_LOGIN, USER_PSWD);
        localUserOne.setId(USER_ID);
        Currency localCurrencyOne = new Currency(CURRENCY_ONE_NAME, CURRENCY_ONE_VALUE);
        localCurrencyOne.setId(CURRENCY_ONE_ID);
        Currency localCurrencyTwo = new Currency(CURRENCY_TWO_NAME, CURRENCY_TWO_VALUE);
        localCurrencyTwo.setId(CURRENCY_TWO_ID);

        UserCurrency localUserCurrencyOne = new UserCurrency();
        localUserCurrencyOne.setId(USERCURRENCY_ONE_ID);
        localUserCurrencyOne.setValue(USERCURRENCY_ONE_VALUE);
        localUserCurrencyOne.setUserId(localUserOne.getId());
        localUserCurrencyOne.setCurrencyId(localCurrencyOne.getId());

        UserCurrency localUserCurrencyTwo = new UserCurrency();
        localUserCurrencyTwo.setId(USERCURRENCY_TWO_ID);
        localUserCurrencyTwo.setValue(USERCURRENCY_TWO_VALUE);
        localUserCurrencyTwo.setUserId(localUserOne.getId());
        localUserCurrencyTwo.setCurrencyId(localCurrencyTwo.getId());

        userCurrencyOptionalNotNullOne = Optional.of(localUserCurrencyOne);
        userCurrencyOptionalNotNullTwo = Optional.of(localUserCurrencyTwo);
        userCurrencyOptionalNull = Optional.ofNullable(null);
    }
}
