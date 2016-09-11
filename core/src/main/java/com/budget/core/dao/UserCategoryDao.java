package com.budget.core.dao;

import com.budget.core.entity.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCategoryDao extends JpaRepository<UserCategory, Long> {
}
