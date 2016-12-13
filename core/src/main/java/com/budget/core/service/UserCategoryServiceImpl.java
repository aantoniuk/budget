package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.dao.UserCategoryDao;
import com.budget.core.entity.UserCategory;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserCategoryServiceImpl extends BaseCategoryServiceImpl<UserCategory> implements UserCategoryService {

    private final UserCategoryDao userCategoryDao;

    @Autowired
    public UserCategoryServiceImpl(UserCategoryDao userCategoryDao) {
        this.userCategoryDao = userCategoryDao;
    }

    public Stream<UserCategory> findByType(@NonNull Long userId, @NonNull OperationType type) {
        return userCategoryDao.findByUserIdAndType(userId, type);
    }

    public Stream<UserCategory> findByParentId(@NonNull Long parentId) {
        return userCategoryDao.findByParentId(parentId);
    }

    public Stream<UserCategory> findByParentId(Long userId, Long parentId) {
        return userCategoryDao.findByUserIdAndParentId(userId, parentId);
    }

    @Override
    @Transactional
    public UserCategory create(UserCategory category) {
        // root user category
        if(category.getParentId() == null) {
            if(category.getUserId() == null) {
                throw new NullPointerException("UserId can't be empty");
            }
        } else {
            // sub user category
            UserCategory parent = findParent(category.getParentId());
            if(category.getUserId() == null) {
                category.setUserId(parent.getUserId());
            } else if(!category.getUserId().equals(parent.getUserId())) {
                    throw new IllegalArgumentException("UserId of Category and Parent Category should be the same");
            }
        }
        return super.create(category);
    }

    @Transactional
    public UserCategory updateParent(@NonNull Long id, Long parentId) {
        UserCategory category = find(id);
        Optional<UserCategory> parentOpt = findOne(parentId);
        if(parentOpt.isPresent() && !parentOpt.get().getUserId().equals(category.getUserId())) {
            throw new IllegalArgumentException("User can't be different in Sub Category and Parent Category");
        }
        return super.updateParent(id, parentId);
    }

    @Override
    void checkExistence(UserCategory userCategory) {
        Optional<UserCategory> existedUserCategory = userCategoryDao.findByUserIdAndNameAndTypeAndParentId(
                userCategory.getUserId(), userCategory.getName(), userCategory.getType(), userCategory.getParentId());
        if(existedUserCategory.isPresent() && !Objects.equals(existedUserCategory.get().getId(), userCategory.getId())) {
            String exMsg = String.format("Object already exists with user=%s, name=%s, type=$s",
                    userCategory.getUserId(),userCategory.getName(), userCategory.getType().name());
            throw new IllegalArgumentException(exMsg);
        }
    }

    @Override
    CrudRepository<UserCategory, Long> getDao() {
        return userCategoryDao;
    }
}
