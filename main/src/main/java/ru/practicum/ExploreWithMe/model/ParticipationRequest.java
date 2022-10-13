package ru.practicum.ExploreWithMe.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "requests")
@NoArgsConstructor
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDateTime created;
    @Enumerated(value = EnumType.STRING)
    private Status status;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Event event;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User requester;

    public ParticipationRequest(LocalDateTime created, Event event, User requester) {
        this.created = created;
        this.event = event;
        this.requester = requester;
    }
}
