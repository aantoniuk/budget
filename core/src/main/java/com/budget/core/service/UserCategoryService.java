package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.UserCategory;

import java.util.stream.Stream;

public interface UserCategoryService extends AbstractService<UserCategory> {
    UserCategory create(UserCategory category);
    UserCategory updateParent(Long id, Long parentId);
    UserCategory updateName(Long id, String name);
    UserCategory updateType(Long id, OperationType type);
    UserCategory updateEnable(Long id, Boolean enable);
    Stream<UserCategory> findByParentId(Long parentId);
    Stream<UserCategory> findByType(Long userId, OperationType type);
    Stream<UserCategory> findByUserIdAndParentId(Long userId, Long parentId);
}
