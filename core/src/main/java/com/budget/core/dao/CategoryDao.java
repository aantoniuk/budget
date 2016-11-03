package com.budget.core.dao;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.Category;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.stream.Stream;

public interface CategoryDao extends Repository<Category, Long> {

    Optional<Category> findOne(Long id);

    Stream<Category> findByType(OperationType type);

    Stream<Category> findByParentId(Long parentId);
    
    Optional<Category> findByNameAndTypeAndParentId(String name, OperationType type, Long parentId);

    Category save(Category category);

    void delete(Long id);

}
