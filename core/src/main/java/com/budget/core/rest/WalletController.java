package com.budget.core.rest;

import com.budget.core.entity.Wallet;
import com.budget.core.exception.ObjectAlreadyExists;
import com.budget.core.exception.ObjectNotFoundException;
import com.budget.core.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Wallet> create(@RequestBody Wallet wallet) {
        Wallet walletForResponse = new Wallet();
        try {
            walletForResponse = walletService.create(wallet);
        } catch (ObjectAlreadyExists oae) {
            throw new ObjectAlreadyExists("REST Controller: Object Currency with id " + wallet.getId() +" is already exist.");
        }
        return new ResponseEntity<>(walletForResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}/cur_id/{cur_id}")
    public ResponseEntity<Wallet> updateCurrencyId(@PathVariable("id") Long walletId, @PathVariable("cur_id") Long currencyId) throws ObjectNotFoundException {
        Wallet walletForResponse;
        try {
            walletForResponse = walletService.updateUserCurrencyId(walletId, currencyId);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("REST Controller: Object Wallet with id " + walletId + " already exists. So nothing to UPDATE.");
        } catch (NullPointerException npe) {
            throw new ObjectNotFoundException("REST Controller: Object Wallet with id " + walletId + " has not been found for UPDATE.");
        }
        return new ResponseEntity<>(walletForResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}/name/{name}")
    public ResponseEntity<Wallet> updateName(@PathVariable("id") Long walletId, @PathVariable("name") String name) throws ObjectNotFoundException {
        Wallet walletForResponse;
        try {
            walletForResponse = walletService.updateName(walletId, name);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("REST Controller: Object Wallet with id " + walletId + " and name " + name + " already exists. So nothing to UPDATE.");
        } catch (NullPointerException npe) {
            throw new ObjectNotFoundException("REST Controller: Object Wallet with id " + walletId + " has not been found for UPDATE.");
        }
        return new ResponseEntity<>(walletForResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<Wallet> remove(@PathVariable("id") Long id) throws ObjectNotFoundException {
        Optional<Wallet> walletOptional = walletService.findOne(id);
        if (!walletOptional.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object Wallet with id " + id +" has not been found for DELETION.");
        }
        walletService.delete(id);
        return new ResponseEntity<>(walletOptional.get(), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<Wallet> getOne(@PathVariable("id") Long id) throws ObjectNotFoundException {
        Optional<Wallet> localWallet = walletService.findOne(id);
        if(!localWallet.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: Object Wallet with id " + id +" has not been found for GETTING.");
        }
        return new ResponseEntity<>(localWallet.get(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/name/{name}/us_cur_id/{id}")
    public ResponseEntity<Wallet> getByNameAndUserCuurencyId(@PathVariable("name") String name, @PathVariable("id") Long id) throws ObjectNotFoundException {
        Optional<Wallet> walletOptional = walletService.findByNameAndUserCurrencyId(name, id).findAny();
        if (!walletOptional.isPresent()) {
            throw new ObjectNotFoundException("REST Controller: All Wallet Objects with Name " + name + " and UserId " + id + " have not been found.");
        }
        return new ResponseEntity<>(walletOptional.get(), HttpStatus.OK);
    }
}
