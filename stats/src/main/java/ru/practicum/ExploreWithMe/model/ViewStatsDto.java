package ru.practicum.ExploreWithMe.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ViewStatsDto {
    private long hits;
    private String app;
    private String uri;

    public ViewStatsDto(long hits, String app, String uri) {
        this.hits = hits;
        this.app = app;
        this.uri = uri;
    }
}