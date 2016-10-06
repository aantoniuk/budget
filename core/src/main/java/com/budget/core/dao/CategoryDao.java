package com.budget.core.dao;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.stream.Stream;

public interface CategoryDao extends Repository<Category, Long> {

    Optional<Category> findOne(Long id);

    Stream<Category> findByType(OperationType type);

    Stream<Category> findByParentId(Long parent);

    Optional<Category> findByNameAndTypeAndParentId(String name, OperationType type, Long parentId);

    Stream<Category> findAll();

    Category save(Category category);

    void delete(Long id);

}
