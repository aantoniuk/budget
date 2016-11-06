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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
        Stream<Category> categories = categoryService.findByType(OperationType.DEBIT);
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

    @Test
    public void findByParent_withNotNullValue() throws Exception {
        Category childCategory = Category.builder().name("childCategory").type(OperationType.CREDIT).parentId(category.getId()).build();
        categoryService.create(childCategory);

        AssertSqlCount.reset();

        Stream<Category> categories = categoryService.findByParentId(category.getId());
        assertAll(
                () -> assertNotNull(categories),
                () -> assertEquals(1, categories.count())
        );
        assertSelectCount(1);
    }

    @Test
    public void findAllByParent() throws Exception {
        Category childCategory = Category.builder().name("child").type(OperationType.CREDIT).parentId(category.getId()).build();
        categoryService.create(childCategory);

        Category childChildCategory = Category.builder().name("childChild").type(OperationType.CREDIT).parentId(childCategory.getId()).build();
        categoryService.create(childChildCategory);

        AssertSqlCount.reset();

        Stream<Category> categoryStream = categoryService.findAllByParentId(category.getId());

        List<Category> categories = categoryStream.collect(Collectors.toList());

        assertSelectCount(3);
        assertAll(
                () -> assertNotNull(categories),
                () -> assertEquals(2, categories.size()),
                () -> assertTrue(categories.contains(childCategory)),
                () -> assertTrue(categories.contains(childChildCategory))
        );
    }

    @Test
    public void createRootCategory() throws Exception {

        Category newCategory = Category.builder().name("newCategory").type(OperationType.CREDIT).build();
        categoryService.create(newCategory);

        assertSelectCount(1);
        assertInsertCount(1);

        Optional<Category> expectedCategory = categoryService.findOne(newCategory.getId());
        assertAll(
                () -> assertTrue(expectedCategory.isPresent()),
                () -> assertEquals(newCategory, expectedCategory.get())
        );
        categoryService.delete(newCategory.getId());
    }

    @Test
    public void createRootCategoryWithoutType() throws Exception {

        Category newCategory = Category.builder().name("newCategory").build();

        Throwable exception =  expectThrows(NullPointerException.class, () -> categoryService.create(newCategory));
        assertNotNull(exception);
    }

    @Test
    public void createSubCategoryWithoutTypeAndEnable() throws Exception {
        Category childCategory = Category.builder().name("childCategory").parentId(category.getId()).build();
        categoryService.create(childCategory);

        assertSelectCount(2);
        assertInsertCount(1);

        Optional<Category> expectedCategory = categoryService.findOne(childCategory.getId());
        assertAll(
                () -> assertTrue(expectedCategory.isPresent()),
                () -> assertEquals(category.getType(), expectedCategory.get().getType()),
                () -> assertEquals(category.getEnable(), expectedCategory.get().getEnable()),
                () -> assertEquals(category.getId(), expectedCategory.get().getParentId())
        );
    }

    @Test
    public void createSubCategoryWithWrongType() throws Exception {
        Category childCategory = Category.builder().name("childCategory").type(OperationType.DEBIT).parentId(category.getId()).build();

        Throwable exception = expectThrows(IllegalArgumentException.class, () -> categoryService.create(childCategory));
        assertNotNull(exception);
    }

    @Test
    public void createSubCategoryWithWrongEnable() throws Exception {
        Category childCategory = Category.builder().name("childCategory").enable(Boolean.FALSE).parentId(category.getId()).build();

        Throwable exception = expectThrows(IllegalArgumentException.class, () -> categoryService.create(childCategory));
        assertNotNull(exception);
    }

    @Test
    public void createSubCategoryWithNotExistenceParentId() throws Exception {
        Category childCategory = Category.builder().name("childCategory").parentId(Long.MAX_VALUE).build();

        Throwable exception = expectThrows(NullPointerException.class, () -> categoryService.create(childCategory));
        assertNotNull(exception);
    }

    @Test
    public void create_duplicateRootCategory() throws Exception {
        Category duplicatedCategory = Category.builder().name(category.getName()).type(category.getType()).build();

        Throwable exception = expectThrows(IllegalArgumentException.class, () -> categoryService.create(duplicatedCategory));
        assertNotNull(exception);
    }

    @Test
    public void updateName() throws Exception {
        String categoryName = "updateCategory";
        category = categoryService.updateName(category.getId(), categoryName);

        assertEquals(categoryName, category.getName());

        assertSelectCount(2);
        assertUpdateCount(1);
    }

    @Test
    public void updateType_rootCategory() throws Exception {
        Category childCategory = Category.builder().name("childCategory").parentId(category.getId()).build();
        categoryService.create(childCategory);

        Category childChildCategory = Category.builder().name("childChildCategory").parentId(childCategory.getId()).build();
        categoryService.create(childChildCategory);

        AssertSqlCount.reset();

        category = categoryService.updateType(category.getId(), OperationType.DEBIT);

        assertSelectCount(5);
        assertUpdateCount(1);

        Stream<Category> categoryStream = categoryService.findAllByParentId(category.getId());

        assertAll(
                () -> assertEquals(OperationType.DEBIT, category.getType()),
                () -> assertTrue(categoryStream.allMatch(e -> e.getType().equals(OperationType.DEBIT)))
        );
    }

    @Test
    public void updateType_subCategory() throws Exception {
        Category childCategory = Category.builder().name("childCategory").parentId(category.getId()).build();
        categoryService.create(childCategory);

        Throwable exception = expectThrows(UnsupportedOperationException.class,
                () -> categoryService.updateType(childCategory.getId(), OperationType.DEBIT));
        assertNotNull(exception);
    }

    @Test
    public void updateEnable() throws Exception {
        Category childCategory = Category.builder().name("childCategory").parentId(category.getId()).build();
        categoryService.create(childCategory);

        Category childChildCategory = Category.builder().name("childChildCategory").parentId(childCategory.getId()).build();
        categoryService.create(childChildCategory);

        AssertSqlCount.reset();

        category = categoryService.updateEnable(category.getId(), Boolean.FALSE);

        assertSelectCount(4);
        assertUpdateCount(1);

        Stream<Category> categoryStream = categoryService.findAllByParentId(category.getId());

        assertAll(
                () -> assertFalse(category.getEnable()),
                () -> assertTrue(categoryStream.allMatch(e -> e.getEnable().equals(Boolean.FALSE)))
        );
    }

    @Test
    public void updateParent_rootCategoryAsSubCategory() {
        Category newCategory = Category.builder().name("newCategory").type(OperationType.DEBIT).enable(Boolean.FALSE).build();
        categoryService.create(newCategory);

        Category childCategory = Category.builder().name("childCategory").enable(Boolean.FALSE).parentId(newCategory.getId()).build();
        categoryService.create(childCategory);

        AssertSqlCount.reset();

        categoryService.updateParent(newCategory.getId(), category.getId());

        assertSelectCount(5);
        assertUpdateCount(1);

        Optional<Category> newCategoryOpt = categoryService.findOne(newCategory.getId());
        Stream<Category> categoryStream = categoryService.findByParentId(newCategory.getId());

        assertAll(
                () -> assertTrue(newCategoryOpt.isPresent()),
                () -> assertEquals(category.getEnable(), newCategoryOpt.get().getEnable()),
                () -> assertEquals(category.getType(), newCategoryOpt.get().getType()),
                () -> assertEquals(category.getId(), newCategoryOpt.get().getParentId()),
                () -> assertTrue(categoryStream.allMatch(
                        e -> e.getEnable().equals(category.getEnable() && e.getType().equals(category.getType()))))
        );
    }

    @Test
    public void updateParent_itselfIdAsParentId() {
        Throwable exception = expectThrows(IllegalArgumentException.class,
                () -> categoryService.updateParent(category.getId(), category.getId()));
        assertNotNull(exception);
    }

    @Test
    public void updateParent_duplicateSubCategory() {
        Category childCategory = Category.builder().name("childCategory").parentId(category.getId()).build();
        categoryService.create(childCategory);

        Category secondChildCategory = Category.builder().name("childCategory").type(category.getType()).
                enable(category.getEnable()).build();
        categoryService.create(secondChildCategory);

        Throwable exception = expectThrows(IllegalArgumentException.class,
                () -> categoryService.updateParent(secondChildCategory.getId(), category.getId()));
        assertNotNull(exception);

        categoryService.delete(secondChildCategory.getId());
    }

    @Test
    public void delete() throws Exception {
        categoryService.delete(category.getId());

        assertSelectCount(3);
        assertDeleteCount(1);

        Optional<Category> deletedCategory = categoryService.findOne(category.getId());

        assertFalse(deletedCategory.isPresent());
    }

    @Test
    public void delete_cascade() throws Exception {

        Category childCategory = Category.builder().name("childCategory").parentId(category.getId()).build();
        categoryService.create(childCategory);

        AssertSqlCount.reset();

        categoryService.delete(category.getId());

        assertSelectCount(4);
        assertDeleteCount(1);

        Optional<Category> deletedCategory = categoryService.findOne(category.getId());
        assertFalse(deletedCategory.isPresent());

        deletedCategory = categoryService.findOne(childCategory.getId());
        assertFalse(deletedCategory.isPresent());
    }

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