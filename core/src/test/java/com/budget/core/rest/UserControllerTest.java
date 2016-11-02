package com.budget.core.rest;

import com.budget.core.entity.User;
import com.budget.core.service.UserService;
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
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService serviceMock;

    private final static long USER_ID_LONG = 946L;
    private final static String USER_LOGIN = "Master";

    Optional<User> userOptionalNotNullOne, userOptionalNotNullTwo, userOptionalNull;

    @Test
    public void findById_UserFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(USER_ID_LONG)).thenReturn(userOptionalNotNullOne);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", USER_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is((int) userOptionalNotNullOne.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login", Matchers.is(userOptionalNotNullOne.get().getLogin())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.is(userOptionalNotNullOne.get().isEnable())));

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USER_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findById_UserNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(USER_ID_LONG)).thenReturn(userOptionalNull);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", USER_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USER_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findAll_UsersFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findAll()).thenReturn(Arrays.asList(userOptionalNotNullOne.get(), userOptionalNotNullTwo.get()));

        mockMvc.perform(MockMvcRequestBuilders.get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is((int) userOptionalNotNullOne.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].login", Matchers.is(userOptionalNotNullOne.get().getLogin())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].enable", Matchers.is(userOptionalNotNullOne.get().isEnable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is((int) userOptionalNotNullTwo.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].login", Matchers.is(userOptionalNotNullTwo.get().getLogin())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].enable", Matchers.is(userOptionalNotNullTwo.get().isEnable())));

        Mockito.verify(serviceMock, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findAll_UsersNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(serviceMock, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void deleteById_UserIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(USER_ID_LONG)).thenReturn(userOptionalNotNullOne);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", USER_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is((int) userOptionalNotNullOne.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login", Matchers.is(userOptionalNotNullOne.get().getLogin())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.is(userOptionalNotNullOne.get().isEnable())));

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USER_ID_LONG);
        Mockito.verify(serviceMock, Mockito.times(1)).delete(USER_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void deleteById_UsersNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(USER_ID_LONG)).thenReturn(userOptionalNull);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", USER_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USER_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateById_UserIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(USER_ID_LONG)).thenReturn(userOptionalNotNullOne);
        userOptionalNotNullOne.get().setLogin("slave");
        Gson gson = new Gson();
        String jsonString = gson.toJson(userOptionalNotNullOne.get());

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", USER_ID_LONG).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is((int) userOptionalNotNullOne.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login", Matchers.is(userOptionalNotNullOne.get().getLogin())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.is(userOptionalNotNullOne.get().isEnable())));

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USER_ID_LONG);
        Mockito.verify(serviceMock, Mockito.times(1)).update(org.mockito.Matchers.refEq(userOptionalNotNullOne.get()));
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateById_UsersNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(USER_ID_LONG)).thenReturn(userOptionalNull);
        Gson gson = new Gson();
        String jsonString = gson.toJson(userOptionalNotNullOne.get());

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", USER_ID_LONG).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USER_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void createByName_UserIsNotFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findByLogin(userOptionalNotNullOne.get().getLogin())).thenReturn(userOptionalNull);
        Gson gson = new Gson();
        String jsonString = gson.toJson(userOptionalNotNullOne.get());

        mockMvc.perform(MockMvcRequestBuilders.post("/users").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is((int) userOptionalNotNullOne.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login", Matchers.is(userOptionalNotNullOne.get().getLogin())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.is(userOptionalNotNullOne.get().isEnable())));

        Mockito.verify(serviceMock, Mockito.times(1)).findByLogin(userOptionalNotNullOne.get().getLogin());
        Mockito.verify(serviceMock, Mockito.times(1)).update(org.mockito.Matchers.refEq(userOptionalNotNullOne.get()));
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void createByName_UserFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findByLogin(userOptionalNotNullOne.get().getLogin())).thenReturn(userOptionalNotNullOne);
        Gson gson = new Gson();
        String jsonString = gson.toJson(userOptionalNotNullOne.get());

        mockMvc.perform(MockMvcRequestBuilders.post("/users").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        Mockito.verify(serviceMock, Mockito.times(1)).findByLogin(userOptionalNotNullOne.get().getLogin());
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Before
    public void setUp() throws Exception {
        User localUser = new User();
        localUser.setId(USER_ID_LONG);
        localUser.setLogin(USER_LOGIN);
        localUser.setEnable(true);
        userOptionalNotNullOne = Optional.of(localUser);

        localUser.setId(USER_ID_LONG - 9L);
        localUser.setLogin(USER_LOGIN.toLowerCase());
        userOptionalNotNullTwo = Optional.of(localUser);

        userOptionalNull = Optional.ofNullable(null);
    }

    @After
    public void tearDown() throws Exception {

    }
}