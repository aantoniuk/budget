package com.budget.core.rest;

import com.budget.core.entity.Category;
import com.budget.core.service.CategoryService;
import com.budget.core.util.CategoryBuilder;
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

    @Test
    public void findById_CategoryFound_ShouldReturnRightObjectBack() throws Exception {
        Category parentCategory = new Category();
        Category categoryForCheck = new CategoryBuilder().name("Drugs").id(17).parent(parentCategory).build();
        Mockito.when(serviceMock.findOne(17L)).thenReturn(Optional.ofNullable(categoryForCheck));

        mockMvc.perform(MockMvcRequestBuilders.get("/categories/{id}", 17L).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusObject.restStatus", Matchers.is("SUCCESS")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusObject.messageList[0].msgCode", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusObject.messageList[0].msgValues", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusObject.messageList[0].msgText", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.id", Matchers.is(17)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.name", Matchers.is("Drugs")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.parent", Matchers.is(parentCategory)));
    }

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}
}