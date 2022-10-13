package ru.practicum.ExploreWithMe.model.dto;

import lombok.Data;

import java.util.Set;

@Data
public class NewCompilationDto {
    private Set<Long> events;
    private Boolean pinned;
    private String title;
}
