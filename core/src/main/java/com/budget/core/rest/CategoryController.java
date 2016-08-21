package com.budget.core.rest;

import com.budget.core.dao.CategoryDao;
import com.budget.core.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryDao categoryDao;

    @Autowired
    public CategoryController(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @RequestMapping("/create")
    public void create(String name, Long parentId) {
        Category category = new Category();
        category.setName(name);
        category.setParentId(parentId);

        categoryDao.save(category);
    }

    @RequestMapping("/update")
    public void update(Long id, String name, Long parentId) {
        Category category = categoryDao.findOne(id);
        if(category == null) {
            throw new NullPointerException("Category is not found by id:" + id);
        }
        if(name != null) {
            category.setName(name);
        }
        if(parentId != null) {
            category.setParentId(parentId);
        }

        categoryDao.save(category);
    }

    @RequestMapping("/remove")
    public void remove(long id) {
        categoryDao.delete(id);
    }

    @RequestMapping("/get")
    public Category get(long id) {
        Category category = categoryDao.findOne(id);
        if(category == null) {
            throw new NullPointerException("Category is not found by id:" + id);
        }

        return category;
    }

    @RequestMapping("/getAll")
    public List<Category> getAll() {
        return categoryDao.findAll();
    }
}
