package ru.practicum.ExploreWithMe.mapper;

import ru.practicum.ExploreWithMe.model.ParticipationRequest;
import ru.practicum.ExploreWithMe.model.dto.ParticipationRequestDto;

import java.time.format.DateTimeFormatter;

public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return new ParticipationRequestDto(
                participationRequest.getId(),
                participationRequest.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                participationRequest.getStatus(),
                participationRequest.getEvent().getId(),
                participationRequest.getRequester().getId());
    }
}