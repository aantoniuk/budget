package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.Category;
import com.budget.core.entity.User;
import com.budget.core.entity.UserCategory;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("h2")
@TestExecutionListeners({
        TransactionalTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class
})
public class UserCategoryServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserCategoryService userCategoryService;

    private User user;
    private UserCategory userCategory;

    @BeforeEach
    public void init() {
        user = new User("login", "password");
        userService.create(user);

        userCategory = UserCategory.builder().name("UserCategoryServiceTest").type(OperationType.CREDIT).userId(user.getId()).build();
        userCategoryService.create(userCategory);

        AssertSqlCount.reset();
    }

    @AfterEach
    public void afterEach() {
        Optional<UserCategory> categoryOptional = userCategoryService.findOne(userCategory.getId());
        if(categoryOptional.isPresent()) {
            userCategoryService.delete(userCategory.getId());
        }
        userService.delete(user.getId());
    }

    @Test
    public void findOne() throws Exception {
        Optional<UserCategory> expectedUserCategory = userCategoryService.findOne(userCategory.getId());
        assertAll(
                () -> assertTrue(expectedUserCategory.isPresent()),
                () -> assertEquals(userCategory, expectedUserCategory.get())
        );
        assertSelectCount(1);
    }

    @Test
    public void findOne_notExists() throws Exception {
        assertFalse(userCategoryService.findOne(Long.MAX_VALUE).isPresent());

        assertSelectCount(1);
    }

    @Test
    public void findByType() throws Exception {
        Stream<UserCategory> expectedUserCategories = userCategoryService.findByType(user.getId(), userCategory.getType());
        assertAll(
                () -> assertNotNull(expectedUserCategories),
                () -> assertEquals(userCategory, expectedUserCategories.findFirst().get())
        );

        assertSelectCount(1);
    }

    @Test
    public void findByType_notExists() throws Exception {
        OperationType type;
        if (userCategory.getType().equals(OperationType.CREDIT)) {
            type = OperationType.DEBIT;
        } else {
            type = OperationType.CREDIT;
        }
        Stream<UserCategory> expectedUserCategories = userCategoryService.findByType(user.getId(), type);
        assertFalse(expectedUserCategories.findFirst().isPresent());

        assertSelectCount(1);
    }

    @Test
    public void findByParent_withNullValue() throws Exception {
        Stream<UserCategory> expectedUserCategories = userCategoryService.findByParentId(user.getId(), null);
        assertAll(
                () -> assertNotNull(expectedUserCategories),
                () -> assertEquals(expectedUserCategories.findFirst().get(), userCategory)
        );
        assertSelectCount(1);
    }

    @Test
    public void findByParent_withNullValue_multipleResult() throws Exception {
        UserCategory secondCategory = UserCategory.builder().name("second").type(OperationType.CREDIT).userId(user.getId()).build();
        userCategoryService.create(secondCategory);

        Stream<UserCategory> expectedUserCategories = userCategoryService.findByParentId(user.getId(), null);
        assertAll(
                () -> assertNotNull(expectedUserCategories),
                () -> assertEquals(2, expectedUserCategories.count())
        );
        userCategoryService.delete(secondCategory.getId());
    }

    @Test
    public void findByParent_withNotNullValue() throws Exception {
        UserCategory childCategory = UserCategory.builder().name("childCategory").parentId(userCategory.getId()).build();
        userCategoryService.create(childCategory);

        Stream<UserCategory> expectedUserCategories = userCategoryService.findByParentId(user.getId(), userCategory.getId());
        assertAll(
                () -> assertNotNull(expectedUserCategories),
                () -> assertEquals(expectedUserCategories.findFirst().get(), childCategory)
        );
        userCategoryService.delete(childCategory.getId());
    }

    @Test
    public void findAllByParent() throws Exception {
        UserCategory childCategory = UserCategory.builder().name("child").parentId(userCategory.getId()).build();
        userCategoryService.create(childCategory);

        UserCategory childChildCategory = UserCategory.builder().name("childChild").type(OperationType.CREDIT).parentId(childCategory.getId()).build();
        userCategoryService.create(childChildCategory);

        AssertSqlCount.reset();

        Stream<UserCategory> categoryStream = userCategoryService.findAllByParentId(userCategory.getId());

        List<UserCategory> categories = categoryStream.collect(Collectors.toList());

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
        UserCategory newCategory = UserCategory.builder().name("newCategory").type(OperationType.CREDIT).userId(user.getId()).build();
        userCategoryService.create(newCategory);

        assertSelectCount(1);
        assertInsertCount(1);

        Optional<UserCategory> expectedCategory = userCategoryService.findOne(newCategory.getId());
        assertAll(
                () -> assertTrue(expectedCategory.isPresent()),
                () -> assertEquals(newCategory, expectedCategory.get())
        );
        userCategoryService.delete(newCategory.getId());
    }

    @Test
    public void createRootCategoryWithoutType() throws Exception {
        UserCategory newCategory = UserCategory.builder().name("newCategory").userId(user.getId()).build();

        Throwable exception = expectThrows(NullPointerException.class, () -> userCategoryService.create(newCategory));
        assertNotNull(exception);
    }

    @Test
    public void createRootCategoryWithoutUserId() throws Exception {
        UserCategory newCategory = UserCategory.builder().name("newCategory").type(OperationType.DEBIT).build();

        Throwable exception = expectThrows(NullPointerException.class, () -> userCategoryService.create(newCategory));
        assertNotNull(exception);
    }

    @Test
    public void createSubCategoryWithoutTypeAndEnableAndUser() throws Exception {
        UserCategory childCategory = UserCategory.builder().name("childCategory").parentId(userCategory.getId()).build();
        userCategoryService.create(childCategory);

        assertSelectCount(2);
        assertInsertCount(1);

        Optional<UserCategory> expectedCategory = userCategoryService.findOne(childCategory.getId());
        assertAll(
                () -> assertTrue(expectedCategory.isPresent()),
                () -> assertEquals(userCategory.getType(), expectedCategory.get().getType()),
                () -> assertEquals(userCategory.getEnable(), expectedCategory.get().getEnable()),
                () -> assertEquals(userCategory.getId(), expectedCategory.get().getParentId()),
                () -> assertEquals(userCategory.getUserId(), expectedCategory.get().getUserId())
        );
    }

    @Test
    public void createSubCategoryWithWrongType() throws Exception {
        UserCategory childCategory = UserCategory.builder().name("childCategory").type(OperationType.DEBIT).parentId(userCategory.getId()).build();

        Throwable exception = expectThrows(IllegalArgumentException.class, () -> userCategoryService.create(childCategory));
        assertNotNull(exception);
    }

    @Test
    public void createSubCategoryWithWrongEnable() throws Exception {
        UserCategory childCategory = UserCategory.builder().name("childCategory").enable(Boolean.FALSE).parentId(userCategory.getId()).build();

        Throwable exception = expectThrows(IllegalArgumentException.class, () -> userCategoryService.create(childCategory));
        assertNotNull(exception);
    }

    @Test
    public void createSubCategoryWithWrongUserId() throws Exception {
        UserCategory childCategory = UserCategory.builder().name("childCategory").parentId(userCategory.getId()).userId(Long.MAX_VALUE).build();

        Throwable exception = expectThrows(IllegalArgumentException.class, () -> userCategoryService.create(childCategory));
        assertNotNull(exception);
    }

    @Test
    public void createSubCategoryWithNotExistenceParentId() throws Exception {
        UserCategory childCategory = UserCategory.builder().name("childCategory").parentId(Long.MAX_VALUE).build();

        Throwable exception = expectThrows(NullPointerException.class, () -> userCategoryService.create(childCategory));
        assertNotNull(exception);
    }

    @Test
    public void create_duplicateCategory() throws Exception {
        UserCategory duplicatedCategory = UserCategory.builder().name(userCategory.getName()).type(userCategory.getType()).userId(user.getId()).build();

        Throwable exception = expectThrows(IllegalArgumentException.class, () -> userCategoryService.create(duplicatedCategory));

        assertNotNull(exception);
    }

    @Test
    public void updateName() throws Exception {
        String categoryName = "updateCategory";
        userCategory = userCategoryService.updateName(userCategory.getId(), categoryName);

        assertEquals(categoryName, userCategory.getName());

        assertSelectCount(2);
        assertUpdateCount(1);
    }

    @Test
    public void updateType_rootCategory() throws Exception {
        UserCategory childCategory = UserCategory.builder().name("childCategory").parentId(userCategory.getId()).build();
        userCategoryService.create(childCategory);

        UserCategory childChildCategory = UserCategory.builder().name("childChildCategory").parentId(childCategory.getId()).build();
        userCategoryService.create(childChildCategory);

        AssertSqlCount.reset();

        userCategory = userCategoryService.updateType(userCategory.getId(), OperationType.DEBIT);

        assertSelectCount(5);
        assertUpdateCount(1);

        Stream<UserCategory> categoryStream = userCategoryService.findAllByParentId(userCategory.getId());

        assertAll(
                () -> assertEquals(OperationType.DEBIT, userCategory.getType()),
                () -> assertTrue(categoryStream.allMatch(e -> e.getType().equals(OperationType.DEBIT)))
        );
    }

    @Test
    public void updateType_subCategory() throws Exception {
        UserCategory childCategory = UserCategory.builder().name("childCategory").parentId(userCategory.getId()).build();
        userCategoryService.create(childCategory);

        Throwable exception = expectThrows(UnsupportedOperationException.class,
                () -> userCategoryService.updateType(childCategory.getId(), OperationType.DEBIT));
        assertNotNull(exception);
    }

    @Test
    public void updateEnable() throws Exception {
        UserCategory childCategory = UserCategory.builder().name("childCategory").parentId(userCategory.getId()).build();
        userCategoryService.create(childCategory);

        UserCategory childChildCategory = UserCategory.builder().name("childChildCategory").parentId(childCategory.getId()).build();
        userCategoryService.create(childChildCategory);

        AssertSqlCount.reset();

        userCategory = userCategoryService.updateEnable(userCategory.getId(), Boolean.FALSE);

        assertSelectCount(4);
        assertUpdateCount(1);

        Stream<UserCategory> categoryStream = userCategoryService.findAllByParentId(userCategory.getId());

        assertAll(
                () -> assertFalse(userCategory.getEnable()),
                () -> assertTrue(categoryStream.allMatch(e -> e.getEnable().equals(Boolean.FALSE)))
        );
    }

    @Test
    public void updateParent_rootCategoryAsSubCategory() {
        UserCategory newCategory = UserCategory.builder().name("newCategory").type(OperationType.DEBIT).
                enable(Boolean.FALSE).userId(user.getId()).build();
        userCategoryService.create(newCategory);

        UserCategory childCategory = UserCategory.builder().name("childCategory").enable(Boolean.FALSE).
                parentId(newCategory.getId()).build();
        userCategoryService.create(childCategory);

        AssertSqlCount.reset();

        userCategoryService.updateParent(newCategory.getId(), userCategory.getId());

        assertSelectCount(5);
        assertUpdateCount(1);

        Optional<UserCategory> newCategoryOpt = userCategoryService.findOne(newCategory.getId());
        Stream<UserCategory> categoryStream = userCategoryService.findByParentId(newCategory.getId());

        assertAll(
                () -> assertTrue(newCategoryOpt.isPresent()),
                () -> assertEquals(userCategory.getEnable(), newCategoryOpt.get().getEnable()),
                () -> assertEquals(userCategory.getType(), newCategoryOpt.get().getType()),
                () -> assertEquals(userCategory.getId(), newCategoryOpt.get().getParentId()),
                () -> assertTrue(categoryStream.allMatch(
                        e -> e.getEnable().equals(userCategory.getEnable() && e.getType().equals(userCategory.getType()))))
        );
    }

    @Test
    public void updateParent_itselfIdAsParentId() {
        Throwable exception = expectThrows(IllegalArgumentException.class,
                () -> userCategoryService.updateParent(userCategory.getId(), userCategory.getId()));
        assertNotNull(exception);
    }

    @Test
    public void updateParentWithWrongUser() {
        User tempUser = new User("test", "test");
        userService.create(tempUser);

        UserCategory newCategory = UserCategory.builder().name("newCategory").type(userCategory.getType()).userId(tempUser.getId()).build();
        userCategoryService.create(newCategory);

        Throwable exception = expectThrows(IllegalArgumentException.class,
                () -> userCategoryService.updateParent(newCategory.getId(), userCategory.getId()));
        assertNotNull(exception);

        userCategoryService.delete(newCategory.getId());
        userService.delete(tempUser.getId());
    }

    @Test
    public void updateParent_duplicateSubCategory() {
        UserCategory childCategory = UserCategory.builder().name("childCategory").parentId(userCategory.getId()).build();
        userCategoryService.create(childCategory);

        UserCategory secondChildCategory = UserCategory.builder().name("childCategory").type(userCategory.getType()).
                enable(userCategory.getEnable()).userId(user.getId()).build();
        userCategoryService.create(secondChildCategory);

        Throwable exception = expectThrows(IllegalArgumentException.class,
                () -> userCategoryService.updateParent(secondChildCategory.getId(), userCategory.getId()));
        assertNotNull(exception);

        userCategoryService.delete(secondChildCategory.getId());
    }

    @Test
    public void delete() throws Exception {
        userCategoryService.delete(userCategory.getId());

        assertSelectCount(3);
        assertDeleteCount(1);

        Optional<UserCategory> deletedCategory = userCategoryService.findOne(userCategory.getId());

        assertFalse(deletedCategory.isPresent());
    }

    @Test
    public void delete_cascade() throws Exception {

        UserCategory childCategory = UserCategory.builder().name("childCategory").type(OperationType.CREDIT).userId(user.getId()).build();
        childCategory.setParentId(userCategory.getId());
        userCategoryService.create(childCategory);

        AssertSqlCount.reset();

        userCategoryService.delete(userCategory.getId());

        assertSelectCount(4);
        assertDeleteCount(1);

        Optional<UserCategory> expectedParentCategory = userCategoryService.findOne(userCategory.getId());
        Optional<UserCategory> expectedChildCategory = userCategoryService.findOne(childCategory.getId());

        assertAll(
                () -> assertFalse(expectedParentCategory.isPresent()),
                () -> assertFalse(expectedChildCategory.isPresent())
        );
    }

}