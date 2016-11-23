package com.budget.core.rest;

import com.budget.core.entity.UserCurrency;
import com.budget.core.exception.ObjectAlreadyExists;
import com.budget.core.exception.ObjectNotFoundException;
import com.budget.core.service.UserCurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usercurrencies")
public class UserCurrencyController {

    private final UserCurrencyService userCurrencyService;

    @Autowired
    public UserCurrencyController(UserCurrencyService userCurrencyService) {
        this.userCurrencyService = userCurrencyService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserCurrency> create(@RequestBody UserCurrency userCurrency) {
        UserCurrency userCurrencyForResponse = new UserCurrency();
        try {
            userCurrencyForResponse = userCurrencyService.save(userCurrency);
        } catch (IllegalArgumentException iae) {
            throw new ObjectAlreadyExists("REST Controller: Object Currency with id " + userCurrency.getId() +" is already exist.");
        }
        return new ResponseEntity<>(userCurrencyForResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}/cur_id/{cur_id}")
    public ResponseEntity<UserCurrency> updateCurrencyId(@PathVariable("id") Long userCurrencyId, @PathVariable("cur_id") Long currencyId) throws ObjectNotFoundException {
        Optional<UserCurrency> localUserCurrency = userCurrencyService.findOne(userCurrencyId);
        if (!localUserCurrency.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object UserCurrency with id " + userCurrencyId + " has not been found for UPDATE.");
        } else if (localUserCurrency.get().getCurrencyId().equals(currencyId)) {
            throw new IllegalArgumentException("REST Controller: Object UserCurrency with id " + userCurrencyId + " already exists. So nothing to UPDATE.");
        }
        UserCurrency userCurrencyForResponse = userCurrencyService.updateCurrencyId(userCurrencyId, currencyId);
        return new ResponseEntity<>(userCurrencyForResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}/value/{value:.+}")
    public ResponseEntity<UserCurrency> updateValue(
            @PathVariable("id") Long userCurrencyId, @PathVariable("value") Float value) {
        Optional<UserCurrency> localUserCurrency = userCurrencyService.findOne(userCurrencyId);
        if (!localUserCurrency.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object UserCurrency with id " + userCurrencyId + " has not been found for UPDATE.");
        } else if (Float.compare(localUserCurrency.get().getValue(), value) == 0) {
            throw new IllegalArgumentException("REST Controller: Object UserCurrency with id " + userCurrencyId + " already exists with exact values. Nothing to UPDATE.");
        }
        localUserCurrency.get().setValue(value);
        UserCurrency userCurrencyForResponse = userCurrencyService.updateValue(localUserCurrency.get());
        return new ResponseEntity<>(userCurrencyForResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<UserCurrency> remove(@PathVariable("id") long id) throws ObjectNotFoundException {
        Optional<UserCurrency> localUserCurrency = userCurrencyService.findOne(id);
        if (!localUserCurrency.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object UserCurrency with id " + id +" has not been found for DELETION.");
        }
        userCurrencyService.delete(id);
        return new ResponseEntity<>(localUserCurrency.get(), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<UserCurrency> getOne(@PathVariable("id") Long id) throws ObjectNotFoundException {
        Optional<UserCurrency> localUserCurrency = userCurrencyService.findOne(id);
        if(!localUserCurrency.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object UserCurrency with id " + id +" has not been found for GETTING.");
        }
        return new ResponseEntity<>(localUserCurrency.get(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/by_uid/{id}")
    public ResponseEntity<List<UserCurrency>> getByUserId(@PathVariable("id") Long id) throws ObjectNotFoundException {
        Optional<List<UserCurrency>> optionalUserCurrencyList = Optional.ofNullable(userCurrencyService.findByUserId(id).collect(Collectors.toList()));
        if (optionalUserCurrencyList.get().size() == 0) {
            throw new ObjectNotFoundException("REST Controller: All UserCurrency Objects for UserId " + id + " have not been found.");
        }
        return new ResponseEntity<>(optionalUserCurrencyList.get(), HttpStatus.OK);
    }
}
