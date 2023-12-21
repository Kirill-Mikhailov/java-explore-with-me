package ru.practicum.ewm.participationrequest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.exception.ConditionsAreNotMetException;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.RequestNotFoundException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.participationrequest.dto.ParticipationRequestDto;
import ru.practicum.ewm.participationrequest.dto.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.participationrequest.model.ParticipationRequest;
import ru.practicum.ewm.participationrequest.model.ParticipationRequestStatus;
import ru.practicum.ewm.participationrequest.storage.ParticipationRequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getUsersRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id=" + userId + " was not found");
        }
        return participationRequestRepository.findByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto saveRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConditionsAreNotMetException("The initiator of the event cannot add a request to " +
                    "participate in his event");
        }
        if (participationRequestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConditionsAreNotMetException("Request with userId=" + userId + " and eventId="
                    + eventId + " already exists");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConditionsAreNotMetException("Event with id=" + eventId + " has not been published");
        }
        if (event.getParticipantLimit() != 0 && participationRequestRepository.countAllByEventIdAndStatus(eventId,
                ParticipationRequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
            throw new ConditionsAreNotMetException("The limit of participation requests has been reached");
        }
        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .build();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
        } else {
            participationRequest.setStatus(ParticipationRequestStatus.PENDING);
        }
        return ParticipationRequestMapper
                .toParticipationRequestDto(participationRequestRepository.save(participationRequest));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id=" + userId + " was not found");
        }
        ParticipationRequest request = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Request with id=" + requestId + " was not found"));
        if (!request.getRequester().getId().equals(userId)) {
            throw new ConditionsAreNotMetException("Only the requester can cancel the request");
        }
        request.setStatus(ParticipationRequestStatus.CANCELED);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequestRepository.save(request));
    }
}
