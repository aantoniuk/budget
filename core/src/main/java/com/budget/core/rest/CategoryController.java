package com.budget.core.rest;

import com.budget.core.entity.Category;
import com.budget.core.response.Message;
import com.budget.core.response.ResponseEntity;
import com.budget.core.response.Status;
import com.budget.core.response.Statuses;
import com.budget.core.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //@RequestMapping(method = RequestMethod.POST, value = "/categories")
    @RequestMapping(method = RequestMethod.POST)
    public org.springframework.http.ResponseEntity create(@RequestBody Category category) {
        Optional<Category> categoryOpt = categoryService.findByName(category.getName());
        if (!categoryOpt.isPresent()) {
            return new org.springframework.http.ResponseEntity(category, HttpStatus.CONFLICT);
        }
        Category categoryForResponse = categoryService.save(category);

        return new org.springframework.http.ResponseEntity(categoryForResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public org.springframework.http.ResponseEntity update(@PathVariable("id") Long id, @RequestBody Category category) {
        Optional<Category> categoryOpt = categoryService.findOne(id);
        if(!categoryOpt.isPresent()) {
            return new org.springframework.http.ResponseEntity(HttpStatus.NOT_FOUND);
        }
        Category localCategory = categoryOpt.get();
        if (category.getName() != null) {
            localCategory.setName(category.getName());
        }
        // FIXME adjust by CategoryDTO and new Entity Category
//        categoryOpt = categoryService.findOne(category.)
//        localCategory.setParent(arentId(category.getParentId());
        localCategory = categoryService.save(category);

        return new org.springframework.http.ResponseEntity(localCategory, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public org.springframework.http.ResponseEntity remove(@PathVariable("id") long id) {
        Optional<Category> categoryOpt = categoryService.findOne(id);
        if(!categoryOpt.isPresent()) {
            return new org.springframework.http.ResponseEntity(HttpStatus.NOT_FOUND);
        }
        categoryService.delete(id);

        return new org.springframework.http.ResponseEntity(categoryOpt.get(), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public org.springframework.http.ResponseEntity getOne(@PathVariable("id") long id) {
        Optional<Category> categoryOpt = categoryService.findOne(id);
        if(!categoryOpt.isPresent()) {
            throw new NullPointerException("Category is not found by id:" + id);
        }
        Message message = new Message();
        Status status = new Status(Statuses.SUCCESS.getText(), Arrays.asList(message));
        ResponseEntity responseEntity = new ResponseEntity(status, categoryOpt.get());
        return new org.springframework.http.ResponseEntity(responseEntity, HttpStatus.OK);
    }

    // @RequestMapping(method = RequestMethod.GET, value = "/categories")
    @RequestMapping(method = RequestMethod.GET)
    public org.springframework.http.ResponseEntity getAll() {
        List<Category> categoryList = categoryService.findAll();
        if (categoryList.isEmpty()) {
            return new org.springframework.http.ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new org.springframework.http.ResponseEntity(categoryList, HttpStatus.OK);
    }
}
