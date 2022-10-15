package ru.practicum.ExploreWithMe.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String message) {
        super(message);
    }

    public EventNotFoundException(long eventId) {
        super(String.format("Event with id=%s was not found.", eventId));
    }

}
