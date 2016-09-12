package com.budget.core.service;

import com.budget.core.dao.CategoryDao;
import com.budget.core.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by tolik on 9/4/2016.
 */
@Service
public class CategoryService {

    private final CategoryDao categoryDao;

    @Autowired
    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public Optional<Category> findOne(Long id) {
        return Optional.ofNullable(categoryDao.findOne(id));
    }

    public Optional<Category> findByName(String name) {
        return Optional.ofNullable(categoryDao.findByName(name));
    }

    public List<Category> findAll() {
        return categoryDao.findAll();
    }

    public Category save(Category category) {
        return categoryDao.save(category);
    }

    public void delete(Long id) {
        categoryDao.delete(id);
    }
}
