package com.budget.core.rest;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.User;
import com.budget.core.entity.UserCategory;
import com.budget.core.service.UserCategoryService;
import com.google.gson.Gson;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
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
@WebMvcTest(UserCategoryController.class)
public class UserCategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserCategoryService serviceMock;

    private final static String USER_ONE_NAME = "test", USER_ONE_PWD = "#$1G", BASE_UC_NAME = "Fun", UC_NAME = "Games", UC_NEW_NAME = "Sex";
    private final static Long USER_ONE_ID = Long.MAX_VALUE, BASE_UC_ID = 1000L, BASEUC_TWO_ID = 617L, UC_ID = 10001L;

    private Optional<UserCategory> baseUserCategoryOptionalNotNullOne, userCategoryOptionalNotNullOne, userCategoryOptionalNull;

    @Test
    public void findById_UserCategoryFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(BASE_UC_ID)).thenReturn(baseUserCategoryOptionalNotNullOne);
        mockMvc.perform(MockMvcRequestBuilders.get("/usercategories/{id}", BASE_UC_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(baseUserCategoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(baseUserCategoryOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(baseUserCategoryOptionalNotNullOne.get().getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(baseUserCategoryOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parentId", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.hasToString(baseUserCategoryOptionalNotNullOne.get().getUserId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].id", Matchers.hasToString(userCategoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].name", Matchers.is(userCategoryOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].type", Matchers.is(userCategoryOptionalNotNullOne.get().getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].enable", Matchers.hasToString(userCategoryOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].parentId", Matchers.hasToString(baseUserCategoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].userId", Matchers.hasToString(userCategoryOptionalNotNullOne.get().getUserId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(BASE_UC_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findById_UserCategoryNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(BASE_UC_ID)).thenReturn(userCategoryOptionalNull);
        mockMvc.perform(MockMvcRequestBuilders.get("/usercategories/{id}", BASE_UC_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(BASE_UC_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findByType_UserCategoryFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findByType(USER_ONE_ID, OperationType.DEBIT)).thenReturn(Stream.of(baseUserCategoryOptionalNotNullOne.get(), userCategoryOptionalNotNullOne.get()));
        mockMvc.perform(MockMvcRequestBuilders.get("/usercategories/uid/{id}/uctype/{type}", USER_ONE_ID, OperationType.DEBIT.name())
                .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.hasToString(baseUserCategoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(baseUserCategoryOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].type", Matchers.is(baseUserCategoryOptionalNotNullOne.get().getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].enable", Matchers.hasToString(baseUserCategoryOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].parentId", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userId", Matchers.hasToString(baseUserCategoryOptionalNotNullOne.get().getUserId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].children[0].id", Matchers.hasToString(userCategoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].children[0].name", Matchers.is(userCategoryOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].children[0].type", Matchers.is(userCategoryOptionalNotNullOne.get().getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].children[0].enable", Matchers.hasToString(userCategoryOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].children[0].parentId", Matchers.hasToString(baseUserCategoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].children[0].userId", Matchers.hasToString(userCategoryOptionalNotNullOne.get().getUserId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.hasToString(userCategoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is(userCategoryOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].type", Matchers.is(userCategoryOptionalNotNullOne.get().getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].enable", Matchers.hasToString(userCategoryOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].parentId", Matchers.hasToString(baseUserCategoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].userId", Matchers.hasToString(userCategoryOptionalNotNullOne.get().getUserId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).findByType(USER_ONE_ID, OperationType.DEBIT);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findByType_UserCategoryNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findByType(USER_ONE_ID, OperationType.CREDIT)).thenReturn(Stream.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/usercategories/uid/{id}/uctype/{type}", USER_ONE_ID, OperationType.CREDIT.name())
                .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findByType(USER_ONE_ID, OperationType.CREDIT);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findByUserAndParentId_UserCategoryFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findByUserIdAndParentId(USER_ONE_ID, BASE_UC_ID)).thenReturn(Stream.of(userCategoryOptionalNotNullOne.get()));
        mockMvc.perform(MockMvcRequestBuilders.get("/usercategories/uid/{id}/parentid/{parentid}", USER_ONE_ID, BASE_UC_ID)
                .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.hasToString(userCategoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(userCategoryOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].type", Matchers.is(userCategoryOptionalNotNullOne.get().getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].enable", Matchers.hasToString(userCategoryOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].parentId", Matchers.hasToString(baseUserCategoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userId", Matchers.hasToString(userCategoryOptionalNotNullOne.get().getUserId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).findByUserIdAndParentId(USER_ONE_ID, BASE_UC_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findByUserAndParentId_UserCategoryNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findByUserIdAndParentId(USER_ONE_ID, BASE_UC_ID)).thenReturn(Stream.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/usercategories/uid/{id}/parentid/{parentid}", USER_ONE_ID, BASE_UC_ID)
                .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findByUserIdAndParentId(USER_ONE_ID, BASE_UC_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void create_UserCategoryIsNotFound_ShouldReturnRightResponseEntity() throws Exception {
        UserCategory localBaseUserCategoryTwo = new UserCategory("Food", OperationType.CREDIT, true, null, USER_ONE_ID);
        localBaseUserCategoryTwo.setId(BASEUC_TWO_ID);
        Mockito.when(serviceMock.create(localBaseUserCategoryTwo)).thenReturn(localBaseUserCategoryTwo);
        Gson gson = new Gson();
        String jsonString = gson.toJson(localBaseUserCategoryTwo);
        mockMvc.perform(MockMvcRequestBuilders.post("/usercategories/create").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(BASEUC_TWO_ID.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(localBaseUserCategoryTwo.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(localBaseUserCategoryTwo.getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(localBaseUserCategoryTwo.getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parentId", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.hasToString(USER_ONE_ID.toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).create(org.mockito.Matchers.refEq(localBaseUserCategoryTwo));
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void createWithWrongValues_UserCategoryNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.create(baseUserCategoryOptionalNotNullOne.get())).thenThrow(new IllegalArgumentException(""));
        Gson gson = new Gson();
        String jsonString = gson.toJson(baseUserCategoryOptionalNotNullOne.get());
        mockMvc.perform(MockMvcRequestBuilders.post("/usercategories/create").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isConflict());
        Mockito.verify(serviceMock, Mockito.times(1)).create(baseUserCategoryOptionalNotNullOne.get());
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void createWithSomethingEmpty_UserCategoryNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.create(baseUserCategoryOptionalNotNullOne.get())).thenThrow(new NullPointerException());
        Gson gson = new Gson();
        String jsonString = gson.toJson(baseUserCategoryOptionalNotNullOne.get());
        mockMvc.perform(MockMvcRequestBuilders.post("/usercategories/create").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).create(baseUserCategoryOptionalNotNullOne.get());
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void deleteCategory_UserCategoryIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(BASE_UC_ID)).thenReturn(baseUserCategoryOptionalNotNullOne);
        mockMvc.perform(MockMvcRequestBuilders.delete("/usercategories/{id}", BASE_UC_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(baseUserCategoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(baseUserCategoryOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(baseUserCategoryOptionalNotNullOne.get().getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(baseUserCategoryOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parentId", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.hasToString(baseUserCategoryOptionalNotNullOne.get().getUserId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].id", Matchers.hasToString(userCategoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].name", Matchers.is(userCategoryOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].type", Matchers.is(userCategoryOptionalNotNullOne.get().getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].enable", Matchers.hasToString(userCategoryOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].parentId", Matchers.hasToString(baseUserCategoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].userId", Matchers.hasToString(userCategoryOptionalNotNullOne.get().getUserId().toString())));;
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(BASE_UC_ID);
        Mockito.verify(serviceMock, Mockito.times(1)).delete(BASE_UC_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void deleteCategory_UserCategoryNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(BASE_UC_ID)).thenReturn(userCategoryOptionalNull);
        mockMvc.perform(MockMvcRequestBuilders.delete("/usercategories/{id}", BASE_UC_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(BASE_UC_ID);
        Mockito.verify(serviceMock, Mockito.times(0)).delete(BASE_UC_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateName_UserCategoryIsFound_ShouldReturnRightResponseEntity() throws Exception {
        UserCategory localCategoryOne = userCategoryOptionalNotNullOne.get();
        UserCategory localCategoryOneNew = new UserCategory(UC_NEW_NAME, localCategoryOne.getType(),
                localCategoryOne.getEnable(), localCategoryOne.getParentId(), USER_ONE_ID);
        localCategoryOneNew.setId(localCategoryOne.getId());
        Mockito.when(serviceMock.updateName(UC_ID, UC_NEW_NAME)).thenReturn(localCategoryOneNew);
        mockMvc.perform(MockMvcRequestBuilders.put("/usercategories/update/{id}/name/{name}", UC_ID, UC_NEW_NAME)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(localCategoryOne.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(UC_NEW_NAME)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(localCategoryOne.getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(localCategoryOne.getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parentId", Matchers.hasToString(localCategoryOne.getParentId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.hasToString(localCategoryOne.getUserId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).updateName(UC_ID, UC_NEW_NAME);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateNameWithIllegalParam_UserCategoryIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.updateName(UC_ID, UC_NEW_NAME)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(MockMvcRequestBuilders.put("/usercategories/update/{id}/name/{name}", UC_ID, UC_NEW_NAME)
                .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isConflict());
        Mockito.verify(serviceMock, Mockito.times(1)).updateName(UC_ID, UC_NEW_NAME);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateNameWithIllegalParam_UserCategoryNotFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.updateName(UC_ID, UC_NEW_NAME)).thenThrow(new NullPointerException());
        mockMvc.perform(MockMvcRequestBuilders.put("/usercategories/update/{id}/name/{name}", UC_ID, UC_NEW_NAME)
                .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).updateName(UC_ID, UC_NEW_NAME);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateType_UserCategoryIsFound_ShouldReturnRightResponseEntity() throws Exception {
        UserCategory localUserCategoryOne = userCategoryOptionalNotNullOne.get();
        UserCategory localBaseUserCategoryOne = baseUserCategoryOptionalNotNullOne.get();
        UserCategory updatedBaseUserCategoryOne = new UserCategory(localBaseUserCategoryOne.getName(), OperationType.CREDIT,
                localBaseUserCategoryOne.getEnable(), localBaseUserCategoryOne.getParentId(), USER_ONE_ID);
        updatedBaseUserCategoryOne.setId(BASE_UC_ID);
        UserCategory updatedUserCategoryOne = new UserCategory(localUserCategoryOne.getName(), OperationType.CREDIT,
                localUserCategoryOne.getEnable(), localUserCategoryOne.getParentId(), USER_ONE_ID);
        updatedUserCategoryOne.setId(localUserCategoryOne.getId());
        updatedBaseUserCategoryOne.setChildren(Sets.newSet(updatedUserCategoryOne));
        Mockito.when(serviceMock.updateType(BASE_UC_ID, OperationType.CREDIT)).thenReturn(updatedBaseUserCategoryOne);
        mockMvc.perform(MockMvcRequestBuilders.put("/usercategories/update/{id}/type/{type}", BASE_UC_ID, OperationType.CREDIT.name())
                .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(localBaseUserCategoryOne.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(localBaseUserCategoryOne.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(updatedBaseUserCategoryOne.getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(localBaseUserCategoryOne.getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parentId", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.hasToString(localBaseUserCategoryOne.getUserId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].id", Matchers.hasToString(localUserCategoryOne.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].name", Matchers.is(localUserCategoryOne.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].type", Matchers.is(OperationType.CREDIT.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].enable", Matchers.hasToString(localUserCategoryOne.getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].parentId", Matchers.hasToString(localBaseUserCategoryOne.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].userId", Matchers.hasToString(localUserCategoryOne.getUserId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).updateType(BASE_UC_ID, OperationType.CREDIT);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateTypeOfSubcategory_UserCategoryIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.updateType(UC_ID, OperationType.CREDIT)).thenThrow(new UnsupportedOperationException());
        mockMvc.perform(MockMvcRequestBuilders.put("/usercategories/update/{id}/type/{type}", UC_ID, OperationType.CREDIT.name())
                .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verify(serviceMock, Mockito.times(1)).updateType(UC_ID, OperationType.CREDIT);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateEnable_UserCategoryIsFound_ShouldReturnRightResponseEntity() throws Exception {
           UserCategory localUserCategoryOne = userCategoryOptionalNotNullOne.get();
           UserCategory localBaseUserCategoryOne = baseUserCategoryOptionalNotNullOne.get();
           UserCategory updatedBaseUserCategoryOne = new UserCategory(localBaseUserCategoryOne.getName(), localBaseUserCategoryOne.getType(),
                   !localBaseUserCategoryOne.getEnable(), localBaseUserCategoryOne.getParentId(), USER_ONE_ID);
           updatedBaseUserCategoryOne.setId(BASE_UC_ID);
           UserCategory updatedUserCategoryOne = new UserCategory(localUserCategoryOne.getName(), localUserCategoryOne.getType(),
                   !localUserCategoryOne.getEnable(), localUserCategoryOne.getParentId(), USER_ONE_ID);
           updatedUserCategoryOne.setId(localUserCategoryOne.getId());
           updatedBaseUserCategoryOne.setChildren(Sets.newSet(updatedUserCategoryOne));
           Mockito.when(serviceMock.updateEnable(BASE_UC_ID, !localBaseUserCategoryOne.getEnable())).thenReturn(updatedBaseUserCategoryOne);
           mockMvc.perform(MockMvcRequestBuilders.put("/usercategories/update/{id}/enable/{enable}",
                   BASE_UC_ID, String.valueOf(!localBaseUserCategoryOne.getEnable()))
                   .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
                   .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(updatedBaseUserCategoryOne.getId().toString())))
                   .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(updatedBaseUserCategoryOne.getName())))
                   .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(updatedBaseUserCategoryOne.getType().name())))
                   .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(updatedBaseUserCategoryOne.getEnable().toString())))
                   .andExpect(MockMvcResultMatchers.jsonPath("$.parentId", Matchers.isEmptyOrNullString()))
                   .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.hasToString(updatedBaseUserCategoryOne.getUserId().toString())))
                   .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].id", Matchers.hasToString(updatedUserCategoryOne.getId().toString())))
                   .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].name", Matchers.is(updatedUserCategoryOne.getName())))
                   .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].type", Matchers.is(updatedUserCategoryOne.getType().name())))
                   .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].enable", Matchers.hasToString(updatedUserCategoryOne.getEnable().toString())))
                   .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].parentId", Matchers.hasToString(updatedBaseUserCategoryOne.getId().toString())))
                   .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].userId", Matchers.hasToString(updatedUserCategoryOne.getUserId().toString())));
           Mockito.verify(serviceMock, Mockito.times(1)).updateEnable(BASE_UC_ID, !localBaseUserCategoryOne.getEnable());
           Mockito.verifyNoMoreInteractions(serviceMock);
       }

    @Test
    public void updateParentId_UserCategoryIsFound_ShouldReturnRightResponseEntity() throws Exception {
        UserCategory localUserCategoryOne = userCategoryOptionalNotNullOne.get();
        UserCategory localBaseUserCategoryOne = baseUserCategoryOptionalNotNullOne.get();
        UserCategory updatedBaseUserCategoryOne = new UserCategory(localBaseUserCategoryOne.getName(), localBaseUserCategoryOne.getType(),
                localBaseUserCategoryOne.getEnable(), localBaseUserCategoryOne.getParentId(), USER_ONE_ID);
        updatedBaseUserCategoryOne.setId(BASEUC_TWO_ID);
        UserCategory updatedUserCategoryOne = new UserCategory(localUserCategoryOne.getName(), localUserCategoryOne.getType(),
                localUserCategoryOne.getEnable(), BASEUC_TWO_ID, USER_ONE_ID);
        updatedUserCategoryOne.setId(localUserCategoryOne.getId());
        updatedBaseUserCategoryOne.setChildren(Sets.newSet());
        Mockito.when(serviceMock.updateParent(UC_ID, BASEUC_TWO_ID)).thenReturn(updatedUserCategoryOne);
        mockMvc.perform(MockMvcRequestBuilders.put("/usercategories/update/{id}/parentid/{parentid}", UC_ID, BASEUC_TWO_ID)
                .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(localUserCategoryOne.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(localUserCategoryOne.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(localUserCategoryOne.getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(localUserCategoryOne.getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parentId",Matchers.hasToString(updatedUserCategoryOne.getParentId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId",Matchers.hasToString(updatedUserCategoryOne.getUserId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).updateParent(UC_ID, BASEUC_TWO_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateParentIdWrong_UserCategoryIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.updateParent(UC_ID, BASEUC_TWO_ID)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(MockMvcRequestBuilders.put("/usercategories/update/{id}/parentid/{parentid}", UC_ID, BASEUC_TWO_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict());
        Mockito.verify(serviceMock, Mockito.times(1)).updateParent(UC_ID, BASEUC_TWO_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Before
    public void setUp() throws Exception {
        User localUser = new User(USER_ONE_NAME, USER_ONE_PWD);
        localUser.setId(USER_ONE_ID);

        UserCategory localBaseUserCategoryOne =
                new UserCategory(BASE_UC_NAME, OperationType.DEBIT, true, null, localUser.getId());
        localBaseUserCategoryOne.setId(BASE_UC_ID);

        UserCategory localUserCategoryOne =
                new UserCategory(UC_NAME, OperationType.DEBIT, true, localBaseUserCategoryOne.getId(), localUser.getId());
        localUserCategoryOne.setId(UC_ID);

        localBaseUserCategoryOne.setChildren(Sets.newSet(localUserCategoryOne));

        baseUserCategoryOptionalNotNullOne = Optional.of(localBaseUserCategoryOne);
        userCategoryOptionalNotNullOne = Optional.of(localUserCategoryOne);
        userCategoryOptionalNull = Optional.ofNullable(null);
    }
}
