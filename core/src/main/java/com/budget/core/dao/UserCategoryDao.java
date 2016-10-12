package com.budget.core.dao;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.Category;
import com.budget.core.entity.UserCategory;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.stream.Stream;

public interface UserCategoryDao extends Repository<UserCategory, Long> {

    Optional<UserCategory> findOne(Long id);

    Stream<UserCategory> findByUserIdAndType(Long userId, OperationType type);

    Stream<UserCategory> findByUserIdAndParentId(Long userId, Long parent);

    Optional<UserCategory> findByUserIdAndNameAndTypeAndParentId(Long userId, String name, OperationType type, Long parentId);

    Category save(UserCategory userCategory);

    void delete(Long id);
}
