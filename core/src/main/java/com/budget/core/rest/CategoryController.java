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

//    @RequestMapping(path = "/update", method = RequestMethod.PUT)
//    public ResponseEntity<Category> update(@RequestBody Category category) {
//        Category savedCategory = categoryService.update(category);
//        return new ResponseEntity<>(savedCategory, HttpStatus.OK);
//    }
/*
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<Category> remove(@PathVariable("id") long id) throws ObjectNotFoundException {
        Optional<Category> localCategory = categoryService.findOne(id);
        if (!localCategory.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object Category with id " + id +" has not been found for DELETION.");
        }
        categoryService.delete(id);
        return new ResponseEntity<>(localCategory.get(), HttpStatus.NO_CONTENT);
    }
*/

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

//    @RequestMapping(method = RequestMethod.GET)
//    public ResponseEntity<List<Category>> getAll() throws ObjectNotFoundException {
//        List<Category> categoryList = categoryService.findAll();
//        if (categoryList.isEmpty()) {
//            throw new ObjectNotFoundException("REST Controller: All Category Objects have not been found.");
//        }
//        return new ResponseEntity<>(categoryList, HttpStatus.OK);
//    }
}
