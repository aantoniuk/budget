package com.budget.core.rest;

import com.budget.core.entity.User;
import com.budget.core.service.UserService;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService serviceMock;

    private final static Long USER_ONE_ID = 946L, USER_TWO_ID = Long.MAX_VALUE, USER_THREE_ID = 99999L;
    private final static String USER_ONE_LOGIN = "Master", USER_ONE_PSWD = "qwert6",
                                USER_TWO_LOGIN = "@5h18", USER_TWO_PSWD = "1qaz2wsx",
                                USER_THREE_LOGIN = "blabl34", USER_THREE_PSWD = "ldj0;,,,!";

    Optional<User> userOptionalNotNullOne, userOptionalNotNullTwo, userOptionalNull;

    @Test
    public void findById_UserFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(USER_ONE_ID)).thenReturn(userOptionalNotNullOne);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", USER_ONE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(userOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login", Matchers.is(userOptionalNotNullOne.get().getLogin())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.is(userOptionalNotNullOne.get().getEnable())));
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USER_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findById_UserNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(USER_ONE_ID)).thenReturn(userOptionalNull);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", USER_ONE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USER_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findByLogin_UserFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findByLogin(USER_TWO_LOGIN)).thenReturn(userOptionalNotNullTwo);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/login/{login}", USER_TWO_LOGIN).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(userOptionalNotNullTwo.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login", Matchers.is(userOptionalNotNullTwo.get().getLogin())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.is(userOptionalNotNullTwo.get().getEnable())));
        Mockito.verify(serviceMock, Mockito.times(1)).findByLogin(USER_TWO_LOGIN);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findByLogin_UserNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findByLogin(USER_TWO_LOGIN)).thenReturn(userOptionalNull);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/login/{login}", USER_TWO_LOGIN).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findByLogin(USER_TWO_LOGIN);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void deleteById_UserIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(USER_ONE_ID)).thenReturn(userOptionalNotNullOne);
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", USER_ONE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(userOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login", Matchers.is(userOptionalNotNullOne.get().getLogin())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.is(userOptionalNotNullOne.get().getEnable())));
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USER_ONE_ID);
        Mockito.verify(serviceMock, Mockito.times(1)).delete(USER_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void deleteById_UsersNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.doThrow(new NullPointerException()).when(serviceMock).delete(USER_ONE_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", USER_ONE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(USER_ONE_ID);
        Mockito.verify(serviceMock, Mockito.times(1)).delete(USER_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateUserPassword_UserIdFound_ShouldReturnRightResponseEntity() throws Exception {
        String newPassword = userOptionalNotNullOne.get().getPassword().toUpperCase();
        User localUser = new User(USER_ONE_LOGIN, newPassword);
        localUser.setId(USER_ONE_ID);
        Gson gson = new Gson();
        String jsonString = gson.toJson(localUser);
        Mockito.when(serviceMock.updatePassword(USER_ONE_ID, newPassword)).thenReturn(localUser);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}/pswd/{pswd}", USER_ONE_ID, newPassword)
                .accept(MediaType.APPLICATION_JSON).content(jsonString)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(localUser.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login", Matchers.is(localUser.getLogin())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password", Matchers.is(newPassword)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.is(localUser.getEnable())));
        Mockito.verify(serviceMock, Mockito.times(0)).findOne(USER_ONE_ID);
        Mockito.verify(serviceMock, Mockito.times(1)).updatePassword(USER_ONE_ID, newPassword);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateUserPassword_UsersNotFound_ShouldReturnRightResponseException() throws Exception {
        String newPassword = userOptionalNotNullOne.get().getPassword().toUpperCase();
        User localUser = new User(USER_ONE_LOGIN, newPassword);
        localUser.setId(USER_ONE_ID + 1);
        Gson gson = new Gson();
        String jsonString = gson.toJson(localUser);
        Mockito.when(serviceMock.updatePassword(localUser.getId(), newPassword)).thenThrow(new NullPointerException());
        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}/pswd/{pswd}", localUser.getId(), newPassword)
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(0)).findOne(USER_ONE_ID);
        Mockito.verify(serviceMock, Mockito.times(1)).updatePassword(localUser.getId(), newPassword);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateEnable_UserIdFound_ShouldReturnRightResponseEntity() throws Exception {
        Boolean newEnableStatus = ! userOptionalNotNullTwo.get().getEnable();
        User localUser = new User(USER_TWO_LOGIN, USER_TWO_PSWD);
        localUser.setId(USER_TWO_ID);
        localUser.setEnable(newEnableStatus);
        Gson gson = new Gson();
        String jsonString = gson.toJson(localUser);
        Mockito.when(serviceMock.updateEnable(USER_TWO_ID, newEnableStatus)).thenReturn(localUser);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}/enable/{enable}", USER_TWO_ID, newEnableStatus)
                .accept(MediaType.APPLICATION_JSON).content(jsonString)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(localUser.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login", Matchers.is(localUser.getLogin())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.is(newEnableStatus)));
        Mockito.verify(serviceMock, Mockito.times(0)).findOne(USER_TWO_ID);
        Mockito.verify(serviceMock, Mockito.times(1)).updateEnable(USER_TWO_ID, newEnableStatus);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateEnable_UsersNotFound_ShouldReturnRightResponseException() throws Exception {
        Boolean newEnableStatus = ! userOptionalNotNullTwo.get().getEnable();
        User localUser = new User(USER_TWO_LOGIN, USER_TWO_PSWD);
        localUser.setId(USER_TWO_ID - 10);
        localUser.setEnable(newEnableStatus);
        Gson gson = new Gson();
        String jsonString = gson.toJson(localUser);
        Mockito.when(serviceMock.updateEnable(localUser.getId(), newEnableStatus)).thenThrow(new NullPointerException());
        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}/enable/{enable}", localUser.getId(), newEnableStatus)
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(0)).findOne(USER_ONE_ID);
        Mockito.verify(serviceMock, Mockito.times(1)).updateEnable(localUser.getId(), newEnableStatus);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void createUser_UserIsNotFound_ShouldReturnRightResponseEntity() throws Exception {
        User localUser = new User(USER_THREE_LOGIN, USER_THREE_PSWD);
        localUser.setId(USER_THREE_ID);
        Gson gson = new Gson();
        String jsonString = gson.toJson(localUser);
        Mockito.when(serviceMock.create(localUser)).thenReturn(localUser);
        mockMvc.perform(MockMvcRequestBuilders.post("/users").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(localUser.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login", Matchers.is(localUser.getLogin())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.is(localUser.getEnable())));
        Mockito.verify(serviceMock, Mockito.times(0)).findByLogin(localUser.getLogin());
        Mockito.verify(serviceMock, Mockito.times(1)).create(org.mockito.Matchers.refEq(localUser));
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void createUer_UserFound_ShouldReturnRightResponseException() throws Exception {
        Gson gson = new Gson();
        String jsonString = gson.toJson(userOptionalNotNullOne.get());
        Mockito.when(serviceMock.create(userOptionalNotNullOne.get())).thenThrow(new IllegalArgumentException());
        mockMvc.perform(MockMvcRequestBuilders.post("/users").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isConflict());
        Mockito.verify(serviceMock, Mockito.times(0)).findByLogin(userOptionalNotNullOne.get().getLogin());
        Mockito.verify(serviceMock, Mockito.times(1)).create(org.mockito.Matchers.refEq(userOptionalNotNullOne.get()));
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Before
    public void setUp() throws Exception {
        User localUserOne = new User(USER_ONE_LOGIN, USER_ONE_PSWD);
        localUserOne.setId(USER_ONE_ID);
        userOptionalNotNullOne = Optional.of(localUserOne);
        User localUserTwo = new User(USER_TWO_LOGIN, USER_TWO_PSWD);
        localUserTwo.setId(USER_TWO_ID);
        userOptionalNotNullTwo = Optional.of(localUserTwo);
        userOptionalNull = Optional.ofNullable(null);
    }
}