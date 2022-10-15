package ru.practicum.ExploreWithMe.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ExploreWithMe.model.Category;

@Data
@NoArgsConstructor
public class EventShortDto {
    private String annotation;
    private Category category;
    private int confirmedRequests;
    private String eventDate;
    private long id;
    private UserDto initiator;
    private boolean paid;
    private String title;
    private int views;
    private int likes;

    public EventShortDto(String annotation,
                         Category category,
                         String eventDate,
                         long id,
                         UserDto initiator,
                         boolean paid,
                         String title) {
        this.annotation = annotation;
        this.category = category;
        this.eventDate = eventDate;
        this.id = id;
        this.initiator = initiator;
        this.paid = paid;
        this.title = title;
    }

    public EventShortDto(String annotation,
                         Category category,
                         String eventDate,
                         long id,
                         UserDto initiator,
                         boolean paid,
                         String title,
                         int views) {
        this.annotation = annotation;
        this.category = category;
        this.eventDate = eventDate;
        this.id = id;
        this.initiator = initiator;
        this.paid = paid;
        this.title = title;
        this.views = views;
    }
}
