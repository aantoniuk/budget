package com.budget.core.dao;

import com.budget.core.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDao extends JpaRepository<Category, Long> {

    Category findByName(String name);
}
