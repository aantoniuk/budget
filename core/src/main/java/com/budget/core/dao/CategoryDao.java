package com.budget.core.dao;

import com.budget.core.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CategoryDao extends JpaRepository<Category, Long> {
}
