package ru.practicum.ExploreWithMe.exception;

public class CreateRequestException extends RuntimeException {
    public CreateRequestException(String message) {
        super(message);
    }
}