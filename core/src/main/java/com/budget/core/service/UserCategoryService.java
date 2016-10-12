package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.dao.UserCategoryDao;
import com.budget.core.entity.Category;
import com.budget.core.entity.UserCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserCategoryService {

    private final UserCategoryDao userCategoryDao;

    @Autowired
    public UserCategoryService(UserCategoryDao userCategoryDao) {
        this.userCategoryDao = userCategoryDao;
    }

    public Optional<UserCategory> findOne(Long id) {
        return userCategoryDao.findOne(id);
    }

    public Stream<UserCategory> findByType(Long userId, OperationType type) {
        return userCategoryDao.findByUserIdAndType(userId, type);
    }

    public Stream<UserCategory> findByParentId(Long userId, Long parentId) {
        return userCategoryDao.findByUserIdAndParentId(userId, parentId);
    }

    public Category create(UserCategory category) {
        checkExistenceByNameTypeParent(category);
        return userCategoryDao.save(category);
    }

    public Category update(UserCategory category) {
        if(!findOne(category.getId()).isPresent()) {
            throw new NullPointerException("Object doesn't exist");
        }
        checkExistenceByNameTypeParent(category);
        return userCategoryDao.save(category);
    }

    public void delete(Long id) {
        if(id == null) {
            throw new NullPointerException("Id cannot be null");
        }
        if(!findOne(id).isPresent()) {
            throw new NullPointerException("Object doesn't exist");
        }
        userCategoryDao.delete(id);
    }

    private void checkExistenceByNameTypeParent(UserCategory category) {
        Long parentId = null;
        if(category.getParent() != null) {
            parentId = category.getParent().getId();
        }
        if(userCategoryDao.findByUserIdAndNameAndTypeAndParentId(category.getUser().getId(), category.getName(),
                category.getType(), parentId).isPresent()) {
            String exMsg = String.format("Object already exists with user=%s, name=%s, type=$s",
                    category.getUser().getId(),category.getName(), category.getType().name());
            throw new IllegalArgumentException(exMsg);
        }
    }

}
