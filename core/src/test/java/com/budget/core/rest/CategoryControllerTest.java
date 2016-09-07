package com.budget.core.rest;

import com.budget.core.entity.Category;
import com.budget.core.entity.RestMessage;
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
        Category categoryForCheck = new CategoryBuilder().name("Drugs").description("For fun").id(17).parentId(177L).build();
        RestMessage restMessage = new RestMessage();

        Mockito.when(serviceMock.findOne(17L)).thenReturn(categoryForCheck);

        mockMvc.perform(MockMvcRequestBuilders.get("/categories/{id}", 17L).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restStatus", Matchers.is("SUCCESS")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restStatusObject.restMessageList", Matchers.containsInAnyOrder("null", "null", "null")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.id", Matchers.is(17)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.name", Matchers.is("Drugs")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.parentId", Matchers.is(177)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.restObject.description", Matchers.is("For fun")));
    }

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}
}