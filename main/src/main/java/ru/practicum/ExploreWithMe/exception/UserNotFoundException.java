package ru.practicum.ExploreWithMe.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(long userId) {
        super(String.format("User with id=%s was not found.", userId));
    }
}
