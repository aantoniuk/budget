package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.dao.CategoryDao;
import com.budget.core.entity.Category;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class CategoryServiceImpl extends BaseCategoryServiceImpl<Category> implements CategoryService {

    private final CategoryDao categoryDao;

    @Autowired
    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public Stream<Category> findByType(@NonNull OperationType type) {
        return categoryDao.findByType(type);
    }

    public Stream<Category> findByParentId(Long parentId) {
        return categoryDao.findByParentId(parentId);
    }

    //@Override
    void checkExistence(Category category) {
        Optional<Category> existedCategory = categoryDao.findByNameAndTypeAndParentId(category.getName(), category.getType(), category.getParentId());
        if(existedCategory.isPresent() && existedCategory.get().getId() != category.getId()) {
            String exMsg = String.format("Object already exists with name=%s, type=$s", category.getName(), category.getType().name());
            throw new IllegalArgumentException(exMsg);
        }
    }

    @Override
    CrudRepository<Category, Long> getDao() {
        return categoryDao;
    }
}
