package com.budget.core.dao;

import com.budget.core.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryDao extends JpaRepository<Category, Long> {

    Category findByName(String name);

    Optional<Category> findByNameAndParentId(String name, Long parentId);
}
