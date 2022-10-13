package ru.practicum.ExploreWithMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ExploreWithMe.model.EndpointHit;
import ru.practicum.ExploreWithMe.model.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collection;

public interface HitRepository extends JpaRepository<EndpointHit, Long> {


    @Query(value = "select new ru.practicum.ExploreWithMe.model.ViewStatsDto(count (e.ip), e.app, e.uri) " +
            "from EndpointHit e where e.uri in :uris and e.timestamp between :start and :end group by e.app, e.uri")
    Collection<ViewStatsDto> getStats(@Param("uris") String[] uris,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    @Query(value = "select new ru.practicum.ExploreWithMe.model.ViewStatsDto(count (distinct e.ip), e.app, e.uri) " +
            "from EndpointHit e where e.uri in :uris and e.timestamp between :start and :end group by e.app, e.uri")
    Collection<ViewStatsDto> getUniqueStats(@Param("uris") String[] uris,
                                            @Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);
}