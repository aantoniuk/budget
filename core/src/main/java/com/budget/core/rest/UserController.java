package com.budget.core.rest;

import com.budget.core.entity.User;
import com.budget.core.exception.ObjectAlreadyExists;
import com.budget.core.exception.ObjectNotFoundException;
import com.budget.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<User> create(@RequestBody User user) throws ObjectAlreadyExists {
        Optional<User> localUser = userService.findByLogin(user.getLogin());
        if (localUser.isPresent()) {
            throw new ObjectAlreadyExists("REST Controller: Object User with id " + localUser.get().getId() +" is already exist.");
        }
        User userForResponse = userService.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public ResponseEntity<User> update(@PathVariable("id") Long id, @RequestBody User user) throws ObjectNotFoundException {
        Optional<User> localUser = userService.findOne(id);
        if(!localUser.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object User with id " + id +" has not been found for UPDATE.");
        }
        if (user.getLogin() != null) {
            localUser.get().setLogin(user.getLogin());
        }
        userService.save(user);
        return new ResponseEntity<>(localUser.get(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<User> remove(@PathVariable("id") long id) throws ObjectNotFoundException {
        Optional<User> localUser = userService.findOne(id);
        if (!localUser.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object User with id " + id +" has not been found for DELETION.");
        }
        userService.delete(id);
        return new ResponseEntity<>(localUser.get(), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<User> getOne(@PathVariable("id") long id) throws ObjectNotFoundException {
        Optional<User> localUser = userService.findOne(id);
        if(!localUser.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object User with id " + id +" has not been found for GETTING.");
        }
        return new ResponseEntity<>(localUser.get(), HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<User>> getAll() throws ObjectNotFoundException {
        List<User> userList = userService.findAll();
        if (userList.isEmpty()) {
            throw new ObjectNotFoundException("REST Controller: All User Objects have not been found.");
        }
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }
}
