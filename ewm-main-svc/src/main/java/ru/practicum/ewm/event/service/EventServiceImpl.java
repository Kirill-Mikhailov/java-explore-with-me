package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.EwmProducer;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.ReqParamForGetStats;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.dto.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.location.Location;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.participationrequest.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.participationrequest.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.participationrequest.dto.ParticipationRequestDto;
import ru.practicum.ewm.participationrequest.dto.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.participationrequest.model.ParticipationRequest;
import ru.practicum.ewm.participationrequest.model.ParticipationRequestStatus;
import ru.practicum.ewm.participationrequest.storage.ParticipationRequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository requestRepository;
    private final EwmProducer ewmProducer;

    @Override
    public List<EventShortDto> getEvents(
            String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Boolean onlyAvailable, String sort, int from, int size, HttpServletRequest request) {
        if (Objects.isNull(rangeStart)) {
            rangeStart = LocalDateTime.now();
        }
        if (!Objects.isNull(rangeEnd) && rangeStart.isAfter(rangeEnd)) {
            throw new IncorrectlyMadeRequest("The start date must not be later than the end date");
        }
        if ((!Objects.isNull(text) && text.isBlank()) || (!Objects.isNull(categories) && categories.isEmpty())) {
            return Collections.emptyList();
        }
        saveHit(request);
        switch (Objects.isNull(sort) ? "default" : sort) {
            case "EVENT_DATE":
                return eventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                                getPageable(from, size, Sort.by("eventDate"))).stream()
                        .map(event -> EventMapper.toEventShortDto(event, getConfirmedRequests(event), getViews(event)))
                        .collect(Collectors.toList());
            case "VIEWS":
                return eventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                                getPageable(from, size, null)).stream()
                        .map(event -> EventMapper.toEventShortDto(event, getConfirmedRequests(event), getViews(event)))
                        .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                        .collect(Collectors.toList());
            default:
                return eventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                                getPageable(from, size, null)).stream()
                        .map(event -> EventMapper.toEventShortDto(event, getConfirmedRequests(event), getViews(event)))
                        .collect(Collectors.toList());
        }
    }

    @Override
    public EventFullDto getEventById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + id + " was not found"));
        saveHit(request);
        return EventMapper.toEventFullDto(event, getConfirmedRequests(event), getViews(event));
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id=" + userId + " was not found");
        }
        return eventRepository.findByInitiatorId(userId, getPageable(from, size, Sort.by("id").ascending()))
                .stream().map(event -> EventMapper.toEventShortDto(event, getConfirmedRequests(event), getViews(event)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto saveEvent(NewEventDto newEventDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id=" + userId + " was not found"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException("Category with id=" + newEventDto.getCategory()
                        + " was not found"));
        return EventMapper.toEventFullDto(eventRepository
                .save(EventMapper.toEvent(newEventDto, category, user)), 0, 0L);
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id=" + userId + " was not found");
        }
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + " and initiatorId="
                        + userId + " was not found"));
        return EventMapper.toEventFullDto(event, getConfirmedRequests(event), getViews(event));
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id=" + userId + " was not found");
        }
        Event eventFromBd = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + " and initiatorId="
                        + userId + " was not found"));
        if (eventFromBd.getState().equals(EventState.PUBLISHED)) {
            throw new ConditionsAreNotMetException("Only PENDING or CANCELED events can be changed");
        }
        Category category = eventFromBd.getCategory();
        if (!Objects.isNull(updateEventUserRequest.getCategory())) {
            category = categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException("Category with id="
                            + updateEventUserRequest.getCategory() + " was not found"));
        }
        Location location = Objects.isNull(updateEventUserRequest.getLocation()) ?
                eventFromBd.getLocation() : updateEventUserRequest.getLocation();
        Event newEvent = EventMapper.toEvent(updateEventUserRequest, eventFromBd, category, location);
        return EventMapper.toEventFullDto(eventRepository.save(newEvent), getConfirmedRequests(newEvent), getViews(newEvent));
    }

    @Override
    public List<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id=" + userId + " was not found");
        }
         if (!eventRepository.existsByIdAndInitiatorId(eventId, userId))
                throw new EventNotFoundException("Event with id=" + eventId + " and initiatorId="
                        + userId + " was not found");
         return requestRepository.findByEventId(eventId).stream()
                 .map(ParticipationRequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestsStatus(
            Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id=" + userId + " was not found");
        }
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + " and initiatorId="
                        + userId + " was not found"));
        if (!event.getRequestModeration()) {
            throw new ConditionsAreNotMetException("For an event with id =" + eventId
                    + " confirmation of requests  is not required");
        }
        int available = event.getParticipantLimit() - getConfirmedRequests(event);
        if (available <= 0) {
            throw new ConditionsAreNotMetException("The participant limit has been reached");
        }
        List<ParticipationRequest> requests = requestRepository.findByEventIdAndStatusAndIdIn(eventId,
                ParticipationRequestStatus.PENDING, eventRequestStatusUpdateRequest.getRequestIds());
        if (requests.size() < eventRequestStatusUpdateRequest.getRequestIds().size()) {
            throw new ConditionsAreNotMetException("The status can only be changed for PENDING requests");
        }
        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();
        for (ParticipationRequest request : requests) {
            if (eventRequestStatusUpdateRequest.getStatus().equals(ParticipationRequestStatus.CONFIRMED) && available > 0) {
                request.setStatus(ParticipationRequestStatus.CONFIRMED);
                confirmedRequests.add(request);
                available--;
            } else {
                request.setStatus(ParticipationRequestStatus.REJECTED);
                rejectedRequests.add(request);
            }
            requestRepository.saveAll(confirmedRequests);
            requestRepository.saveAll(rejectedRequests);
        }
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests.stream()
                        .map(ParticipationRequestMapper::toParticipationRequestDto).collect(Collectors.toList()))
                .rejectedRequests(rejectedRequests.stream()
                        .map(ParticipationRequestMapper::toParticipationRequestDto).collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<EventFullDto> getEventsForAdmin(List<Long> users, List<EventState> states,
                                                List<Long> categories, LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd, int from, int size) {
        return eventRepository.getEventsForAdmin(users, states, categories, rangeStart, rangeEnd,
                        getPageable(from, size, Sort.by("id").ascending()))
                .stream().map(event -> EventMapper.toEventFullDto(event, getConfirmedRequests(event), getViews(event)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event eventFromBd = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + " was not found"));
        if (!Objects.isNull(updateEventAdminRequest.getStateAction())
                && !eventFromBd.getState().equals(EventState.PENDING)) {
            throw new ConditionsAreNotMetException("Event with id=" + eventId + " does not have the PENDING status");
        }
        Category category = eventFromBd.getCategory();
        if (!Objects.isNull(updateEventAdminRequest.getCategory())) {
            category = categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException("Category with id="
                            + updateEventAdminRequest.getCategory() + " was not found"));
        }
        Location location = Objects.isNull(updateEventAdminRequest.getLocation()) ?
                eventFromBd.getLocation() : updateEventAdminRequest.getLocation();
        Event newEvent = EventMapper.toEvent(updateEventAdminRequest, eventFromBd, category, location);
        if (!Objects.isNull(newEvent.getPublishedOn())
                && newEvent.getEventDate().isBefore(newEvent.getPublishedOn().plusHours(1))) {
            throw new ConditionsAreNotMetException("The date of the event must be no earlier than one hour " +
                    "from the date of publication");
        }
        return EventMapper.toEventFullDto(eventRepository
                .save(newEvent), getConfirmedRequests(newEvent), getViews(newEvent));
    }

    public Set<EventShortDto> toEventShortDtoList(Set<Event> events) {
        return events.stream().map(event -> EventMapper.toEventShortDto(event, getConfirmedRequests(event),
                getViews(event))).collect(Collectors.toSet());
    }

    private Pageable getPageable(int from, int size, Sort sort) {
        int page = from / size;
        return Objects.isNull(sort) ?
                PageRequest.of(page, size) : PageRequest.of(page, size, sort);
    }

    private Integer getConfirmedRequests(Event event) {
        return event.getState().equals(EventState.PUBLISHED) ?
                requestRepository.countAllByEventIdAndStatus(event.getId(), ParticipationRequestStatus.CONFIRMED) : 0;
    }

    private Long getViews(Event event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            return 0L;
        }
        List<StatsDto> statsDto;
        ReqParamForGetStats param = ReqParamForGetStats.builder()
                .start(event.getPublishedOn())
                .end(LocalDateTime.now())
                .uris(List.of(String.format("/events/%d", event.getId())))
                .unique(true)
                .build();
        try {
            statsDto = ewmProducer.getStats(param);
            return statsDto.isEmpty() ? 0L : statsDto.get(0).getHits();
        } catch (AmqpException | NullPointerException e) {
            throw new StatisticsServerException("Error on the statistics service side");
        }
    }

    private void saveHit(HttpServletRequest request) {
        ewmProducer.saveHits(
                HitDto.builder()
                        .app("ewm-main-service")
                        .uri(request.getRequestURI())
                        .ip(request.getRemoteAddr())
                        .hitTime(LocalDateTime.now())
                        .build());
    }
}