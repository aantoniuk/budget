package com.budget.core.rest;

import com.budget.core.entity.Category;
import com.budget.core.service.CategoryService;
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

    private final static Long CATEGORY_ID_LONG = 17L;

    Optional<Category> categoryOptionalNotNullOne, categoryOptionalNotNullTwo, categoryOptionalNull;

    @Test
    public void findById_CategoryFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(CATEGORY_ID_LONG)).thenReturn(categoryOptionalNotNullOne);

        mockMvc.perform(MockMvcRequestBuilders.get("/categories/{id}", CATEGORY_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is((int) categoryOptionalNotNullOne.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(categoryOptionalNotNullOne.get().getName())));

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CATEGORY_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findById_CategoryNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(CATEGORY_ID_LONG)).thenReturn(categoryOptionalNull);

        mockMvc.perform(MockMvcRequestBuilders.get("/categories/{id}", CATEGORY_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CATEGORY_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findAll_CategoriesFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findAll()).thenReturn(Arrays.asList(categoryOptionalNotNullOne.get(), categoryOptionalNotNullTwo.get()));

        mockMvc.perform(MockMvcRequestBuilders.get("/categories").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is((int) categoryOptionalNotNullOne.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(categoryOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is((int) categoryOptionalNotNullTwo.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is(categoryOptionalNotNullTwo.get().getName())));

        Mockito.verify(serviceMock, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findAll_CategoriesNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/categories").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(serviceMock, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void deleteById_CategoryIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(CATEGORY_ID_LONG)).thenReturn(categoryOptionalNotNullOne);

        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/{id}", CATEGORY_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is((int) categoryOptionalNotNullOne.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(categoryOptionalNotNullOne.get().getName())));

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CATEGORY_ID_LONG);
        Mockito.verify(serviceMock, Mockito.times(1)).delete(CATEGORY_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void deleteById_CategoriesNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(CATEGORY_ID_LONG)).thenReturn(categoryOptionalNull);

        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/{id}", CATEGORY_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CATEGORY_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateById_CategoryIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(CATEGORY_ID_LONG)).thenReturn(categoryOptionalNotNullOne);
        categoryOptionalNotNullOne.get().setName("Medicine");
        Gson gson = new Gson();
        String jsonString = gson.toJson(categoryOptionalNotNullOne.get());

        mockMvc.perform(MockMvcRequestBuilders.put("/categories/{id}", CATEGORY_ID_LONG).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is((int) categoryOptionalNotNullOne.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(categoryOptionalNotNullOne.get().getName())));

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CATEGORY_ID_LONG);
        Mockito.verify(serviceMock, Mockito.times(1)).save(org.mockito.Matchers.refEq(categoryOptionalNotNullOne.get()));
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void updateById_CategoriesNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(CATEGORY_ID_LONG)).thenReturn(categoryOptionalNull);
        Gson gson = new Gson();
        String jsonString = gson.toJson(categoryOptionalNotNullOne.get());

        mockMvc.perform(MockMvcRequestBuilders.put("/categories/{id}", CATEGORY_ID_LONG).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CATEGORY_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void createByName_CategoryIsNotFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findByName(categoryOptionalNotNullOne.get().getName())).thenReturn(categoryOptionalNull);
        Gson gson = new Gson();
        String jsonString = gson.toJson(categoryOptionalNotNullOne.get());

        mockMvc.perform(MockMvcRequestBuilders.post("/categories").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is((int) categoryOptionalNotNullOne.get().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(categoryOptionalNotNullOne.get().getName())));

        Mockito.verify(serviceMock, Mockito.times(1)).findByName(categoryOptionalNotNullOne.get().getName());
        Mockito.verify(serviceMock, Mockito.times(1)).save(org.mockito.Matchers.refEq(categoryOptionalNotNullOne.get()));
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void createByName_CategoriesFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findByName(categoryOptionalNotNullOne.get().getName())).thenReturn(categoryOptionalNotNullOne);
        Gson gson = new Gson();
        String jsonString = gson.toJson(categoryOptionalNotNullOne.get());

        mockMvc.perform(MockMvcRequestBuilders.post("/categories").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        Mockito.verify(serviceMock, Mockito.times(1)).findByName(categoryOptionalNotNullOne.get().getName());
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Before
    public void setUp() throws Exception {
        Category localCategory = new Category();
        localCategory.setName("Drugs");
        localCategory.setId(CATEGORY_ID_LONG);
        categoryOptionalNotNullOne = Optional.of(localCategory);

        localCategory.setName("Family");
        localCategory.setId(CATEGORY_ID_LONG + 3);
        categoryOptionalNotNullTwo = Optional.of(localCategory);

        categoryOptionalNull = Optional.ofNullable(null);
    }

    @After
    public void tearDown() throws Exception {

    }
}
