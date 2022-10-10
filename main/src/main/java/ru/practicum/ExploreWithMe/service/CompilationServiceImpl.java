package ru.practicum.ExploreWithMe.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ExploreWithMe.exception.CompilationNotFoundException;
import ru.practicum.ExploreWithMe.exception.CompilationUpdateException;
import ru.practicum.ExploreWithMe.exception.EventNotFoundException;
import ru.practicum.ExploreWithMe.mapper.CompilationMapper;
import ru.practicum.ExploreWithMe.mapper.EventMapper;
import ru.practicum.ExploreWithMe.model.Compilation;
import ru.practicum.ExploreWithMe.model.Event;
import ru.practicum.ExploreWithMe.model.dto.CompilationDto;
import ru.practicum.ExploreWithMe.model.dto.EventShortDto;
import ru.practicum.ExploreWithMe.model.dto.NewCompilationDto;
import ru.practicum.ExploreWithMe.repository.CompilationRepository;
import ru.practicum.ExploreWithMe.repository.EventRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final EventRepository eventRepository;
    private final EventServiceImpl eventService;
    private final CompilationRepository compilationRepository;

    public CompilationServiceImpl(EventRepository eventRepository,
                                  EventServiceImpl eventService,
                                  CompilationRepository compilationRepository) {
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.compilationRepository = compilationRepository;
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Set<EventShortDto> eventShortDtos = new HashSet<>();
        Set<Event> events = new HashSet<>();
        if (newCompilationDto.getEvents().size() > 0) {
            events = new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents()));
            eventShortDtos = eventService.getStats(List.copyOf(events)).stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toSet());
        }
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        compilation.setEvents(events);
        CompilationDto result = CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
        result.setEvents(eventShortDtos);
        log.info("Добавлена новая подборка событий id={}", result.getId());
        return result;
    }

    @Override
    public void deleteCompilation(long compId) {
        compilationRepository.findById(compId).orElseThrow(
                () -> new CompilationNotFoundException(String.format("Compilation id=%s is not found", compId)));
        compilationRepository.deleteById(compId);
        log.info("Удалена подборка событий id={}", compId);
    }

    @Override
    public void deleteEventFromCompilation(long compId, long eventId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new CompilationNotFoundException(String.format("Compilation id=%s is not found", compId)));
        Event deleteEvent = new Event();
        for (Event event : compilation.getEvents()) {
            if (event.getId() == eventId) {
                deleteEvent = event;
            }
        }
        if (deleteEvent.getId() == eventId) {
            compilation.getEvents().remove(deleteEvent);
            compilationRepository.save(compilation);
            log.info("Удалено событие id={} из подборки id={}", eventId, compId);
        } else {
            throw new EventNotFoundException(String.format("Event with id=%s was not found.", eventId));
        }
    }

    @Override
    public void addEventToCompilation(long compId, long eventId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new CompilationNotFoundException(String.format("Compilation id=%s is not found", compId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(
                String.format("Event with id=%s was not found.", eventId)));
        compilation.getEvents().add(event);
        compilationRepository.save(compilation);
        log.info("Добавлено событие id={} в подборку id={}", eventId, compId);
    }

    @Override
    public void unpinCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new CompilationNotFoundException(String.format("Compilation id=%s is not found", compId)));
        if (compilation.isPinned()) {
            compilation.setPinned(false);
            compilationRepository.save(compilation);
            log.info("Подборка id={} откреплена от главной страницы", compId);
        } else {
            throw new CompilationUpdateException(String.format("Compilation id=%s was not pinned", compId));
        }
    }

    @Override
    public void pinCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new CompilationNotFoundException(String.format("Compilation id=%s is not found", compId)));
        if (!compilation.isPinned()) {
            compilation.setPinned(true);
            compilationRepository.save(compilation);
            log.info("Подборка id={} закреплена на главной странице", compId);
        } else {
            throw new CompilationUpdateException(String.format("Compilation id=%s is already pinned", compId));
        }
    }

    @Override
    public Collection<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        List<Compilation> compilationList;
        if (pinned != null) {
            compilationList = compilationRepository.findAllByPinned(pinned, pageable).getContent();
        } else {
            compilationList = compilationRepository.findAll(pageable).getContent();
        }
        if (compilationList.size() > 0) {
            List<CompilationDto> result = new ArrayList<>();
            for (Compilation compilation : compilationList) {
                Set<EventShortDto> eventShortDtos =
                        eventService.getStats(List.copyOf(compilation.getEvents())).stream()
                                .map(EventMapper::toEventShortDto).collect(Collectors.toSet());
                CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);
                compilationDto.setEvents(eventShortDtos);
                result.add(compilationDto);
            }
            return result;
        } else return Collections.emptyList();
    }

    @Override
    public CompilationDto getCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new CompilationNotFoundException(String.format("Compilation id=%s is not found", compId)));
        CompilationDto result = CompilationMapper.toCompilationDto(compilation);
        result.setEvents(eventService.getStats(List.copyOf(compilation.getEvents())).stream()
                .map(EventMapper::toEventShortDto).collect(Collectors.toSet()));
        log.info("Запрошена подборка id={}", compId);
        return result;
    }
}