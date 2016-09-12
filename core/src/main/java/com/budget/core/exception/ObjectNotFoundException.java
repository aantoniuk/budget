package com.budget.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Description:
 * <p>
 * -----------------------------------------------------
 * Created by kremezniy on 9/8/2016.
 */
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Exception class: There is no such object")
public class ObjectNotFoundException extends Exception {
    public ObjectNotFoundException(String message) {
        super(message);
    }
}
