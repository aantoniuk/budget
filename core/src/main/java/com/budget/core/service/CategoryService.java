package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.dao.CategoryDao;
import com.budget.core.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class CategoryService {

    private final CategoryDao categoryDao;

    @Autowired
    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public Optional<Category> findOne(Long id) {
        return categoryDao.findOne(id);
    }

    public Optional<Category> findByName(String name) {
        return categoryDao.findByName(name);
    }

    public Stream<Category> findByType(OperationType type) {
        return categoryDao.findByType(type);
    }

    public Stream<Category> fingByParent(Category parent) {
        return categoryDao.findByParent(parent.getId());
    }

    public Stream<Category> findAll() {
        return categoryDao.findAll();
    }

    public Category create(Category category) {
        checkExistenceByNameTypeParent(category);
        return categoryDao.save(category);
    }

    public Category update(Category category) {
        if(!findOne(category.getId()).isPresent()) {
            throw new NullPointerException("Object doesn't exist");
        }
        checkExistenceByNameTypeParent(category);
        return categoryDao.save(category);
    }

    public void delete(Long id) {
        categoryDao.delete(id);
    }

    private void checkExistenceByNameTypeParent(Category category) {
        Long parentId = null;
        if(category.getParent() != null) {
            parentId = category.getParent().getId();
        }
        if(categoryDao.findByNameAndTypeAndParentId(category.getName(), category.getType(), parentId).isPresent()) {
            String exMsg = String.format("Object already exists with name=%s, type=$s", category.getName(), category.getType());
            throw new IllegalArgumentException(exMsg);
        }
    }
}
