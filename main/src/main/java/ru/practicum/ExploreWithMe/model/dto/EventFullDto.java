package ru.practicum.ExploreWithMe.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ExploreWithMe.model.Category;
import ru.practicum.ExploreWithMe.model.State;

@Data
@NoArgsConstructor
public class EventFullDto {
    private String annotation;
    private Category category;
    private int confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private long id;
    private UserDto initiator;
    private LocationDto location;
    private Boolean paid;
    private int participantLimit;
    private String publishedOn;
    private boolean requestModeration;
    private State state;
    private String title;
    private int views;

    public EventFullDto(
            long id,
            String annotation,
            String description,
            String createdOn,
            String eventDate,
            Boolean paid,
            int participantLimit,
            Boolean requestModeration,
            State state,
            String title,
            Category category,
            UserDto initiator,
            LocationDto location
    ) {
        this.id = id;
        this.annotation = annotation;
        this.description = description;
        this.createdOn = createdOn;
        this.eventDate = eventDate;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.requestModeration = requestModeration;
        this.state = state;
        this.title = title;
        this.category = category;
        this.initiator = initiator;
        this.location = location;
    }
}