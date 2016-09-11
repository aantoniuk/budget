package com.budget.core.rest;

import com.budget.core.entity.Category;
import com.budget.core.response.Statuses;
import com.budget.core.response.enums.RestErrorCodes;
import com.budget.core.service.CategoryService;
import com.budget.core.util.CategoryBuilder;
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
@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoryService serviceMock;

    private final static Long CATEGORY_ID_LONG = 17L, PARENT_ID_LONG = 177L;
    private final static int CATEGORY_ID_INT = 17;

    @Test
    public void findById_CategoryFound_ShouldReturnRightResponseEntity() throws Exception {
        // Category categoryForCheck = new CategoryBuilder().name("Drugs").description("For fun").id(CATEGORY_ID_INT).parentId(PARENT_ID_LONG).build();
        Category categoryForCheck = new CategoryBuilder().name("Drugs").id(CATEGORY_ID_INT).build();
        Optional<Category> categoryForCheckOptional = Optional.of((Category) categoryForCheck);
        Mockito.when(serviceMock.findOne(CATEGORY_ID_LONG)).thenReturn(categoryForCheckOptional);

        mockMvc.perform(MockMvcRequestBuilders.get("/categories/{id}", CATEGORY_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restStatus", Matchers.is(Statuses.SUCCESS.getText())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgCode", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgValues", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgText", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.id", Matchers.is(categoryForCheck.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.name", Matchers.is(categoryForCheck.getName())))
                // .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.parentId", Matchers.is(categoryForCheck.getParentId().intValue())))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.restObject.description", Matchers.is(categoryForCheck.getDescription())));
        ;

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CATEGORY_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findById_CategoryNotFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(CATEGORY_ID_LONG)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/categories/{id}", CATEGORY_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restStatus", Matchers.is(Statuses.WARNING.getText())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgCode", Matchers.is(RestErrorCodes.RECAT02.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgValues[0]", Matchers.is(Long.toString(CATEGORY_ID_LONG))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgText", Matchers.is(RestErrorCodes.RECAT02.getText())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject", Matchers.isEmptyOrNullString()));

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CATEGORY_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);

    }

    @Test
    public void findAll_CategoriesFound_ShouldReturnRightResponseEntity() throws Exception {
        /*Category categoryForCheckOne = new CategoryBuilder().name("Family").description("Father, mother, sister, brother")
                .id(CATEGORY_ID_INT).parentId(PARENT_ID_LONG).build();
        Category categoryForCheckTwo = new CategoryBuilder().name("Car").description("Gas, wash, tires")
                .id(CATEGORY_ID_INT + 1).parentId(PARENT_ID_LONG - 1).build();*/
        Category categoryForCheckOne = new CategoryBuilder().name("Family").id(CATEGORY_ID_INT).build();
        Category categoryForCheckTwo = new CategoryBuilder().name("Car").id(CATEGORY_ID_INT + 1).build();

        Mockito.when(serviceMock.findAll()).thenReturn(Arrays.asList(categoryForCheckOne, categoryForCheckTwo));

        mockMvc.perform(MockMvcRequestBuilders.get("/categories").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restStatus", Matchers.is(Statuses.SUCCESS.getText())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgCode", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgValues", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgText", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject[0].id", Matchers.is(categoryForCheckOne.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject[0].name", Matchers.is(categoryForCheckOne.getName())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject[0].parentId", Matchers.is(categoryForCheckOne.getParentId().intValue())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject[0].description", Matchers.is(categoryForCheckOne.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject[1].id", Matchers.is(categoryForCheckTwo.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject[1].name", Matchers.is(categoryForCheckTwo.getName())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject[1].parentId", Matchers.is(categoryForCheckTwo.getParentId().intValue())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject[1].description", Matchers.is(categoryForCheckTwo.getDescription())));;
        ;

        Mockito.verify(serviceMock, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findAll_CategoriesNotFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/categories").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restStatus", Matchers.is(Statuses.WARNING.getText())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgCode", Matchers.is(RestErrorCodes.RECAT01.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgValues", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgText", Matchers.is(RestErrorCodes.RECAT01.getText())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject", Matchers.hasSize(0)));

        Mockito.verify(serviceMock, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void deleteById_CategoryIsFound_ShouldReturnRightResponseEntity() throws Exception {
        // Category categoryForCheck = new CategoryBuilder().name("Drugs").description("For fun").id(CATEGORY_ID_INT).parentId(PARENT_ID_LONG).build();
        Category categoryForCheck = new CategoryBuilder().name("Drugs").id(CATEGORY_ID_INT).build();
        Optional<Category> categoryForCheckOptional = Optional.of((Category) categoryForCheck);
        Mockito.when(serviceMock.findOne(CATEGORY_ID_LONG)).thenReturn(categoryForCheckOptional);

        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/{id}", CATEGORY_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restStatus", Matchers.is(Statuses.SUCCESS.getText())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgCode", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgValues", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgText", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.id", Matchers.is(categoryForCheck.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.name", Matchers.is(categoryForCheck.getName())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.parentId", Matchers.is(categoryForCheck.getParentId().intValue())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.description", Matchers.is(categoryForCheck.getDescription())));
        ;

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CATEGORY_ID_LONG);
        Mockito.verify(serviceMock, Mockito.times(1)).delete(CATEGORY_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void deleteById_CategoriesNotFound_ShouldReturnRightResponseEntity() throws Exception {
        Category categoryForCheck = new CategoryBuilder().name("Drugs").id(CATEGORY_ID_INT).build();
        Mockito.when(serviceMock.findOne(CATEGORY_ID_LONG)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/{id}", CATEGORY_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restStatus", Matchers.is(Statuses.WARNING.getText())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgCode", Matchers.is(RestErrorCodes.RECAT02.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgValues[0]", Matchers.is(Long.toString(CATEGORY_ID_LONG))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgText", Matchers.is(RestErrorCodes.RECAT02.getText())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject", Matchers.isEmptyOrNullString()));

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CATEGORY_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }


    @Test
    public void updateById_CategoryIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Category categoryForCheck = new CategoryBuilder().name("Drugs").id(CATEGORY_ID_INT).build();
        Optional<Category> categoryForCheckOptional = Optional.of((Category) categoryForCheck);
        Mockito.when(serviceMock.findOne(CATEGORY_ID_LONG)).thenReturn(categoryForCheckOptional);
        categoryForCheck.setName("Medicine");
        Gson gson = new Gson();
        String jsonString = gson.toJson(categoryForCheck);

        mockMvc.perform(MockMvcRequestBuilders.put("/categories/{id}", CATEGORY_ID_LONG).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restStatus", Matchers.is(Statuses.SUCCESS.getText())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgCode", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgValues[0]", Matchers.is(Long.toString(CATEGORY_ID_LONG))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgText", Matchers.is("Category with ID %1 has been successfully updated.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.id", Matchers.is(categoryForCheck.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.name", Matchers.is(categoryForCheck.getName())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.parentId", Matchers.is(categoryForCheck.getParentId().intValue())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.description", Matchers.is(categoryForCheck.getDescription())));
        ;

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CATEGORY_ID_LONG);
        Mockito.verify(serviceMock, Mockito.times(1)).save(org.mockito.Matchers.refEq(categoryForCheck));
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateById_CategoriesNotFound_ShouldReturnRightResponseEntity() throws Exception {
        Category categoryForCheck = new CategoryBuilder().name("Drugs").id(CATEGORY_ID_INT).build();
        Mockito.when(serviceMock.findOne(CATEGORY_ID_LONG)).thenReturn(null);
        categoryForCheck.setName("Medicine");
        Gson gson = new Gson();
        String jsonString = gson.toJson(categoryForCheck);

        mockMvc.perform(MockMvcRequestBuilders.put("/categories/{id}", CATEGORY_ID_LONG).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restStatus", Matchers.is(Statuses.WARNING.getText())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgCode", Matchers.is(RestErrorCodes.RECAT02.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgValues[0]", Matchers.is(Long.toString(CATEGORY_ID_LONG))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList[0].msgText", Matchers.is(RestErrorCodes.RECAT02.getText())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject", Matchers.isEmptyOrNullString()));

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CATEGORY_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

}
