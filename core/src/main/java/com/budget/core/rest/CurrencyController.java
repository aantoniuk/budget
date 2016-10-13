package com.budget.core.rest;

import com.budget.core.entity.Currency;
import com.budget.core.exception.ObjectAlreadyExists;
import com.budget.core.exception.ObjectNotFoundException;
import com.budget.core.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

//@RestController
@RequestMapping("/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    /*@RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Currency> create(@RequestBody Currency currency) throws ObjectAlreadyExists {
        Optional<Currency> localCurrency = currencyService.findByName(currency.getName());
        if (localCurrency.isPresent()) {
            throw new ObjectAlreadyExists("REST Controller: Object Currency with id " + localCurrency.get().getId() +" is already exist.");
        }
        Currency categoryForResponse = currencyService.save(currency);
        return new ResponseEntity<>(currency, HttpStatus.CREATED);
    }*/

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public ResponseEntity<Currency> update(@PathVariable("id") Long id, @RequestBody Currency currency) throws ObjectNotFoundException {
        Optional<Currency> localCurrency = currencyService.findOne(id);
        if(!localCurrency.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object Currency with id " + id +" has not been found for UPDATE.");
        }
        if (currency.getName() != null) {
            localCurrency.get().setName(currency.getName());
        }
        currencyService.save(currency);
        return new ResponseEntity<>(localCurrency.get(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<Currency> remove(@PathVariable("id") long id) throws ObjectNotFoundException {
        Optional<Currency> localCurrency = currencyService.findOne(id);
        if (!localCurrency.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object Currency with id " + id +" has not been found for DELETION.");
        }
        currencyService.delete(id);
        return new ResponseEntity<>(localCurrency.get(), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<Currency> getOne(@PathVariable("id") long id) throws ObjectNotFoundException {
        Optional<Currency> localCurrency = currencyService.findOne(id);
        if(!localCurrency.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object Currency with id " + id +" has not been found for GETTING.");
        }
        return new ResponseEntity<>(localCurrency.get(), HttpStatus.OK);
    }

    /*@RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Currency>> getAll() throws ObjectNotFoundException {
        Stream<Currency> currencyList = currencyService.findAll();
        if (!currencyList.findFirst().isPresent()) {
            throw new ObjectNotFoundException("REST Controller: All Currency Objects have not been found.");
        }
        return new ResponseEntity<>(currencyList, HttpStatus.OK);
    }*/
}
