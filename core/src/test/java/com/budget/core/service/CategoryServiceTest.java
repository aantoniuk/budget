package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.Category;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.Optional;
import java.util.stream.Stream;

import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.assertDeleteCount;
import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.assertInsertCount;
import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.assertSelectCount;
import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.assertUpdateCount;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("h2")
@TestExecutionListeners({
        TransactionalTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class
})
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    public void beforeEach() {
        category = Category.builder().name("CategoryServiceTest").type(OperationType.CREDIT).build();

        category = categoryService.create(category);
        AssertSqlCount.reset();
    }

    @AfterEach
    public void afterEach() {
        Optional<Category> categoryOptional = categoryService.findOne(category.getId());
        if(categoryOptional.isPresent()) {
            categoryService.delete(category.getId());
        }
    }

    @Test
    public void findOne() throws Exception {
        Optional<Category> expectedCategory = categoryService.findOne(category.getId());
        assertAll(
                () -> assertTrue(expectedCategory.isPresent()),
                () -> assertEquals(expectedCategory.get(), category)
        );
        // select by first level Cache
        assertSelectCount(1);
    }

    @Test
    public void findOne_notExists() throws Exception {
        assertFalse(categoryService.findOne(Long.MAX_VALUE).isPresent());
        assertSelectCount(1);
    }

    @Test
    public void findByType() throws Exception {
        Stream<Category> categories = categoryService.findByType(category.getType());
        assertAll(
                () -> assertNotNull(categories),
                () -> assertEquals(categories.findFirst().get(), category)
        );
        assertSelectCount(1);
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

        assertSelectCount(1);
    }

    @Test
    public void findByParent_withNullValue() throws Exception {
        Stream<Category> categories = categoryService.findByParentId(null);
        assertAll(
                () -> assertEquals(1, categories.count())
        );
        assertSelectCount(1);
    }

    @Test
    public void findByParent_withNullValue_multipleResult() throws Exception {
        Category secondCategory = Category.builder().name("second").type(OperationType.CREDIT).build();
        categoryService.create(secondCategory);

        AssertSqlCount.reset();

        Stream<Category> categories = categoryService.findByParentId(null);
        assertAll(
                () -> assertNotNull(categories),
                () -> assertEquals(2, categories.count())
        );
        assertSelectCount(1);

        categoryService.delete(secondCategory.getId());
    }

//    @Test
//    public void findByParent_withNotNullValue() throws Exception {
//        Category childCategory = Category.builder().name("childCategory").type(OperationType.CREDIT).build();
//        category.getChildren().add(childCategory);
//        categoryService.update(category);
//
//        AssertSqlCount.reset();
//
//        Stream<Category> categories = categoryService.findByParentId(category.getId());
//        assertAll(
//                () -> assertNotNull(categories),
//                () -> assertEquals(1, categories.count())
//        );
//        assertSelectCount(1);
//    }

    @Test
    public void create() throws Exception {

        Category newCategory = Category.builder().name("newCategory").type(OperationType.CREDIT).build();
        categoryService.create(newCategory);

        assertSelectCount(1);
        assertInsertCount(1);

        Optional<Category> expectedCategory = categoryService.findOne(newCategory.getId());
        assertAll(
                () -> assertTrue(expectedCategory.isPresent()),
                () -> assertEquals(expectedCategory.get(), newCategory)
        );
        categoryService.delete(newCategory.getId());
    }

    @Test
    public void create_duplicateRootCategory() throws Exception {
        Category duplicatedCategory = Category.builder().name(category.getName()).type(category.getType()).build();

        Throwable exception = expectThrows(IllegalArgumentException.class, () -> categoryService.create(duplicatedCategory));
        assertNotNull(exception);
    }

//    @Test
//    public void update() throws Exception {
//        String categoryName = "updateCategory";
//        category.setName(categoryName);
//        categoryService.update(category);
//
//        assertEquals(categoryName, category.getName());
//
//        assertSelectCount(3);
//        assertUpdateCount(1);
//    }

    @Test
    public void update_withEmptyId() throws Exception {
//        Category newCategory = new Category();
//
//        Throwable exception = expectThrows(NullPointerException.class, () -> categoryService.update(newCategory));
//        assertNotNull(exception);
//
//        assertSelectCount(1);
//        assertUpdateCount(0);
    }

    @Test
    public void delete() throws Exception {
        categoryService.delete(category.getId());

        assertSelectCount(3);
        assertDeleteCount(1);

        Optional<Category> deletedCategory = categoryService.findOne(category.getId());

        assertFalse(deletedCategory.isPresent());
    }

//    @Test
//    public void delete_cascade() throws Exception {
//
//        Category childCategory = Category.builder().name("childCategory").type(OperationType.CREDIT).build();
//
//        category.getChildren().add(childCategory);
//        categoryService.update(category);
//
//        AssertSqlCount.reset();
//
//        categoryService.delete(category.getId());
//
//        assertSelectCount(4);
//        assertDeleteCount(1);
//
//        Optional<Category> deletedCategory = categoryService.findOne(category.getId());
//        assertFalse(deletedCategory.isPresent());
//
//        deletedCategory = categoryService.findOne(childCategory.getId());
//        assertFalse(deletedCategory.isPresent());
//    }

    @Test
    public void delete_byNullValue() throws Exception {
        Throwable exception = expectThrows(NullPointerException.class, () -> categoryService.delete(null));

        assertSelectCount(0);
        assertDeleteCount(0);

        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals("Id cannot be null", exception.getMessage())
        );
    }

    @Test
    public void delete_byNotExistenceValue() throws Exception {
        Throwable exception = expectThrows(NullPointerException.class, () -> categoryService.delete(Long.MAX_VALUE));

        assertSelectCount(1);
        assertDeleteCount(0);

        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals("Object doesn't exist", exception.getMessage())
        );
    }
}