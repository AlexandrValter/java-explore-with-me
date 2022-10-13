package ru.practicum.ExploreWithMe.mapper;

import ru.practicum.ExploreWithMe.model.Location;
import ru.practicum.ExploreWithMe.model.dto.LocationDto;

public class LocationMapper {
    public static Location toLocation(LocationDto locationDto) {
        return new Location(locationDto.getLat(), locationDto.getLon());
    }

    public static LocationDto toLocationDto(Location location) {
        return new LocationDto(location.getLatitude(), location.getLongitude());
    }
}
