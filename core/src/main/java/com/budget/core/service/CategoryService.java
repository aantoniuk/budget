package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.Category;
import com.budget.core.entity.Wallet;

import java.util.stream.Stream;

public interface CategoryService extends AbstractService<Category> {
    Stream<Category> findByType(OperationType type);
    Stream<Category> findByParentId(Long parentId);
    //void checkExistence(Category category);
}
