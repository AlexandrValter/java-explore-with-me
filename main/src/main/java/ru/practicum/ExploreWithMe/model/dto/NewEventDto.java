package ru.practicum.ExploreWithMe.model.dto;

import lombok.Data;

@Data
public class NewEventDto {
    private String annotation;
    private long category;
    private String description;
    private String eventDate;
    private LocationDto location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private String title;
}
