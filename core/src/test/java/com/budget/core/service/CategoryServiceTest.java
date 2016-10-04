package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("h2")
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    private static Category category;

    @BeforeEach
    public void init() {
        if(category == null) {
            category = new Category();
            category.setName("CategoryServiceTest");
            category.setType(OperationType.CREDIT);

            category = categoryService.create(category);
        }
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
        assertThat(categories.findFirst().get(), equalTo(category));
    }

    @Test
    public void findByType_notExists() throws Exception {
        OperationType type;
        if (category.getType().equals(OperationType.CREDIT)) {
            type = OperationType.DEBIT;
        } else {
            type = OperationType.CREDIT;
        }
        Stream<Category> categories = categoryService.findByType(type);
        assertThat(categories.findFirst().isPresent(), is(false));
    }

    @Test
    public void findByParent() throws Exception {

    }

    @Test
    public void create() throws Exception {
    }

    @Test
    public void update() throws Exception {

    }

    @Test
    public void delete() throws Exception {

    }
}