package com.budget.core.rest;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.Category;
import com.budget.core.exception.ObjectNotFoundException;
import com.budget.core.service.CategoryService;
import com.google.gson.Gson;
import org.hamcrest.Matchers;
import org.junit.After;
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
@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoryService serviceMock;

    private final static Long BASECATEGORY_ONE_ID = 17L, CATEGORY_ONE_ID = 2L, BASECATEGORY_TWO_ID = Long.MAX_VALUE;

    private Optional<Category> baseCategoryOptionalNotNullOne, categoryOptionalNotNullOne, categoryOptionalNull;

    @Test
    public void findById_CategoryFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(BASECATEGORY_ONE_ID)).thenReturn(baseCategoryOptionalNotNullOne);
        mockMvc.perform(MockMvcRequestBuilders.get("/categories/{id}", BASECATEGORY_ONE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(baseCategoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(baseCategoryOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(baseCategoryOptionalNotNullOne.get().getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(baseCategoryOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parentId", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].id", Matchers.hasToString(categoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].name", Matchers.is(categoryOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].type", Matchers.is(categoryOptionalNotNullOne.get().getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].enable", Matchers.hasToString(categoryOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].parentId", Matchers.hasToString(baseCategoryOptionalNotNullOne.get().getId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(BASECATEGORY_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findById_CategoryNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(BASECATEGORY_ONE_ID)).thenReturn(categoryOptionalNull);
        mockMvc.perform(MockMvcRequestBuilders.get("/categories/{id}", BASECATEGORY_ONE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findOne(BASECATEGORY_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findByType_CategoryFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findByType(OperationType.DEBIT)).thenReturn(Stream.of(baseCategoryOptionalNotNullOne.get(), categoryOptionalNotNullOne.get()));
        mockMvc.perform(MockMvcRequestBuilders.get("/categories/type/{type}", OperationType.DEBIT.name()).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.hasToString(baseCategoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(baseCategoryOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].type", Matchers.is(baseCategoryOptionalNotNullOne.get().getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].enable", Matchers.hasToString(baseCategoryOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].parentId", Matchers.isEmptyOrNullString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].children[0].id", Matchers.hasToString(categoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].children[0].name", Matchers.is(categoryOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].children[0].type", Matchers.is(categoryOptionalNotNullOne.get().getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].children[0].enable", Matchers.hasToString(categoryOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].children[0].parentId", Matchers.hasToString(baseCategoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.hasToString(categoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is(categoryOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].type", Matchers.is(categoryOptionalNotNullOne.get().getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].enable", Matchers.hasToString(categoryOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].parentId", Matchers.hasToString(baseCategoryOptionalNotNullOne.get().getId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).findByType(OperationType.DEBIT);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findByType_CategoryNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findByType(OperationType.CREDIT)).thenThrow(new ObjectNotFoundException(""));
        mockMvc.perform(MockMvcRequestBuilders.get("/categories/type/{type}", OperationType.CREDIT.name()).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findByType(OperationType.CREDIT);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findByParentId_CategoryFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findByParentId(BASECATEGORY_ONE_ID)).thenReturn(Stream.of(categoryOptionalNotNullOne.get()));
        mockMvc.perform(MockMvcRequestBuilders.get("/categories/parentid/{id}", BASECATEGORY_ONE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.hasToString(categoryOptionalNotNullOne.get().getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(categoryOptionalNotNullOne.get().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].type", Matchers.is(categoryOptionalNotNullOne.get().getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].enable", Matchers.hasToString(categoryOptionalNotNullOne.get().getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].parentId", Matchers.hasToString(baseCategoryOptionalNotNullOne.get().getId().toString())));
        Mockito.verify(serviceMock, Mockito.times(1)).findByParentId(BASECATEGORY_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void findByParentId_CategoryNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findByParentId(BASECATEGORY_ONE_ID)).thenThrow(new ObjectNotFoundException(""));
        mockMvc.perform(MockMvcRequestBuilders.get("/categories/parentid/{id}", BASECATEGORY_ONE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).findByParentId(BASECATEGORY_ONE_ID);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void create_CategoryIsNotFound_ShouldReturnRightResponseEntity() throws Exception {
        Category localBaseCategoryTwo = new Category("Food", OperationType.CREDIT, true, null);
        localBaseCategoryTwo.setId(BASECATEGORY_TWO_ID);
        Mockito.when(serviceMock.create(localBaseCategoryTwo)).thenReturn(localBaseCategoryTwo);
        Gson gson = new Gson();
        String jsonString = gson.toJson(localBaseCategoryTwo);
        mockMvc.perform(MockMvcRequestBuilders.post("/categories/create").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.hasToString(BASECATEGORY_TWO_ID.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(localBaseCategoryTwo.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(localBaseCategoryTwo.getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enable", Matchers.hasToString(localBaseCategoryTwo.getEnable().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parentId", Matchers.isEmptyOrNullString()));
        Mockito.verify(serviceMock, Mockito.times(1)).create(org.mockito.Matchers.refEq(localBaseCategoryTwo));
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void createWithWrongValues_CategoryNotFound_ShouldReturnRightResponseException() throws Exception {
        String exMsg = String.format("Object already exists with name=%s, type=$s", baseCategoryOptionalNotNullOne.get().getName(),
                baseCategoryOptionalNotNullOne.get().getType().name());
        Mockito.when(serviceMock.create(baseCategoryOptionalNotNullOne.get())).thenThrow(new IllegalArgumentException(exMsg));
        Gson gson = new Gson();
        String jsonString = gson.toJson(baseCategoryOptionalNotNullOne.get());
        mockMvc.perform(MockMvcRequestBuilders.post("/categories/create").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isConflict());
        Mockito.verify(serviceMock, Mockito.times(1)).create(baseCategoryOptionalNotNullOne.get());
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    public void createWithSomethingEmpty_CategoryNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.create(baseCategoryOptionalNotNullOne.get())).thenThrow(new NullPointerException());
        Gson gson = new Gson();
        String jsonString = gson.toJson(baseCategoryOptionalNotNullOne.get());
        mockMvc.perform(MockMvcRequestBuilders.post("/categories/create").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(serviceMock, Mockito.times(1)).create(baseCategoryOptionalNotNullOne.get());
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    /*
    @Test
    public void deleteById_CategoryIsFound_ShouldReturnRightResponseEntity() throws Exception {
        Mockito.when(serviceMock.findOne(CATEGORY_ID_LONG)).thenReturn(categoryOptionalNotNullOne);

        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/{id}", CATEGORY_ID_LONG).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(categoryOptionalNotNullOne.get().getId())))
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

//    @Test
//    public void updateById_CategoryIsFound_ShouldReturnRightResponseEntity() throws Exception {
//        Mockito.when(serviceMock.findOne(CATEGORY_ID_LONG)).thenReturn(categoryOptionalNotNullOne);
//        categoryOptionalNotNullOne.get().setName("Medicine");
//        Gson gson = new Gson();
//        String jsonString = gson.toJson(categoryOptionalNotNullOne.get());
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/categories/update", CATEGORY_ID_LONG).accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is((int) categoryOptionalNotNullOne.get().getId())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(categoryOptionalNotNullOne.get().getName())));
//
//        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CATEGORY_ID_LONG);
//        Mockito.verify(serviceMock, Mockito.times(1)).update(org.mockito.Matchers.refEq(categoryOptionalNotNullOne.get()));
//        Mockito.verifyNoMoreInteractions(serviceMock);
//    }

    @Test
    public void updateById_CategoriesNotFound_ShouldReturnRightResponseException() throws Exception {
        Mockito.when(serviceMock.findOne(CATEGORY_ID_LONG)).thenReturn(categoryOptionalNull);
        Gson gson = new Gson();
        String jsonString = gson.toJson(categoryOptionalNotNullOne.get());

        mockMvc.perform(MockMvcRequestBuilders.put("/categories/update", CATEGORY_ID_LONG).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(serviceMock, Mockito.times(1)).findOne(CATEGORY_ID_LONG);
        Mockito.verifyNoMoreInteractions(serviceMock);
    }
*/


//
//    @Test
//    public void createByName_CategoriesFound_ShouldReturnRightResponseException() throws Exception {
//        Mockito.when(serviceMock.findByName(categoryOptionalNotNullOne.get().getName())).thenReturn(categoryOptionalNotNullOne);
//        Gson gson = new Gson();
//        String jsonString = gson.toJson(categoryOptionalNotNullOne.get());
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/categories/create").accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON).content(jsonString))
//                .andExpect(MockMvcResultMatchers.status().isConflict());
//
//        Mockito.verify(serviceMock, Mockito.times(1)).findByName(categoryOptionalNotNullOne.get().getName());
//        Mockito.verifyNoMoreInteractions(serviceMock);
//    }

    @Before
    public void setUp() throws Exception {
        Category localBaseCategoryOne = new Category("Drugs", OperationType.DEBIT, true, null);
        localBaseCategoryOne.setId(BASECATEGORY_ONE_ID);

        Category localCategoryOne = new Category("Coca-Cola", OperationType.DEBIT, true, BASECATEGORY_ONE_ID);
        localCategoryOne.setId(CATEGORY_ONE_ID);

        localBaseCategoryOne.setChildren(Sets.newSet(localCategoryOne));

        baseCategoryOptionalNotNullOne = Optional.of(localBaseCategoryOne);
        categoryOptionalNotNullOne = Optional.of(localCategoryOne);
        categoryOptionalNull = Optional.ofNullable(null);
    }

    @After
    public void tearDown() throws Exception {}
}
