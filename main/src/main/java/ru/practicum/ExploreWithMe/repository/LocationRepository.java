package ru.practicum.ExploreWithMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ExploreWithMe.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findLocationByLatitudeAndLongitude(double lat, double lon);
}