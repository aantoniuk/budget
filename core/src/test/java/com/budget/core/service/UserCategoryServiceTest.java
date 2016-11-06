package com.budget.core.service;

import com.budget.core.Utils.OperationType;
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

import java.util.Optional;
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
@Sql(executionPhase= Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts="classpath:data-h2.sql")
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
        UserCategory childCategory = UserCategory.builder().name("childCategory").type(OperationType.CREDIT).userId(user.getId()).build();
        childCategory.setParentId(userCategory.getId());
        userCategoryService.create(childCategory);

        Stream<UserCategory> expectedUserCategories = userCategoryService.findByParentId(user.getId(), userCategory.getId());
        assertAll(
                () -> assertNotNull(expectedUserCategories),
                () -> assertEquals(expectedUserCategories.findFirst().get(), childCategory)
        );
        userCategoryService.delete(childCategory.getId());
    }

    @Test
    public void create() throws Exception {

        UserCategory newCategory = UserCategory.builder().name("newCategory").type(OperationType.CREDIT).userId(user.getId()).build();
        userCategoryService.create(newCategory);

        assertSelectCount(1);
        assertInsertCount(1);

        Optional<UserCategory> expectedCategory = userCategoryService.findOne(newCategory.getId());
        assertAll(
                () -> assertTrue(expectedCategory.isPresent()),
                () -> assertNotNull(expectedCategory.get().getUserId()),
                () -> assertEquals(newCategory, expectedCategory.get())
        );
        userCategoryService.delete(newCategory.getId());
    }

    @Test
    public void create_withParent() throws Exception {

        UserCategory childCategory = UserCategory.builder().name("childCategory").type(OperationType.CREDIT).userId(user.getId()).build();
        childCategory.setParentId(userCategory.getId());
        userCategoryService.create(childCategory);

        assertSelectCount(1);
        assertInsertCount(1);

        Stream<UserCategory> expectedChildrenOfParentCategory = userCategoryService.findByParentId(user.getId(), userCategory.getId());
        Optional<UserCategory> expectedChildCategory = userCategoryService.findOne(childCategory.getId());

        assertAll(
                () -> assertEquals(childCategory, expectedChildrenOfParentCategory.findFirst().get()),
                () -> assertEquals(userCategory.getId(), (long) expectedChildCategory.get().getParentId())
        );
    }

    @Test
    public void create_duplicateCategory() throws Exception {
        UserCategory duplicatedCategory = UserCategory.builder().name(userCategory.getName()).type(userCategory.getType()).userId(user.getId()).build();

        Throwable exception = expectThrows(IllegalArgumentException.class, () -> userCategoryService.create(duplicatedCategory));

        assertNotNull(exception);
    }

//    @Test
//    @Sql(executionPhase= Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts="classpath:data-h2.sql")
//    public void update() throws Exception {
//        String categoryName = "updateUserCategory";
//        userCategory.setName(categoryName);
//        userCategoryService.update(userCategory);
//
//        assertSelectCount(3);
//        assertUpdateCount(1);
//
//        assertAll(
//                () -> assertEquals(categoryName, userCategory.getName())
//        );
//    }

//    @Test
//    public void update_changeParentId() throws Exception {
//        // create child category with parent
//        UserCategory childCategory = UserCategory.builder().name("childCategory").type(OperationType.CREDIT).userId(user.getId()).build();
//        childCategory.setParentId(userCategory.getId());
//        userCategoryService.create(childCategory);
//
//        // create new parent category
//        UserCategory newParent = UserCategory.builder().name("newParent").type(OperationType.CREDIT).userId(user.getId()).build();
//        userCategoryService.create(newParent);
//
//        // update child category
//        childCategory.setParentId(newParent.getId());
//        userCategoryService.update(childCategory);
//
//        Stream<UserCategory> expectedChildrenOfOldParentCategory = userCategoryService.findByParentId(user.getId(), userCategory.getId());
//        Stream<UserCategory> expectedChildrenOfNewParentCategory = userCategoryService.findByParentId(user.getId(), newParent.getId());
//        Optional<UserCategory> expectedChildCategory = userCategoryService.findOne(childCategory.getId());
//
//        assertAll(
//                () -> assertFalse(expectedChildrenOfOldParentCategory.findAny().isPresent()),
//                () -> assertEquals(childCategory, expectedChildrenOfNewParentCategory.findFirst().get()),
//                () -> assertEquals(newParent.getId(), (long) expectedChildCategory.get().getParentId())
//        );
//        userCategoryService.delete(childCategory.getId());
//        userCategoryService.delete(newParent.getId());
//    }

    @Test
    public void delete() throws Exception {
        userCategoryService.delete(userCategory.getId());

        assertSelectCount(3);
        assertDeleteCount(1);

        Optional<UserCategory> deletedCategory = userCategoryService.findOne(userCategory.getId());

        assertFalse(deletedCategory.isPresent());
    }

//    @Test
//    public void deleteRelationThroughChild() throws Exception {
//
//        UserCategory childCategory = UserCategory.builder().name("childCategory").type(OperationType.CREDIT).userId(user.getId()).build();
//        childCategory.setParentId(userCategory.getId());
//        userCategoryService.create(childCategory);
//
//        childCategory.setParentId(null);
//        userCategoryService.update(childCategory);
//
//        Stream<UserCategory> expectedChildrenOfParentCategory = userCategoryService.findByParentId(user.getId(), userCategory.getId());
//        Optional<UserCategory> expectedChildCategory = userCategoryService.findOne(childCategory.getId());
//
//        assertAll(
//                () -> assertTrue(expectedChildCategory.isPresent()),
//                () -> assertNull(expectedChildCategory.get().getParentId()),
//                () -> assertFalse(expectedChildrenOfParentCategory.findAny().isPresent())
//        );
//        userCategoryService.delete(childCategory.getId());
//    }

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