package ru.practicum.ExploreWithMe.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ExploreWithMe.model.EndpointHitDto;
import ru.practicum.ExploreWithMe.model.ViewStatsDto;
import ru.practicum.ExploreWithMe.service.HitService;

import java.util.Collection;

@RestController
public class HitController {
    private final HitService hitService;

    public HitController(HitService hitService) {
        this.hitService = hitService;
    }

    @PostMapping("/hit")
    public void createHit(@RequestBody EndpointHitDto endpointHitDto) {
        hitService.createHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public Collection<ViewStatsDto> getStats(@RequestParam(required = false) String start,
                                             @RequestParam(required = false) String end,
                                             @RequestParam String[] uris,
                                             @RequestParam(defaultValue = "false") Boolean unique) {
        return hitService.getStats("2000-01-01 01:01:10", "2030-01-01 01:01:10", uris, unique);
    }
}