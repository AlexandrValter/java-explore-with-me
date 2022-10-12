package ru.practicum.ExploreWithMe.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ExploreWithMe.model.EventParam;
import ru.practicum.ExploreWithMe.model.dto.*;
import ru.practicum.ExploreWithMe.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@RestController
public class EventController {
    private final EventService eventService;
    private static final String APP = "Explore with me";

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/users/{userId}/events")
    public EventFullDto createEvent(@PathVariable long userId,
                                    @RequestBody NewEventDto newEventDto) {
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events")
    public Collection<EventShortDto> getAllUserEvents(@PathVariable long userId,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                      @RequestParam(defaultValue = "10") @Positive int size) {
        return eventService.getAllUserEvents(userId, from, size);
    }

    @PatchMapping("/users/{userId}/events")
    public EventFullDto updateEvent(@PathVariable long userId,
                                    @RequestBody UpdateEventRequest updateEventRequest) {
        return eventService.updateEvent(userId, updateEventRequest);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getEvent(@PathVariable long userId,
                                 @PathVariable long eventId) {
        return eventService.getEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto cancelEvent(@PathVariable long userId,
                                    @PathVariable long eventId) {
        return eventService.cancelEvent(userId, eventId);
    }

    @GetMapping("/admin/events")
    public Collection<EventFullDto> searchEvents(@RequestParam(required = false) int[] users,
                                                 @RequestParam(required = false) String[] states,
                                                 @RequestParam(required = false) int[] categories,
                                                 @RequestParam(required = false) String rangeStart,
                                                 @RequestParam(required = false) String rangeEnd,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "10") @Positive int size) {
        return eventService.searchEventsPrivate(new EventParam(users, states, categories, rangeStart, rangeEnd), from, size);
    }

    @PutMapping("/admin/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable long eventId,
                                    @RequestBody NewEventDto newEventDto) {
        return eventService.updateEvent(eventId, newEventDto);
    }

    @PatchMapping("/admin/events/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable long eventId) {
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("/admin/events/{eventId}/reject")
    public EventFullDto cancelEvent(@PathVariable long eventId) {
        return eventService.cancelEvent(eventId);
    }

    @GetMapping("/events")
    public Collection<EventShortDto> searchEvents(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) int[] categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false) String rangeStart,
                                                  @RequestParam(required = false) String rangeEnd,
                                                  @RequestParam(required = false) Boolean onlyAvailable,
                                                  @RequestParam(required = false) String sort,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size,
                                                  HttpServletRequest request) {
        return eventService.searchEventsPublic(
                new EventParam(
                        categories,
                        rangeStart,
                        rangeEnd,
                        text,
                        paid,
                        onlyAvailable,
                        sort),
                new EndpointHitDto(
                        APP,
                        request.getRequestURI(),
                        request.getRemoteAddr(),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
                from, size);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEvent(@PathVariable long id, HttpServletRequest request) {
        return eventService.getEvent(
                id,
                new EndpointHitDto(
                        APP,
                        request.getRequestURI(),
                        request.getRemoteAddr(),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
    }
}
