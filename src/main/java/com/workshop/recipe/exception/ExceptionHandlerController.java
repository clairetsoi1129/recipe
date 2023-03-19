package com.workshop.recipe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice(annotations = RestController.class)
public class ExceptionHandlerController {

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ErrorType> handleResourceNotFoundException(
            RuntimeException ex) {
        return new ResponseEntity<>(
                new ErrorType(ex.getMessage(), "USER_NOT_FOUND", "DATA NOT FOUND FOR GIVEN ID", "UserAccount"),
                HttpStatus.NOT_FOUND);
    }
}
