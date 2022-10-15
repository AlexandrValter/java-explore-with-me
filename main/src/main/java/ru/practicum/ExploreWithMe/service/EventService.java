package ru.practicum.ExploreWithMe.service;

import ru.practicum.ExploreWithMe.model.EventParam;
import ru.practicum.ExploreWithMe.model.dto.*;

import java.util.Collection;

public interface EventService {
    EventFullDto createEvent(long userId, NewEventDto eventDtoPost);

    Collection<EventShortDto> getAllUserEvents(long userId, int from, int size);

    EventFullDto updateEvent(long userId, UpdateEventRequest updateEventRequest);

    EventFullDto getEvent(long userId, long eventId);

    EventFullDto cancelEvent(long userId, long eventId);

    Collection<EventFullDto> searchEventsPrivate(EventParam param, int from, int size);

    EventFullDto updateEvent(long eventId, NewEventDto newEventDto);

    EventFullDto publishEvent(long eventId);

    EventFullDto cancelEvent(long eventId);

    Collection<EventShortDto> searchEventsPublic(EventParam param, EndpointHitDto endpointHitDto, int from, int size);

    EventFullDto getEvent(long id, EndpointHitDto endpointHitDto);

    EventFullDto addLike(long userId, long eventId);

    void deleteLike(long userId, long eventId);

    Collection<EventShortDto> getPopularEvents(int from, int size);
}
