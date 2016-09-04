package com.budget.core.rest;

import com.budget.core.entity.Category;
import com.budget.core.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/categories")
    public ResponseEntity<Category> create(@RequestBody Category category) {
        if (categoryService.findByName(category.getName())) {
            return new ResponseEntity<>(category, HttpStatus.CONFLICT);
        }
        Category categoryForResponse = categoryService.save(category);

        return new ResponseEntity<>(categoryForResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/categories/{id}")
    public ResponseEntity<Category> update(@PathVariable("id") Long id, @RequestBody Category category) {
        Category localCategory = categoryService.findOne(id);
        if(localCategory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(category.getName() != null) {
            localCategory.setName(category.getName());
        }
        localCategory.setParentId(category.getParentId());
        localCategory = categoryService.save(category);

        return new ResponseEntity<>(localCategory, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/categories/{id}")
    public ResponseEntity<Category> remove(@PathVariable("id") long id) {
        Category localCategory = categoryService.findOne(id);
        if(localCategory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        categoryService.delete(id);

        return new ResponseEntity<>(localCategory, HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/categories/{id}")
    public ResponseEntity<Category> getOne(@PathVariable("id") long id) {
        Category category = categoryService.findOne(id);
        if(category == null) {
            throw new NullPointerException("Category is not found by id:" + id);
        }

        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/categories")
    public ResponseEntity<List<Category>> getAll() {
        List<Category> categoryList = categoryService.findAll();
        if (categoryList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(categoryList, HttpStatus.OK);
    }
}
