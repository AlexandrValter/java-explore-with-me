package ru.practicum.ExploreWithMe.service;

import ru.practicum.ExploreWithMe.model.EndpointHitDto;
import ru.practicum.ExploreWithMe.model.ViewStatsDto;

import java.util.Collection;

public interface HitService {
    void createHit(EndpointHitDto endpointHitDto);

    Collection<ViewStatsDto> getStats(String start, String end, String[] uris, Boolean unique);
}
