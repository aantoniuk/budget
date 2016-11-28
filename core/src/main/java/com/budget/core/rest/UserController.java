package com.budget.core.rest;

import com.budget.core.entity.User;
import com.budget.core.exception.ObjectAlreadyExists;
import com.budget.core.exception.ObjectNotFoundException;
import com.budget.core.service.UserService;
import com.budget.core.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<User> create(@RequestBody User user) throws ObjectAlreadyExists {
        /*Optional<User> localUser = userService.findByLogin(user.getLogin());
        if (localUser.isPresent()) {
            throw new ObjectAlreadyExists("REST Controller: Object User with id " + localUser.get().getId() +" is already exist.");
        }
        User userForResponse = userService.update(user);*/
        User userForResponse = new User();
        try {
            userForResponse = userService.create(user);
        } catch (IllegalArgumentException iae) {
            throw new ObjectAlreadyExists("REST Controller: Object User with id " + userForResponse.getId() + " is already exist.");
        }
        return new ResponseEntity<>(userForResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}/pswd/{pswd}")
    public ResponseEntity<User> updatePswd(@PathVariable("id") Long id, @PathVariable("pswd") String password) throws ObjectNotFoundException {
        User userForReturn;
        try {
            userForReturn = userService.updatePassword(id, password);
        } catch (NullPointerException npe) {
            throw new ObjectNotFoundException("REST Controller: Object User with id " + id +" has not been found for UPDATE.");
        }
        return new ResponseEntity<>(userForReturn, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}/enable/{enable}")
    public ResponseEntity<User> updateEnable(@PathVariable("id") Long id, @PathVariable("enable") Boolean enable) throws ObjectNotFoundException {
        User userForReturn;
        try {
            userForReturn = userService.updateEnable(id, enable);
        } catch (NullPointerException npe) {
            throw new ObjectNotFoundException("REST Controller: Object User with id " + id +" has not been found for UPDATE.");
        }
        return new ResponseEntity<>(userForReturn, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<User> remove(@PathVariable("id") Long id) throws ObjectNotFoundException {
        Optional<User> localUser = userService.findOne(id);
        try {
            userService.delete(id);
        } catch (NullPointerException npe) {
            throw new ObjectNotFoundException("REST Controller: Object User with id " + id +" has not been found for DELETION.");
        }
        return new ResponseEntity<>(localUser.get(), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<User> getOneById(@PathVariable("id") Long id) throws ObjectNotFoundException {
        Optional<User> localUser = userService.findOne(id);
        if(!localUser.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object User with id " + id +" has not been found for GETTING.");
        }
        return new ResponseEntity<>(localUser.get(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "login/{login}")
    public ResponseEntity<User> getOneByLogin(@PathVariable("login") String login) throws ObjectNotFoundException {
        Optional<User> localUser = userService.findByLogin(login);
        if(!localUser.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object User with login " + login +" has not been found for GETTING.");
        }
        return new ResponseEntity<>(localUser.get(), HttpStatus.OK);
    }
}
