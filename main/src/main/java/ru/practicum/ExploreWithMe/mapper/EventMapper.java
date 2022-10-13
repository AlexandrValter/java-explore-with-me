package ru.practicum.ExploreWithMe.mapper;

import ru.practicum.ExploreWithMe.model.Event;
import ru.practicum.ExploreWithMe.model.dto.EventFullDto;
import ru.practicum.ExploreWithMe.model.dto.EventShortDto;
import ru.practicum.ExploreWithMe.model.dto.NewEventDto;
import ru.practicum.ExploreWithMe.model.dto.UpdateEventRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {
    public static Event toEvent(NewEventDto newEventDto) {
        Event event = new Event(
                newEventDto.getAnnotation(),
                newEventDto.getDescription(),
                LocalDateTime.parse(newEventDto.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                newEventDto.isPaid(),
                newEventDto.getParticipantLimit(),
                newEventDto.isRequestModeration(),
                newEventDto.getTitle()
        );
        if (newEventDto.getLocation() != null) {
            event.setLocation(LocationMapper.toLocation(newEventDto.getLocation()));
        }
        return event;
    }

    public static EventFullDto toEventFullDto(Event event) {
        EventFullDto response = new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                event.getDescription(),
                event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getCategory(),
                UserMapper.toUserDto(event.getInitiator()),
                LocationMapper.toLocationDto(event.getLocation())
        );
        if (event.getPublishedOn() != null) {
            response.setPublishedOn(
                    event.getPublishedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
        }
        return response;
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getAnnotation(),
                event.getCategory(),
                event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                event.getId(),
                UserMapper.toUserDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle()
        );
    }

    public static Event toEvent(UpdateEventRequest updateEventRequest) {
        return new Event(
                updateEventRequest.getAnnotation(),
                updateEventRequest.getDescription(),
                LocalDateTime.parse(updateEventRequest.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                updateEventRequest.isPaid(),
                updateEventRequest.getParticipantLimit(),
                updateEventRequest.getTitle()
        );
    }

    public static EventShortDto toEventShortDto(EventFullDto eventFullDto) {
        return new EventShortDto(
                eventFullDto.getAnnotation(),
                eventFullDto.getCategory(),
                eventFullDto.getEventDate(),
                eventFullDto.getId(),
                eventFullDto.getInitiator(),
                eventFullDto.getPaid(),
                eventFullDto.getTitle(),
                eventFullDto.getViews()
        );
    }
}