package com.budget.core.rest;

import com.budget.core.dao.CategoryDao;
import com.budget.core.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryDao categoryDao;

    @Autowired
    public CategoryController(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/categories")
    public ResponseEntity<Category> create(@RequestBody Category category) {
        if (categoryDao.existsByName(category.getName())) {
            return new ResponseEntity<>(category, HttpStatus.CONFLICT);
        }
        Category categoryForResponse = categoryDao.save(category);

        return new ResponseEntity<>(categoryForResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/categories/{id}")
    public ResponseEntity<Category> update(@PathVariable("id") Long id, @RequestBody Category category) {
        Category localCategory = categoryDao.findOne(id);
        if(localCategory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(category.getName() != null) {
            localCategory.setName(category.getName());
        }
        if(category.getDescription() != null) {
            localCategory.setDescription(category.getDescription());
        }
        localCategory.setParentId(category.getParentId());
        localCategory = categoryDao.save(category);

        return new ResponseEntity<>(localCategory, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/categories/{id}")
    public ResponseEntity<Category> remove(@PathVariable("id") long id) {
        Category localCategory = categoryDao.findOne(id);
        if(localCategory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        categoryDao.delete(id);

        return new ResponseEntity<>(localCategory, HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/categories/{id}")
    public ResponseEntity<Category> getOne(@PathVariable("id") long id) {
        Category category = categoryDao.findOne(id);
        if(category == null) {
            throw new NullPointerException("Category is not found by id:" + id);
        }

        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/categories")
    public ResponseEntity<List<Category>> getAll() {
        List<Category> categoryList = categoryDao.findAll();
        if (categoryList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(categoryList, HttpStatus.OK);
    }
}
