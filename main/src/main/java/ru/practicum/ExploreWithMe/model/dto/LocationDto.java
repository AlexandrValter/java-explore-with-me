package ru.practicum.ExploreWithMe.model.dto;

import lombok.Data;

@Data
public class LocationDto {
    private double lat;
    private double lon;

    public LocationDto(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}