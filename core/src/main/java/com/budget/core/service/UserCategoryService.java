package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.Category;
import com.budget.core.entity.UserCategory;

import java.util.stream.Stream;

public interface UserCategoryService extends AbstractService<UserCategory> {
    Stream<UserCategory> findByParentId(Long parentId);
}
