package ru.practicum.ExploreWithMe.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
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
