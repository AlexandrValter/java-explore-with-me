package ru.practicum.ExploreWithMe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.ExploreWithMe.model.Event;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor,
        PagingAndSortingRepository<Event, Long> {

    Page<Event> findAllByInitiatorId(long userId, Pageable pageable);

    Optional<Event> findEventByIdAndInitiatorId(long eventId, long initiatorId);

    @Query(value = "select count (e.id) from Event e where e.category.id = ?1")
    int getCountEventWithCategory(long categoryId);

    @Query(value = "select e from Event e order by size(e.likes) desc")
    Page<Event> findPopularEvents(Pageable pageable);
}