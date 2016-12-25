package com.budget.core.rest;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.UserCategory;
import com.budget.core.exception.ObjectNotFoundException;
import com.budget.core.service.UserCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/usercategories")
public class UserCategoryController {
    private final UserCategoryService userCategoryService;

    @Autowired
    public UserCategoryController(UserCategoryService userCategoryService) {
        this.userCategoryService = userCategoryService;
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity<UserCategory> create(@RequestBody UserCategory userCategory) {
        try {
            UserCategory createdCategory = userCategoryService.create(userCategory);
            return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Wrong parameters of UserCategory object for creation: " + iae.getMessage());
        } catch (NullPointerException npe) {
            throw new NullPointerException("Something is missing in UserCategory object for creation: " + npe.getMessage());
        }
    }

    @RequestMapping(path = "/update/{id}/name/{name}", method = RequestMethod.PUT)
    public ResponseEntity<UserCategory> updateName(@PathVariable("id") Long id, @PathVariable("name") String name) {
        try {
            UserCategory savedUserCategory = userCategoryService.updateName(id, name);
            return new ResponseEntity<>(savedUserCategory, HttpStatus.OK);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Wrong data for update of UserCategory with id=" + id + ": " + iae.getMessage());
        } catch (NullPointerException npe) {
            throw new NullPointerException("Didn't find UserCategory with id=" + id + " for update: " + npe.getMessage());
        }
    }

    @RequestMapping(path = "/update/{id}/type/{type}", method = RequestMethod.PUT)
    public ResponseEntity<UserCategory> updateType(@PathVariable("id") Long id, @PathVariable("type") String type) {
        try {
            UserCategory savedUserCategory = userCategoryService.updateType(id, OperationType.valueOf(type));
            return new ResponseEntity<>(savedUserCategory, HttpStatus.OK);
        } catch (UnsupportedOperationException usoe) {
            throw new UnsupportedOperationException("Wrong operation for update of UserCategory with id=" + id + ": " + usoe.getMessage());
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Wrong data for update of UserCategory with id=" + id + ": " + iae.getMessage());
        } catch (NullPointerException npe) {
            throw new NullPointerException("Didn't find UserCategory with id=" + id + " for update: " + npe.getMessage());
        }
    }

    @RequestMapping(path = "/update/{id}/parentid/{parentid}", method = RequestMethod.PUT)
    public ResponseEntity<UserCategory> updateParent(@PathVariable("id") Long id, @PathVariable("parentid") Long parentId) {
        try {
            UserCategory savedCategory = userCategoryService.updateParent(id, parentId);
            return new ResponseEntity<>(savedCategory, HttpStatus.OK);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Wrong data for update of UserCategory with id=" + id + ": " + iae.getMessage());
        } catch (NullPointerException npe) {
            throw new NullPointerException("Didn't find UserCategory with id=" + id + " for update: " + npe.getMessage());
        }
    }

    @RequestMapping(path = "/update/{id}/enable/{enable}", method = RequestMethod.PUT)
    public ResponseEntity<UserCategory> updateEnable(@PathVariable("id") Long id, @PathVariable("enable") String enable) {
        try {
            UserCategory savedUserCategory = userCategoryService.updateEnable(id, Boolean.getBoolean(enable));
            return new ResponseEntity<>(savedUserCategory, HttpStatus.OK);
        } catch (UnsupportedOperationException usoe) {
            throw new UnsupportedOperationException("Wrong operation for update of UserCategory with id=" + id + ": " + usoe.getMessage());
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Wrong data for update of UserCategory with id=" + id + ": " + iae.getMessage());
        } catch (NullPointerException npe) {
            throw new NullPointerException("Didn't find UserCategory with id=" + id + " for update: " + npe.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<UserCategory> remove(@PathVariable("id") Long id) throws ObjectNotFoundException {
        Optional<UserCategory> localUserCategory = userCategoryService.findOne(id);
        if (!localUserCategory.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object UserCategory with id " + id +" has not been found for DELETION.");
        }
        userCategoryService.delete(id);
        return new ResponseEntity<>(localUserCategory.get(), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<UserCategory> getOne(@PathVariable("id") Long id) throws ObjectNotFoundException {
        Optional<UserCategory> localUserCategory = userCategoryService.findOne(id);
        if (!localUserCategory.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object UserCategory with id " + id + " has not been found for GETTING.");
        }
        return new ResponseEntity<>(localUserCategory.get(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/uid/{id}/uctype/{type}")
    public ResponseEntity<List<UserCategory>> getByTypeForUser(@PathVariable("id") Long id, @PathVariable("type") String type) throws ObjectNotFoundException {
        Stream<UserCategory> localUserCategoryStream = userCategoryService.findByType(id, OperationType.valueOf(type));
        List<UserCategory> localUserCategoryList = localUserCategoryStream.collect(Collectors.toList());
        if (localUserCategoryList.size() == 0) {
            throw new ObjectNotFoundException("REST Controller: Object UserCategory with id " + id + " and type: " + type + " has not been found for GETTING.");
        }
        return new ResponseEntity<>(localUserCategoryList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/uid/{id}/parentid/{parentid}")
    public ResponseEntity<List<UserCategory>> getByTypeForUser(@PathVariable("id") Long id, @PathVariable("parentid") Long parentid) throws ObjectNotFoundException {
        Stream<UserCategory> localUserCategoryStream = userCategoryService.findByUserIdAndParentId(id, parentid);
        List<UserCategory> localUserCategoryList = localUserCategoryStream.collect(Collectors.toList());
        if (localUserCategoryList.size() == 0) {
            throw new ObjectNotFoundException("REST Controller: Object UserCategory with id " + id + " and parentid " + parentid + " has not been found for GETTING.");
        }
        return new ResponseEntity<>(localUserCategoryList, HttpStatus.OK);
    }
}
