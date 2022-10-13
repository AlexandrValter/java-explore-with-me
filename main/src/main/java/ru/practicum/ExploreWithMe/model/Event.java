package ru.practicum.ExploreWithMe.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "events")
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String annotation;
    private String description;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Enumerated(value = EnumType.STRING)
    private State state;
    private String title;
    @ManyToMany()
    @JoinTable(
            name = "events_compilations",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "compilation_id")
    )
    private Set<Compilation> compilations;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Category category;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private User initiator;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Location location;

    public Event(
            String annotation,
            String description,
            LocalDateTime eventDate,
            boolean paid,
            int participantLimit,
            boolean requestModeration,
            String title
    ) {
        this.annotation = annotation;
        this.description = description;
        this.eventDate = eventDate;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.requestModeration = requestModeration;
        this.title = title;
    }

    public Event(
            String annotation,
            String description,
            LocalDateTime eventDate,
            boolean paid,
            int participantLimit,
            String title
    ) {
        this.annotation = annotation;
        this.description = description;
        this.eventDate = eventDate;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.title = title;
    }
}