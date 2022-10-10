package ru.practicum.ExploreWithMe.model.dto;

import lombok.Data;

@Data
public class UpdateEventRequest {
    private String annotation;
    private long category;
    private String description;
    private String eventDate;
    private long eventId;
    private boolean paid;
    private int participantLimit;
    private String title;
}
