package com.budget.core.dao;

import com.budget.core.entity.User;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserDao extends Repository<User, Long> {

    Optional<User> findOne(Long userId);

    Optional<User> findByLogin(String login);

    User save(User user);

    void delete(Long id);
}
