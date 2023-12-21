package ru.practicum.ewm.event.dto.mapper;

import ru.practicum.ewm.category.dto.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.location.Location;
import ru.practicum.ewm.user.dto.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

public class EventMapper {

    public static Event toEvent(NewEventDto newEventDto, Category category, User initiator) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(initiator)
                .location(newEventDto.getLocation())
                .paid(!Objects.isNull(newEventDto.getPaid()) && newEventDto.getPaid())
                .participantLimit(Objects.isNull(newEventDto.getParticipantLimit()) ? 0 : newEventDto.getParticipantLimit())
                .requestModeration(Objects.isNull(newEventDto.getRequestModeration()) || newEventDto.getRequestModeration())
                .state(EventState.PENDING)
                .title(newEventDto.getTitle())
                .build();
    }

    public static Event toEvent(UpdateEventUserRequest newEvent, Event eventFromBd, Category newCategory, Location newLocation) {
        EventState state;
        if (Objects.isNull(newEvent.getStateAction())) {
            state = eventFromBd.getState();
        } else {
            state = newEvent.getStateAction().equals(UserStateAction.SEND_TO_REVIEW) ?
                    EventState.PENDING : EventState.CANCELED;
        }
        return Event.builder()
                .id(eventFromBd.getId())
                .annotation(Objects.isNull(newEvent.getAnnotation()) ? eventFromBd.getAnnotation() : newEvent.getAnnotation())
                .category(Objects.isNull(newCategory) ? eventFromBd.getCategory() : newCategory)
                .createdOn(eventFromBd.getCreatedOn())
                .description(Objects.isNull(newEvent.getDescription()) ? eventFromBd.getDescription() : newEvent.getDescription())
                .eventDate(Objects.isNull(newEvent.getEventDate()) ? eventFromBd.getEventDate() : newEvent.getEventDate())
                .initiator(eventFromBd.getInitiator())
                .location(Objects.isNull(newLocation) ? eventFromBd.getLocation() : newLocation)
                .paid(Objects.isNull(newEvent.getPaid()) ? eventFromBd.getPaid() : newEvent.getPaid())
                .participantLimit(Objects.isNull(newEvent.getParticipantLimit()) ?
                        eventFromBd.getParticipantLimit() : newEvent.getParticipantLimit())
                .requestModeration(Objects.isNull(newEvent.getRequestModeration()) ?
                        eventFromBd.getRequestModeration() : newEvent.getRequestModeration())
                .title(Objects.isNull(newEvent.getTitle()) ? eventFromBd.getTitle() : newEvent.getTitle())
                .publishedOn(eventFromBd.getPublishedOn())
                .state(state)
                .build();
    }

    public static Event toEvent(UpdateEventAdminRequest newEvent, Event eventFromBd, Category newCategory, Location newLocation) {
        EventState state;
        if (Objects.isNull(newEvent.getStateAction())) {
            state = eventFromBd.getState();
        } else {
            state = newEvent.getStateAction().equals(AdminStateAction.PUBLISH_EVENT) ? EventState.PUBLISHED : EventState.CANCELED;
        }
        return Event.builder()
                .id(eventFromBd.getId())
                .annotation(Objects.isNull(newEvent.getAnnotation()) ? eventFromBd.getAnnotation() : newEvent.getAnnotation())
                .category(Objects.isNull(newCategory) ? eventFromBd.getCategory() : newCategory)
                .createdOn(eventFromBd.getCreatedOn())
                .description(Objects.isNull(newEvent.getDescription()) ? eventFromBd.getDescription() : newEvent.getDescription())
                .eventDate(Objects.isNull(newEvent.getEventDate()) ? eventFromBd.getEventDate() : newEvent.getEventDate())
                .initiator(eventFromBd.getInitiator())
                .location(Objects.isNull(newLocation) ? eventFromBd.getLocation() : newLocation)
                .paid(Objects.isNull(newEvent.getPaid()) ? eventFromBd.getPaid() : newEvent.getPaid())
                .participantLimit(Objects.isNull(newEvent.getParticipantLimit()) ?
                        eventFromBd.getParticipantLimit() : newEvent.getParticipantLimit())
                .requestModeration(Objects.isNull(newEvent.getRequestModeration()) ?
                        eventFromBd.getRequestModeration() : newEvent.getRequestModeration())
                .title(Objects.isNull(newEvent.getTitle()) ? eventFromBd.getTitle() : newEvent.getTitle())
                .publishedOn(state.equals(EventState.PUBLISHED) ? LocalDateTime.now() : null)
                .state(state)
                .build();
    }

    public static EventFullDto toEventFullDto(Event event, Integer confirmedRequests, Long views) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    public static EventShortDto toEventShortDto(Event event, Integer confirmedRequests, Long views) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .build();
    }
}
