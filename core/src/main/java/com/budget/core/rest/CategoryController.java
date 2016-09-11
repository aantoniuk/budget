package com.budget.core.rest;

import com.budget.core.entity.Category;
import com.budget.core.exception.CategoryNotFoundException;
import com.budget.core.response.Message;
import com.budget.core.response.RestResponseEntity;
import com.budget.core.response.Status;
import com.budget.core.response.Statuses;
import com.budget.core.response.enums.RestErrorCodes;
import com.budget.core.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Category> create(@RequestBody Category category) {
        if (categoryService.findByName(category.getName()).isPresent()) {
            return new ResponseEntity<>(category, HttpStatus.CONFLICT);
        }
        Category categoryForResponse = categoryService.save(category);

        return new ResponseEntity<>(categoryForResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public ResponseEntity<RestResponseEntity> update(@PathVariable("id") Long id, @RequestBody Category category) throws CategoryNotFoundException{
        // Category localCategory = categoryService.findOne(id);
        Optional<Category> localCategory = categoryService.findOne(id);
        if(!localCategory.isPresent()) {
            Message restMessage = new Message(RestErrorCodes.RECAT02.name(), new String[] {Long.toString(id)},
                    RestErrorCodes.RECAT02.getText());
            Status restStatus = new Status(Statuses.WARNING.getText(), Arrays.asList(restMessage));
            RestResponseEntity restResponseEntity = new RestResponseEntity(restStatus, localCategory);
            return new ResponseEntity<>(restResponseEntity, HttpStatus.NOT_FOUND);
        }
        if (category.getName() != null) {
            // localCategory.setName(category.getName());
        }
        // localCategory.setParentId(category.getParentId());
        categoryService.save(category);

        Message restMessage = new Message(null, new String[] {Long.toString(id)}, "Category with ID %1 has been successfully updated.");
        Status restStatus = new Status(Statuses.SUCCESS.getText(), Arrays.asList(restMessage));
        RestResponseEntity restResponseEntity = new RestResponseEntity(restStatus, category);
        return new ResponseEntity<>(restResponseEntity, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<RestResponseEntity> remove(@PathVariable("id") long id) throws CategoryNotFoundException {
        // Category localCategory = categoryService.findOne(id);
        Optional<Category> localCategory = categoryService.findOne(id);
        if (!localCategory.isPresent()) {
            Message restMessage = new Message(RestErrorCodes.RECAT02.name(), new String[] {Long.toString(id)},
                    RestErrorCodes.RECAT02.getText());
            Status restStatus = new Status(Statuses.WARNING.getText(), Arrays.asList(restMessage));
            RestResponseEntity restResponseEntity = new RestResponseEntity(restStatus, localCategory);
            return new ResponseEntity<>(restResponseEntity, HttpStatus.NOT_FOUND);
        }

        categoryService.delete(id);
        Message restMessage = new Message();
        Status restStatus = new Status(Statuses.SUCCESS.getText(), Arrays.asList(restMessage));
        RestResponseEntity restResponseEntity = new RestResponseEntity(restStatus, localCategory);
        return new ResponseEntity<>(restResponseEntity, HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<RestResponseEntity> getOne(@PathVariable("id") long id) throws CategoryNotFoundException {
        // Category category = categoryService.findOne(id);
        Optional<Category> localCategory = categoryService.findOne(id);
        if(!localCategory.isPresent()) {
            Message restMessage = new Message(RestErrorCodes.RECAT02.name(), new String[] {Long.toString(id)},
                    RestErrorCodes.RECAT02.getText());
            Status restStatus = new Status(Statuses.WARNING.getText(), Arrays.asList(restMessage));
            RestResponseEntity restResponseEntity = new RestResponseEntity(restStatus, localCategory.get());
            return new ResponseEntity<>(restResponseEntity, HttpStatus.NOT_FOUND);
        }
        Message restMessage = new Message();
        Status restStatus = new Status(Statuses.SUCCESS.getText(), Arrays.asList(restMessage));
        RestResponseEntity restResponseEntity = new RestResponseEntity(restStatus, localCategory.get());
        return new ResponseEntity<>(restResponseEntity, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<RestResponseEntity> getAll() {
        List<Category> categoryList = categoryService.findAll();
        if (categoryList.isEmpty()) {
            Message restMessage = new Message(RestErrorCodes.RECAT01.name(), null, RestErrorCodes.RECAT01.getText());
            Status restStatus = new Status(Statuses.WARNING.getText(), Arrays.asList(restMessage));
            RestResponseEntity restResponseEntity = new RestResponseEntity(restStatus, categoryList);
            return new ResponseEntity<>(restResponseEntity, HttpStatus.NOT_FOUND);
        }
        Message restMessage = new Message();
        Status restStatus = new Status(Statuses.SUCCESS.getText(), Arrays.asList(restMessage));
        RestResponseEntity restResponseEntity = new RestResponseEntity(restStatus, categoryList);
        return new ResponseEntity<>(restResponseEntity, HttpStatus.OK);
    }
}
