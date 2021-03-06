package com.budget.core.handlers;

import com.budget.core.exception.ObjectAlreadyExists;
import com.budget.core.exception.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Description:
 * <p>
 * -----------------------------------------------------
 * Created by kremezniy on 9/12/2016.
 */
@ControllerAdvice
public class ExceptionHandlingController {
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "HandlerController: Object has not been found.")
    @ExceptionHandler(ObjectNotFoundException.class)
    public void objectNotFound() {}

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "HandlerController: NPE: Object has not been found.")
    @ExceptionHandler(NullPointerException.class)
    public void nullPointerException() {}

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "HandlerController: Object is already exists.")
    @ExceptionHandler(ObjectAlreadyExists.class)
    public void objectAlreadyExists() {}

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "HandlerController: Object is same and can't be updated.")
    @ExceptionHandler(IllegalArgumentException.class)
    public void illegalArgumentException() {}

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "HandlerController: Wrong request to Controller.")
    @ExceptionHandler(UnsupportedOperationException.class)
    public void unsupportedOperationException() {}
}
