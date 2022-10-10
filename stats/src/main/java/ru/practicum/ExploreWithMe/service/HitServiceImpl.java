package ru.practicum.ExploreWithMe.service;

import org.springframework.stereotype.Service;
import ru.practicum.ExploreWithMe.exception.NotValidDateTimeFormatException;
import ru.practicum.ExploreWithMe.mapper.EndpointHitMapper;
import ru.practicum.ExploreWithMe.model.EndpointHitDto;
import ru.practicum.ExploreWithMe.model.ViewStatsDto;
import ru.practicum.ExploreWithMe.repository.HitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;

@Service
public class HitServiceImpl implements HitService {
    private final HitRepository repository;

    public HitServiceImpl(HitRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createHit(EndpointHitDto endpointHitDto) {
        repository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
    }

    @Override
    public Collection<ViewStatsDto> getStats(String start, String end, String[] uris, Boolean unique) {
        LocalDateTime startRange;
        LocalDateTime endRange;
        try {
            startRange = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            endRange = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            throw new NotValidDateTimeFormatException("The date format should be 'yyyy-MM-dd HH:mm:ss'");
        }
        if (unique) {
            Collection<ViewStatsDto> a = repository.getUniqueStats(uris, startRange, endRange);
            return a;
        } else {
            Collection<ViewStatsDto> b = repository.getStats(uris, startRange, endRange);
            return b;
        }
    }
}
