package com.budget.core.rest;

import com.budget.core.entity.Wallet;
import com.budget.core.exception.ObjectAlreadyExists;
import com.budget.core.exception.ObjectNotFoundException;
import com.budget.core.service.WalletService;
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
@WebMvcTest(WalletController.class)
public class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WalletService serviceMock;

    private final static String WALLET_NAME_ONE = "Nany", WALLET_NAME_TWO = "Home", WALLET_THREE_NAME = "Drugs";
    private final static Long WALLET_ONE_ID = 4958L, WALLET_TWO_ID = 111L, WALLET_THREE_ID = 7L, USER_CURRENCY_ONE_ID = Long.MAX_VALUE;
    private Optional<Wallet> walletOptionalNotNullOne, walletOptionalNotNullTwo, walletOptionalNull;

    @Test
    public void findById_WalletFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(WALLET_ONE_ID)).thenReturn(walletOptionalNotNullOne);
        mockMvc.perform(MockMvcRequestBuilders.get("/wallets/{id}", WALLET_ONE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(walletOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.hasToString(walletOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(walletOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userCurrencyId", Matchers.hasToString(walletOptionalNotNullOne.get().getUserCurrencyId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(WALLET_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findById_WalletNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(WALLET_ONE_ID)).thenReturn(walletOptionalNull);
        mockMvc.perform(MockMvcRequestBuilders.get("/wallets/{id}", WALLET_ONE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(WALLET_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findByNameAndCurrencyId_WalletFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.doReturn(Stream.of(walletOptionalNotNullOne.get())).when(serviceMock).findByNameAndUserCurrencyId(WALLET_NAME_ONE, USER_CURRENCY_ONE_ID);
        mockMvc.perform(MockMvcRequestBuilders.get("/wallets/name/{name}/us_cur_id/{id}", WALLET_NAME_ONE, USER_CURRENCY_ONE_ID)
                .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(walletOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.hasToString(walletOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(walletOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userCurrencyId", Matchers.hasToString(walletOptionalNotNullOne.get().getUserCurrencyId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).findByNameAndUserCurrencyId(WALLET_NAME_ONE, USER_CURRENCY_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findByNameAndCurrencyId_WalletNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.doReturn(Stream.of()).when(serviceMock).findByNameAndUserCurrencyId(WALLET_NAME_ONE, USER_CURRENCY_ONE_ID);
        mockMvc.perform(MockMvcRequestBuilders.get("/wallets/name/{name}/us_cur_id/{id}", WALLET_NAME_ONE, USER_CURRENCY_ONE_ID)
                .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findByNameAndUserCurrencyId(WALLET_NAME_ONE, USER_CURRENCY_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void deleteById_WalletIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(WALLET_ONE_ID)).thenReturn(walletOptionalNotNullOne);
        mockMvc.perform(MockMvcRequestBuilders.delete("/wallets/{id}", WALLET_ONE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(walletOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.hasToString(walletOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(walletOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userCurrencyId", Matchers.hasToString(walletOptionalNotNullOne.get().getUserCurrencyId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(WALLET_ONE_ID);
        Mockito.verify(serviceMock, Mockito.times(1)).delete(WALLET_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void deleteById_WalletNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(WALLET_ONE_ID)).thenReturn(walletOptionalNull);
        mockMvc.perform(MockMvcRequestBuilders.delete("/wallets/{id}", WALLET_ONE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(WALLET_ONE_ID);
        Mockito.verify(serviceMock, Mockito.times(0)).delete(WALLET_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateUserCurrencyId_WalletIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Long newId = walletOptionalNotNullTwo.get().getUserCurrencyId() - 1;
        Gson gson = new Gson();
        String jsonString = gson.toJson(walletOptionalNotNullTwo.get());
        Wallet localWallet = new Wallet(walletOptionalNotNullTwo.get().getName(), newId);
        localWallet.setId(WALLET_TWO_ID);
        Mockito.when(serviceMock.updateUserCurrencyId(WALLET_TWO_ID, newId)).thenReturn(localWallet);
        mockMvc.perform(MockMvcRequestBuilders.put("/wallets/{id}/cur_id/{cur_id}", WALLET_TWO_ID, newId).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(walletOptionalNotNullTwo.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.hasToString(walletOptionalNotNullTwo.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(walletOptionalNotNullTwo.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userCurrencyId", Matchers.hasToString(newId.toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).updateUserCurrencyId(WALLET_TWO_ID, newId);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateUserCurrencyId_WalletNotFound_ShouldReturnRightResponseException() throws Exception {
        Long newId = walletOptionalNotNullTwo.get().getUserCurrencyId() - 1;
        Gson gson = new Gson();
        String jsonString = gson.toJson(walletOptionalNotNullTwo.get());
        Mockito.when(serviceMock.updateUserCurrencyId(WALLET_TWO_ID, newId))
                .thenThrow(new ObjectNotFoundException("Exception class: There is no such object"));
        mockMvc.perform(MockMvcRequestBuilders.put("/wallets/{id}/cur_id/{cur_id}", WALLET_TWO_ID, newId).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).updateUserCurrencyId(WALLET_TWO_ID, newId);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateUserCurrencyId_WalletFound_ExactlySame_ShouldReturnRightResponseException() throws Exception {
        Gson gson = new Gson();
        String jsonString = gson.toJson(walletOptionalNotNullTwo.get());
        Long localId = walletOptionalNotNullTwo.get().getUserCurrencyId();
        Mockito.when(serviceMock.updateUserCurrencyId(WALLET_TWO_ID, localId)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(MockMvcRequestBuilders.put(
                "/wallets/{id}/cur_id/{cur_id}", WALLET_TWO_ID, localId).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isConflict());
        Mockito.verify(serviceMock, Mockito.times(1)).updateUserCurrencyId(WALLET_TWO_ID, localId);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateName_WalletIsFound_ShouldReturnRightResponseEntity() throws Exception {
        String newName = walletOptionalNotNullTwo.get().getName() + "XXX";
        Gson gson = new Gson();
        String jsonString = gson.toJson(walletOptionalNotNullTwo.get());
        Wallet localWallet = new Wallet(newName, walletOptionalNotNullTwo.get().getUserCurrencyId());
        localWallet.setId(WALLET_TWO_ID);
        Mockito.when(serviceMock.updateName(WALLET_TWO_ID, newName)).thenReturn(localWallet);
        mockMvc.perform(MockMvcRequestBuilders.put("/wallets/{id}/name/{name}", WALLET_TWO_ID, newName).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(walletOptionalNotNullTwo.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.hasToString(localWallet.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(walletOptionalNotNullTwo.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userCurrencyId", Matchers.hasToString(walletOptionalNotNullTwo.get().getUserCurrencyId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).updateName(WALLET_TWO_ID, newName);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateName_WalletNotFound_ShouldReturnRightResponseException() throws Exception {
        String newName = walletOptionalNotNullTwo.get().getName() + "XXX";
        Gson gson = new Gson();
        String jsonString = gson.toJson(walletOptionalNotNullTwo.get());
        Mockito.when(serviceMock.updateName(WALLET_TWO_ID, newName))
                .thenThrow(new ObjectNotFoundException("Exception class: There is no such object"));
        mockMvc.perform(MockMvcRequestBuilders.put("/wallets/{id}/name/{name}", WALLET_TWO_ID, newName).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).updateName(WALLET_TWO_ID, newName);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateWallet_WalletFound_ExactlySame_ShouldReturnRightResponseException() throws Exception {
        Gson gson = new Gson();
        String jsonString = gson.toJson(walletOptionalNotNullTwo.get());
        String localName = walletOptionalNotNullTwo.get().getName();
        Mockito.when(serviceMock.updateName(WALLET_TWO_ID, localName)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(MockMvcRequestBuilders.put(
                "/wallets/{id}/name/{name}", WALLET_TWO_ID, localName).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isConflict());
        Mockito.verify(serviceMock, Mockito.times(1)).updateName(WALLET_TWO_ID, localName);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void createWallet_WalletIsNotFound_ShouldReturnRightResponseEntity() throws Exception {
        Wallet walletThree = new Wallet(WALLET_THREE_NAME, USER_CURRENCY_ONE_ID);
        walletThree.setId(WALLET_THREE_ID);
        Mockito.when(serviceMock.create(walletThree)).thenReturn(walletThree);
        Gson gson = new Gson();
        String jsonString = gson.toJson(walletThree);
        mockMvc.perform(MockMvcRequestBuilders.post("/wallets").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(walletThree.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.hasToString(walletThree.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(walletThree.getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userCurrencyId", Matchers.hasToString(walletThree.getUserCurrencyId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).create(walletThree);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void createWallet_WalletIsFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.create(walletOptionalNotNullTwo.get())).thenThrow(new ObjectAlreadyExists("Object Wallet already exists."));
        Gson gson = new Gson();
        String jsonString = gson.toJson(walletOptionalNotNullTwo.get());
        mockMvc.perform(MockMvcRequestBuilders.post("/wallets").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isConflict());
        Mockito.verify(serviceMock, Mockito.times(1)).create(walletOptionalNotNullTwo.get());
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Before
    public void setUp() throws Exception {
        Wallet walletOne = new Wallet(WALLET_NAME_ONE, USER_CURRENCY_ONE_ID);
        walletOne.setId(WALLET_ONE_ID);
        walletOptionalNotNullOne = Optional.of(walletOne);

        Wallet walletTwo = new Wallet(WALLET_NAME_TWO, USER_CURRENCY_ONE_ID);
        walletTwo.setId(WALLET_TWO_ID);
        walletOptionalNotNullTwo = Optional.of(walletTwo);

        walletOptionalNull = Optional.ofNullable(null);
    }
}
