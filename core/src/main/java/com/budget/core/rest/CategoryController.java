package com.budget.core.rest;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.Category;
import com.budget.core.exception.ObjectNotFoundException;
import com.budget.core.service.CategoryService;
import com.budget.core.service.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity<Category> create(@RequestBody Category category) {
        try {
            Category createdCategory = categoryService.create(category);
            return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Wrong parameters of Category object for creation: " + iae.getMessage());
        } catch (NullPointerException npe) {
            throw new NullPointerException("Something is missing in Category object for creation: " + npe.getMessage());
        }
    }

    @RequestMapping(path = "/update/{id}/name/{name}", method = RequestMethod.PUT)
    public ResponseEntity<Category> updateName(@PathVariable("id") Long id, @PathVariable("name") String name) {
        try {
            Category savedCategory = categoryService.updateName(id, name);
            return new ResponseEntity<>(savedCategory, HttpStatus.OK);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Wrong data for update of Category with id=" + id + ": " + iae.getMessage());
        } catch (NullPointerException npe) {
            throw new NullPointerException("Didn't find Category with id=" + id + " for update: " + npe.getMessage());
        }
    }

    @RequestMapping(path = "/update/{id}/type/{type}", method = RequestMethod.PUT)
    public ResponseEntity<Category> updateType(@PathVariable("id") Long id, @PathVariable("type") String type) {
        try {
            Category savedCategory = categoryService.updateType(id, OperationType.valueOf(type));
            return new ResponseEntity<>(savedCategory, HttpStatus.OK);
        } catch (UnsupportedOperationException usoe) {
            throw new UnsupportedOperationException("Wrong operation for update of Category with id=" + id + ": " + usoe.getMessage());
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Wrong data for update of Category with id=" + id + ": " + iae.getMessage());
        } catch (NullPointerException npe) {
            throw new NullPointerException("Didn't find Category with id=" + id + " for update: " + npe.getMessage());
        }
    }

    @RequestMapping(path = "/update/{id}/parentid/{parentid}", method = RequestMethod.PUT)
    public ResponseEntity<Category> updateParent(@PathVariable("id") Long id, @PathVariable("parentid") Long parentId) {
        try {
            Category savedCategory = categoryService.updateParent(id, parentId);
            return new ResponseEntity<>(savedCategory, HttpStatus.OK);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Wrong data for update of Category with id=" + id + ": " + iae.getMessage());
        } catch (NullPointerException npe) {
            throw new NullPointerException("Didn't find Category with id=" + id + " for update: " + npe.getMessage());
        }
    }

    @RequestMapping(path = "/update/{id}/enable/{enable}", method = RequestMethod.PUT)
    public ResponseEntity<Category> updateEnable(@PathVariable("id") Long id, @PathVariable("enable") String enable) {
        try {
            Category savedCategory = categoryService.updateEnable(id, Boolean.getBoolean(enable));
            return new ResponseEntity<>(savedCategory, HttpStatus.OK);
        } catch (UnsupportedOperationException usoe) {
            throw new UnsupportedOperationException("Wrong operation for update of Category with id=" + id + ": " + usoe.getMessage());
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Wrong data for update of Category with id=" + id + ": " + iae.getMessage());
        } catch (NullPointerException npe) {
            throw new NullPointerException("Didn't find Category with id=" + id + " for update: " + npe.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<Category> remove(@PathVariable("id") Long id) throws ObjectNotFoundException {
        Optional<Category> localCategory = categoryService.findOne(id);
        if (!localCategory.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object Category with id " + id +" has not been found for DELETION.");
        }
        categoryService.delete(id);
        return new ResponseEntity<>(localCategory.get(), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<Category> getOne(@PathVariable("id") Long id) throws ObjectNotFoundException {
        Optional<Category> localCategory = categoryService.findOne(id);
        if (!localCategory.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object Category with id " + id + " has not been found for GETTING.");
        }
        return new ResponseEntity<>(localCategory.get(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/type/{type}")
    public ResponseEntity<List<Category>> getOneByType(@PathVariable("type") String type) throws ObjectNotFoundException {
        try {
            Stream<Category> localCategory = categoryService.findByType(OperationType.valueOf(type));
            return new ResponseEntity<>(localCategory.collect(Collectors.toList()), HttpStatus.OK);
        } catch (ObjectNotFoundException onfe) {
            throw new ObjectNotFoundException("REST Controller: Objects Categories with type " + type + " has not been found for GETTING.");
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/parentid/{id}")
    public ResponseEntity<List<Category>> getOneByParentId(@PathVariable("id") Long id) throws ObjectNotFoundException {
        try {
            Stream<Category> localCategory = categoryService.findByParentId(id);
            return new ResponseEntity<>(localCategory.collect(Collectors.toList()), HttpStatus.OK);
        } catch (ObjectNotFoundException onfe) {
            throw new ObjectNotFoundException("REST Controller: Object Category with parentId " + id + " has not been found for GETTING.");
        }
    }
}
