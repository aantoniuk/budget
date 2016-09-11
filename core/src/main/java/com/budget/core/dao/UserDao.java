package com.budget.core.dao;

import com.budget.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Long> {

    User findByLogin(String login);
}
