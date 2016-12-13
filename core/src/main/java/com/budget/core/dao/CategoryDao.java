package com.budget.core.dao;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.stream.Stream;

public interface CategoryDao extends JpaRepository<Category, Long> {
    Stream<Category> findByType(OperationType type);
    Stream<Category> findByParentId(Long parentId);
    Optional<Category> findByNameAndTypeAndParentId(String name, OperationType type, Long parentId);
}
