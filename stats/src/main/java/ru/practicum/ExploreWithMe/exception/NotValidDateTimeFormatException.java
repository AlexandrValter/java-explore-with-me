package ru.practicum.ExploreWithMe.exception;

public class NotValidDateTimeFormatException extends RuntimeException {
    public NotValidDateTimeFormatException(String message) {
        super(message);
    }
}