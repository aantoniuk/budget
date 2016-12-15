package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.Category;
import com.budget.core.entity.Wallet;
import com.budget.core.exception.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

public interface CategoryService extends AbstractService<Category> {
    Category create(Category category);
    Stream<Category> findByType(OperationType type) throws ObjectNotFoundException;
    Stream<Category> findByParentId(Long parentId);
}
