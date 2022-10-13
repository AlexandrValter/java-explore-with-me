package ru.practicum.ExploreWithMe.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ExploreWithMe.exception.*;
import ru.practicum.ExploreWithMe.mapper.RequestMapper;
import ru.practicum.ExploreWithMe.model.*;
import ru.practicum.ExploreWithMe.model.dto.ParticipationRequestDto;
import ru.practicum.ExploreWithMe.repository.EventRepository;
import ru.practicum.ExploreWithMe.repository.RequestRepository;
import ru.practicum.ExploreWithMe.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public RequestServiceImpl(RequestRepository requestRepository,
                              UserRepository userRepository,
                              EventRepository eventRepository,
                              EventService eventService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(long userId, long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("User with id=%s was not found.", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(
                String.format("Event with id=%s was not found.", eventId)));
        if (event.getInitiator().getId() != userId) {
            if (event.getState().equals(State.PUBLISHED)) {
                ParticipationRequest request = new ParticipationRequest(LocalDateTime.now(), event, user);
                if (!event.getRequestModeration()) {
                    request.setStatus(Status.CONFIRMED);
                } else if (event.getParticipantLimit() == 0 ||
                        event.getParticipantLimit() > requestRepository.getCountConfirmedRequests(
                                eventId, Status.CONFIRMED)) {
                    request.setStatus(Status.PENDING);
                } else throw new CreateRequestException(String.format(
                        "The event id=%s has exceeded the limit of requests for participation", eventId));
                try {
                    log.info("Добавлен новый запрос пользователем id={} к событию id={}", userId, eventId);
                    return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
                } catch (DataIntegrityViolationException e) {
                    throw new CreateRequestException(String.format(
                            "User id=%s has already made a request to participate in the event id=%s",
                            userId, eventId));
                }
            } else throw new CreateRequestException(String.format(
                    "User id=%s cannot submit a request to participate for an unpublished event id=%s",
                    userId, eventId));
        } else throw new CreateRequestException(String.format(
                "User id=%s cannot submit a request to participate in his event id=%s", userId, eventId));
    }

    @Override
    public Collection<ParticipationRequestDto> getRequests(long userId) {
        log.info("Запрошены все запросы пользователя id={}", userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequests(long userId, long requestId) {
        ParticipationRequest request = requestRepository.findParticipationRequestByRequesterIdAndId(userId, requestId);
        if (request != null) {
            log.info("Пользователь id={} отменил запрос id={}", userId, requestId);
            if (request.getStatus().equals(Status.CANCELED)) {
                return RequestMapper.toParticipationRequestDto(request);
            } else {
                request.setStatus(Status.CANCELED);
                return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
            }
        } else {
            throw new RequestNotFoundException(String.format(
                    "Request id=%s from user id=%s was not found.", requestId, userId));
        }
    }

    @Override
    public Collection<ParticipationRequestDto> getUserRequests(long userId, long eventId) {
        eventRepository.findEventByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new EventNotFoundException(
                String.format("User id=%s has not event id=%s.", userId, eventId)));
        log.info("Пользователь id={} запрашивает запросы к событию id={}", userId, eventId);
        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto rejectRequests(long userId, long reqId, long eventId) {
        eventRepository.findEventByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new EventNotFoundException(String.format("User id=%s has not event id=%s.", userId, eventId)));
        ParticipationRequest request = requestRepository.findParticipationRequestByEventIdAndId(eventId, reqId)
                .orElseThrow(() -> new RequestNotFoundException(String.format("Request id=%s was not found.", reqId)));
        if (request.getStatus().equals(Status.PENDING)) {
            request.setStatus(Status.REJECTED);
            log.info("Пользователь id={} отклонил запрос id={} к событию id={}", userId, reqId, eventId);
            return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
        } else {
            throw new RequestUpdateException("Only pending request can be rejected");
        }
    }

    @Override
    @Transactional
    public ParticipationRequestDto confirmRequests(long userId, long reqId, long eventId) {
        ParticipationRequestDto result;
        Event event = eventRepository.findEventByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new EventNotFoundException(String.format("User id=%s has not event id=%s.", userId, eventId)));
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new RequestUpdateException(String.format(
                    "Event id=%s confirmation of events is not required", eventId));
        }
        int count = requestRepository.getCountConfirmedRequests(eventId, Status.CONFIRMED);
        if (event.getParticipantLimit() > count) {
            ParticipationRequest request = requestRepository.findParticipationRequestByEventIdAndId(eventId, reqId)
                    .orElseThrow(() -> new RequestNotFoundException(String.format("Request id=%s was not found.", reqId)));
            if (request.getStatus().equals(Status.PENDING)) {
                request.setStatus(Status.CONFIRMED);
                result = RequestMapper.toParticipationRequestDto(requestRepository.save(request));
                if (event.getParticipantLimit() == count + 1) {
                    rejectAllRequests(eventId);
                }
            } else throw new RequestUpdateException("Only pending request can be rejected");
        } else throw new CreateRequestException(String.format(
                "The event id=%s has exceeded the limit of requests for participation", eventId));
        log.info("Пользователь id={} одобрил запрос id={} к событию id={}", userId, reqId, eventId);
        return result;
    }

    private void rejectAllRequests(long eventId) {
        List<ParticipationRequest> requests = requestRepository.findAllByEventIdAndStatus(
                eventId, Status.PENDING.toString());
        if (requests.size() > 0) {
            requests.stream()
                    .peek(s -> s.setStatus(Status.REJECTED))
                    .forEach(requestRepository::save);
        }
    }
}
