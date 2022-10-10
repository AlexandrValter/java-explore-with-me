package ru.practicum.ExploreWithMe.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class CompilationDto {
    private Set<EventShortDto> events;
    private long id;
    private Boolean pinned;
    private String title;

    public CompilationDto(long id, Boolean pinned, String title) {
        this.id = id;
        this.pinned = pinned;
        this.title = title;
    }
}
