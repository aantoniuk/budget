package com.budget.core.service;

import com.budget.core.entity.User;

import java.util.Optional;

/**
 * Created by serhii.kremeznyi on 2016-11-28.
 */
public interface UserService extends AbstractService<User> {
    User create(User user);
    Optional<User> findByLogin(String login);
    User updatePassword(Long userId, String password);
    User updateEnable(Long userId, Boolean enable);
}
