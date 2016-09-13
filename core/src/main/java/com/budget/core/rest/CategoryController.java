package com.budget.core.rest;

import com.budget.core.entity.Category;
import com.budget.core.exception.ObjectAlreadyExists;
import com.budget.core.exception.ObjectNotFoundException;
import com.budget.core.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Category> create(@RequestBody Category category) throws ObjectAlreadyExists {
        Optional<Category> localCategory = categoryService.findByName(category.getName());
        if (localCategory.isPresent()) {
            throw new ObjectAlreadyExists("REST Controller: Object Category with id " + localCategory.get().getId() +" is already exist.");
        }
        Category categoryForResponse = categoryService.save(category);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public ResponseEntity<Category> update(@PathVariable("id") Long id, @RequestBody Category category) throws ObjectNotFoundException {
        Optional<Category> localCategory = categoryService.findOne(id);
        if(!localCategory.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object Category with id " + id +" has not been found for UPDATE.");
        }
        if (category.getName() != null) {
            localCategory.get().setName(category.getName());
        }
        categoryService.save(category);
        return new ResponseEntity<>(localCategory.get(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<Category> remove(@PathVariable("id") long id) throws ObjectNotFoundException {
        Optional<Category> localCategory = categoryService.findOne(id);
        if (!localCategory.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object Category with id " + id +" has not been found for DELETION.");
        }
        categoryService.delete(id);
        return new ResponseEntity<>(localCategory.get(), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<Category> getOne(@PathVariable("id") long id) throws ObjectNotFoundException {
        Optional<Category> localCategory = categoryService.findOne(id);
        if(!localCategory.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object Category with id " + id +" has not been found for GETTING.");
        }
        return new ResponseEntity<>(localCategory.get(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Category>> getAll() throws ObjectNotFoundException {
        List<Category> categoryList = categoryService.findAll();
        if (categoryList.isEmpty()) {
            throw new ObjectNotFoundException("REST Controller: All Category Objects have not been found.");
        }
        return new ResponseEntity<>(categoryList, HttpStatus.OK);
    }
}
