package com.budget.core.rest;

import com.budget.core.entity.Category;
import com.budget.core.exception.ObjectNotFoundException;
import com.budget.core.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
        Category createdCategory = categoryService.create(category);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

//    @RequestMapping(path = "/update", method = RequestMethod.PUT)
//    public ResponseEntity<Category> update(@RequestBody Category category) {
//        Category savedCategory = categoryService.update(category);
//        return new ResponseEntity<>(savedCategory, HttpStatus.OK);
//    }

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

//    @RequestMapping(method = RequestMethod.GET)
//    public ResponseEntity<List<Category>> getAll() throws ObjectNotFoundException {
//        List<Category> categoryList = categoryService.findAll();
//        if (categoryList.isEmpty()) {
//            throw new ObjectNotFoundException("REST Controller: All Category Objects have not been found.");
//        }
//        return new ResponseEntity<>(categoryList, HttpStatus.OK);
//    }
}
