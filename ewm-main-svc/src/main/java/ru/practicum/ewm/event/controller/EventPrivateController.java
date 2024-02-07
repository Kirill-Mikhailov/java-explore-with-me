package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.participationrequest.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.participationrequest.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.participationrequest.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Slf4j
@Validated
public class EventPrivateController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getUserEvents(
            @Positive @PathVariable("userId") Long userId,
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("EventPrivateController => getUserEvents: userId={}, from={}, size={}", userId, from, size);
        return eventService.getUserEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto saveEvent(@Valid @RequestBody NewEventDto newEventDto,
                                  @Positive @PathVariable("userId") Long userId) {
        log.info("EventPrivateController => saveEvent: newEventDto={}, userId={}", newEventDto, userId);
        return eventService.saveEvent(newEventDto, userId);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEventById(@Positive @PathVariable("userId") Long userId,
                                         @Positive @PathVariable("eventId") Long eventId) {
        log.info("EventPrivateController => getUserEventById: userId={}, eventId={}", userId, eventId);
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@Positive @PathVariable("userId") Long userId,
                                    @Positive @PathVariable("eventId") Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("EventPrivateController => updateEvent: userId={}, eventId={}, updateEventUserRequest={}",
                userId, eventId, updateEventUserRequest);
        return eventService.updateEventByUser(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequests(@Positive @PathVariable("userId") Long userId,
                                                              @Positive @PathVariable("eventId") Long eventId) {

        log.info("EventPrivateController => getUserEventRequests: userId={}, eventId={}", userId, eventId);
        return eventService.getUserEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestsStatus(
            @Positive @PathVariable("userId") Long userId, @Positive @PathVariable("eventId") Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("EventPrivateController => updateEventRequestsStatus: userId={}, eventId={}, " +
                        "eventRequestStatusUpdateRequest={}", userId, eventId, eventRequestStatusUpdateRequest);
        return eventService.updateEventRequestsStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
