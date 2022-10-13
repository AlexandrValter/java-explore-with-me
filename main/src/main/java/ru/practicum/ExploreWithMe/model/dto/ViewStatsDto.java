package ru.practicum.ExploreWithMe.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ViewStatsDto {
    private int hits;
    private String app;
    private String uri;
}