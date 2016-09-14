package com.budget.core.service;

import com.budget.core.dao.UserCategoryDao;
import com.budget.core.entity.UserCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCategoryService {

    private final UserCategoryDao userCategoryDao;

    @Autowired
    public UserCategoryService(UserCategoryDao userCategoryDao) {
        this.userCategoryDao = userCategoryDao;
    }

    public UserCategory save(UserCategory userCategory) {
        return userCategoryDao.save(userCategory);
    }

    public void delete(UserCategory userCategory) {
        userCategoryDao.delete(userCategory);
    }

}
