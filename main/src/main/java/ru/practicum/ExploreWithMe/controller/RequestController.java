package ru.practicum.ExploreWithMe.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ExploreWithMe.model.dto.ParticipationRequestDto;
import ru.practicum.ExploreWithMe.service.RequestService;

import java.util.Collection;

@RestController
public class RequestController {
    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/users/{userId}/requests")
    public ParticipationRequestDto createRequest(@PathVariable long userId,
                                                 @RequestParam long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping("/users/{userId}/requests")
    public Collection<ParticipationRequestDto> getRequests(@PathVariable long userId) {
        return requestService.getRequests(userId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable long userId,
                                                 @PathVariable long requestId) {
        return requestService.cancelRequests(userId, requestId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public Collection<ParticipationRequestDto> getUserRequests(@PathVariable long userId,
                                                               @PathVariable long eventId) {
        return requestService.getUserRequests(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectRequest(@PathVariable long userId,
                                                 @PathVariable long reqId,
                                                 @PathVariable long eventId) {
        return requestService.rejectRequests(userId, reqId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequest(@PathVariable long userId,
                                                  @PathVariable long reqId,
                                                  @PathVariable long eventId) {
        return requestService.confirmRequests(userId, reqId, eventId);
    }
}
