package ru.practicum.ExploreWithMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ExploreWithMe.model.ParticipationRequest;
import ru.practicum.ExploreWithMe.model.Status;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    @Query(value = "select count (p.id) from ParticipationRequest p where p.event.id = ?1 and p.status = ?2")
    int getCountConfirmedRequests(long event, Status status);

    Collection<ParticipationRequest> findAllByRequesterId(long userId);

    ParticipationRequest findParticipationRequestByRequesterIdAndId(long userId, long requestId);

    Collection<ParticipationRequest> findAllByEventId(long eventId);

    Optional<ParticipationRequest> findParticipationRequestByEventIdAndId(long eventId, long requestId);

    List<ParticipationRequest> findAllByEventIdAndStatus(long eventId, String status);
}
