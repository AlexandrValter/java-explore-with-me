package ru.practicum.ExploreWithMe.service;

import ru.practicum.ExploreWithMe.model.dto.ParticipationRequestDto;

import java.util.Collection;

public interface RequestService {
    ParticipationRequestDto createRequest(long userId, long eventId);

    Collection<ParticipationRequestDto> getRequests(long userId);

    ParticipationRequestDto cancelRequests(long userId, long requestId);

    Collection<ParticipationRequestDto> getUserRequests(long userId, long eventId);

    ParticipationRequestDto rejectRequests(long userId, long reqId, long eventId);

    ParticipationRequestDto confirmRequests(long userId, long reqId, long eventId);
}
