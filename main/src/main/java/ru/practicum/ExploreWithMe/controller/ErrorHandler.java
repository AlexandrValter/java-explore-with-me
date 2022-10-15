package ru.practicum.ExploreWithMe.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ExploreWithMe.exception.Error;
import ru.practicum.ExploreWithMe.exception.*;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<Error> catchUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(new Error(HttpStatus.NOT_FOUND, e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Error> catchCategoryNotFoundException(CategoryNotFoundException e) {
        return new ResponseEntity<>(new Error(HttpStatus.NOT_FOUND, e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Error> catchEventDateTimeException(EventDateTimeException e) {
        return new ResponseEntity<>(new Error(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Error> catchEventNotFoundException(EventNotFoundException e) {
        return new ResponseEntity<>(new Error(HttpStatus.NOT_FOUND, e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Error> catchEventUpdateException(EventUpdateException e) {
        return new ResponseEntity<>(new Error(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Error> catchRequestEventException(RequestEventException e) {
        return new ResponseEntity<>(new Error(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Error> catchCreateRequestException(CreateRequestException e) {
        return new ResponseEntity<>(new Error(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Error> catchRequestNotFoundException(RequestNotFoundException e) {
        return new ResponseEntity<>(new Error(HttpStatus.NOT_FOUND, e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Error> catchRequestUpdateException(RequestUpdateException e) {
        return new ResponseEntity<>(new Error(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Error> catchCreateCompilationException(CreateCompilationException e) {
        return new ResponseEntity<>(new Error(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Error> catchCompilationNotFoundException(CompilationNotFoundException e) {
        return new ResponseEntity<>(new Error(HttpStatus.NOT_FOUND, e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Error> catchCompilationUpdateException(CompilationUpdateException e) {
        return new ResponseEntity<>(new Error(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Error> catchDeleteCategoryException(DeleteCategoryException e) {
        return new ResponseEntity<>(new Error(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Error> catchAddLikeException(AddLikeException e) {
        return new ResponseEntity<>(new Error(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Error> catchDeleteLikeException(DeleteLikeException e) {
        return new ResponseEntity<>(new Error(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}