package ru.practicum.ExploreWithMe.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ExploreWithMe.exception.Error;
import ru.practicum.ExploreWithMe.exception.NotValidDateTimeFormatException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<Error> catchUserNotFoundException(NotValidDateTimeFormatException e) {
        return new ResponseEntity<>(new Error(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}