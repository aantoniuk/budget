package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.Category;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount;
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
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    public void init() {
        category = new Category();
        category.setName("CategoryServiceTest");
        category.setType(OperationType.CREDIT);

        category = categoryService.create(category);
        AssertSqlCount.reset();
    }

    @Test
    public void findOne() throws Exception {
        Optional<Category> expectedCategory = categoryService.findOne(category.getId());
        assertAll(
                () -> assertTrue(expectedCategory.isPresent()),
                () -> assertEquals(expectedCategory.get(), category)
        );
        // select by first level Cache
        assertSelectCount(0);
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
    }

    @Test
    public void findByParent_withNullValue() throws Exception {
        Stream<Category> categories = categoryService.findByParentId(null);
        assertAll(
                () -> assertNotNull(categories),
                () -> assertEquals(categories.findFirst().get(), category)
        );
        assertSelectCount(1);
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
                () -> assertEquals(categories.count(), 2)
        );
    }

    @Test
    public void findByParent_withNotNullValue() throws Exception {
        Category childCategory = new Category();
        childCategory.setName("childCategory");
        childCategory.setType(OperationType.CREDIT);
        childCategory.setParent(category);
        categoryService.create(childCategory);

        Stream<Category> categories = categoryService.findByParentId(category.getId());
        assertAll(
                () -> assertNotNull(categories),
                () -> assertEquals(categories.findFirst().get(), childCategory)
        );
    }

    @Test
    public void create() throws Exception {
        AssertSqlCount.reset();

        Category newCategory = new Category();
        newCategory.setName("newCategory");
        newCategory.setType(OperationType.CREDIT);
        categoryService.create(newCategory);

        assertSelectCount(1);
        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(0);

        Optional<Category> expectedCategory = categoryService.findOne(newCategory.getId());
        assertAll(
                () -> assertTrue(expectedCategory.isPresent()),
                () -> assertEquals(expectedCategory.get(), newCategory)
        );
    }

    @Test
    public void create_duplicateCategory() throws Exception {
        Throwable exception = expectThrows(IllegalArgumentException.class, () -> categoryService.create(category));

        assertNotNull(exception);
    }

    @Test
    public void update() throws Exception {
        AssertSqlCount.reset();
        String categoryName = "updateCategory";
        category.setName(categoryName);
        categoryService.update(category);

        assertSelectCount(1);
        assertInsertCount(0);
        assertDeleteCount(0);

        Optional<Category> expectedCategory = categoryService.findOne(category.getId());

        assertAll(
                () -> assertTrue(expectedCategory.isPresent()),
                () -> assertEquals(expectedCategory.get(), category)
        );
    }

    @Test
    public void update_withEmptyId() throws Exception {
        Category newCategory = new Category();
        Throwable exception = expectThrows(NullPointerException.class, () -> categoryService.update(newCategory));

        assertNotNull(exception);
    }

    @Test
    public void delete() throws Exception {
        AssertSqlCount.reset();

        categoryService.delete(category.getId());

        assertSelectCount(0);
        assertInsertCount(0);
        assertUpdateCount(0);

        Optional<Category> deletedCategory = categoryService.findOne(category.getId());

        assertFalse(deletedCategory.isPresent());
    }

    @Test
    public void delete_byNullValue() throws Exception {
        Throwable exception = expectThrows(NullPointerException.class, () -> categoryService.delete(null));

        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals("Id cannot be null", exception.getMessage())
        );
    }

    @Test
    public void delete_byNotExistenceValue() throws Exception {
        Throwable exception = expectThrows(NullPointerException.class, () -> categoryService.delete(Long.MAX_VALUE));

        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals("Object doesn't exist", exception.getMessage())
        );
    }
}