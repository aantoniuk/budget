package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.Category;
import com.budget.core.entity.User;
import com.budget.core.entity.UserCategory;
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

import static com.jeeconf.hibernate.performancetuning.sqltracker.AssertSqlCount.assertSelectCount;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("h2")
@TestExecutionListeners({
        TransactionalTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class
})
@Transactional
public class UserCategoryServiceTest {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserCategoryService userCategoryService;

    private UserCategory userCategory;

    @BeforeEach
    public void init() {
        Category category = new Category("UserCategoryServiceTest", OperationType.CREDIT);
        categoryService.create(category);

        User user = new User();
        userService.create(user);

        userCategory = userCategoryService.findByParentId(user.getId(), null).findFirst().get();

        AssertSqlCount.reset();
    }

    @Test
    public void findOne() throws Exception {
        Optional<UserCategory> expextedUserCategory = userCategoryService.findOne(userCategory.getId());
        assertAll(
                () -> assertTrue(expextedUserCategory.isPresent()),
                () -> assertEquals(expextedUserCategory.get(), userCategory)
        );
        // select by first level Cache
        assertSelectCount(0);
    }

    @Test
    public void findByType() throws Exception {

    }

    @Test
    public void findByParentId() throws Exception {

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