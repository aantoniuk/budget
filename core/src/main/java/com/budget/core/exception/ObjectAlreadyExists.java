package com.budget.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Description:
 * <p>
 * -----------------------------------------------------
 * Created by kremezniy on 9/8/2016.
 */
@ResponseStatus(value= HttpStatus.CONFLICT, reason="Exception class: Object is already exist")
public class ObjectAlreadyExists extends Exception {
    public ObjectAlreadyExists(String message) {
        super(message);
    }
}
