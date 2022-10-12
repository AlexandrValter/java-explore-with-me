package ru.practicum.ExploreWithMe.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "statistics")
@NoArgsConstructor
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;

    public EndpointHit(String app, String uri, String ip, LocalDateTime timestamp) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timestamp = timestamp;
    }
}