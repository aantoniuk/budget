package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.Category;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("h2")
public class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;

    public static Category category;

    @Before
    public void setUp() throws Exception {
        category = new Category();
        category.setName("CategoryServiceTest");
        category.setType(OperationType.CREDIT);

        category = categoryService.create(category);
    }

    @After
    public void tearDown() throws Exception {
        categoryService.delete(category.getId());
    }

    @Test
    public void findOne() throws Exception {
        Optional<Category> expectedCategory = categoryService.findOne(category.getId());
        assertThat(expectedCategory.isPresent(), is(true));
        assertThat(expectedCategory.get(), equalTo(category));
    }

    @Test
    public void findOne_notExists() throws Exception {
        assertThat(categoryService.findOne(Long.MAX_VALUE).isPresent(), is(false));
    }

    @Test
    public void findByName() throws Exception {
        Optional<Category> expectedCategory = categoryService.findByName(category.getName());
        assertThat(expectedCategory.isPresent(), is(true));
        assertThat(expectedCategory.get(), equalTo(category));
    }

    @Test
    public void findByName_notExists() throws Exception {
        assertThat(categoryService.findByName("abracadabra").isPresent(), is(false));
    }

    @Test
    public void findByType() throws Exception {
        Stream<Category> categories = categoryService.findByType(category.getType());
        assertTrue(categories.anyMatch(item -> category.getId() == item.getId()));
    }

    @Test
    public void findByType_notExists() throws Exception {
        OperationType type;
        if(category.getType().equals(OperationType.CREDIT)) {
            type = OperationType.DEBIT;
        } else {
            type = OperationType.CREDIT;
        }
        Stream<Category> categories = categoryService.findByType(type);
        assertFalse(categories.anyMatch(item -> category.getId() == item.getId()));
    }

    @Test
    @Ignore
    public void findByParent() throws Exception {

    }

    @Test
    @Ignore
    public void create() throws Exception {

    }

    @Test
    @Ignore
    public void update() throws Exception {

    }

    @Test
    @Ignore
    public void delete() throws Exception {

    }

}