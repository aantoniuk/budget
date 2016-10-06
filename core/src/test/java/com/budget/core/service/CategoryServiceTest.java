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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertAll(
                () -> assertTrue(expectedCategory.isPresent()),
                () -> assertEquals(expectedCategory.get(), category));
    }

    @Test
    public void findOne_notExists() throws Exception {
        assertFalse(categoryService.findOne(Long.MAX_VALUE).isPresent());
    }

    @Test
    public void findByType() throws Exception {
        Stream<Category> categories = categoryService.findByType(category.getType());
        assertAll(
                () -> assertNotNull(categories),
                () -> assertEquals(categories.findFirst().get(), category));
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
        assertFalse(categories.findFirst().isPresent());
    }

    @Test
    public void findByParent_withNullValue() throws Exception {
        Stream<Category> categories = categoryService.findByParentId(null);
        assertAll(
                () -> assertNotNull(categories),
                () -> assertEquals(categories.findFirst().get(), category));
    }

    @Test
    public void findByParent_withNullValue_multipleResult() throws Exception {
        Category secondCategory = new Category();
        secondCategory.setName("second");
        secondCategory.setType(OperationType.CREDIT);
        categoryService.create(secondCategory);

        Stream<Category> categories = categoryService.findByParentId(null);
        assertAll(
                () -> assertNotNull(categories),
                () -> assertEquals(categories.count(), 2));
    }

    @Test
    public void findByParent_withNotNullValue() throws Exception {
        Category childCategory = new Category();
        childCategory.setName("second");
        childCategory.setType(OperationType.CREDIT);
        childCategory.setParent(category);
        categoryService.create(childCategory);

        Stream<Category> categories = categoryService.findByParentId(category.getId());
        assertAll(
                () -> assertNotNull(categories),
                () -> assertEquals(categories.findFirst().get(), childCategory));
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