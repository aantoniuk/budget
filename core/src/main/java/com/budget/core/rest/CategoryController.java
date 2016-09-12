package com.budget.core.rest;

import com.budget.core.entity.Category;
import com.budget.core.exception.ObjectNotFoundException;
import com.budget.core.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

/*
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
*/

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
