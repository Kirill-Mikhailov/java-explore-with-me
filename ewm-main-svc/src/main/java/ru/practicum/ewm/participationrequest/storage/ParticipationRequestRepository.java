package ru.practicum.ewm.participationrequest.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.participationrequest.model.ParticipationRequest;
import ru.practicum.ewm.participationrequest.model.ParticipationRequestStatus;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findByRequesterId(Long requesterId);

    List<ParticipationRequest> findByEventId(Long eventId);

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    Integer countAllByEventIdAndStatus(Long eventId, ParticipationRequestStatus status);

    List<ParticipationRequest> findByEventIdAndStatusAndIdIn(Long eventId, ParticipationRequestStatus status,
                                                                List<Long> requestIds);
}
