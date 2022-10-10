package ru.practicum.ExploreWithMe.service;

import ru.practicum.ExploreWithMe.model.dto.CompilationDto;
import ru.practicum.ExploreWithMe.model.dto.NewCompilationDto;

import java.util.Collection;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(long compId);

    void deleteEventFromCompilation(long compId, long eventId);

    void addEventToCompilation(long compId, long eventId);

    void unpinCompilation(long compId);

    void pinCompilation(long compId);

    Collection<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilation(long compId);
}
