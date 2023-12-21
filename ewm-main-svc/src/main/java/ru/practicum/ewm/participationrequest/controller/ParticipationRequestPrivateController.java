package ru.practicum.ewm.participationrequest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.participationrequest.dto.ParticipationRequestDto;
import ru.practicum.ewm.participationrequest.service.ParticipationRequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@Slf4j
@Validated
public class ParticipationRequestPrivateController {

    private final ParticipationRequestService participationRequestService;

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@Positive @PathVariable(name = "userId") Long userId) {
        log.info("ParticipationRequestPrivateController => getUserRequests: userId={}", userId);
        return participationRequestService.getUsersRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto saveRequest(@Positive @PathVariable(name = "userId") Long userId,
                                               @Positive @RequestParam(name = "eventId") Long eventId) {
        log.info("ParticipationRequestPrivateController => saveRequest: userId={}, eventId={}", userId, eventId);
        return participationRequestService.saveRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@Positive @PathVariable(name = "userId") Long userId,
                                                 @Positive @PathVariable(name = "requestId") Long requestId) {
        log.info("ParticipationRequestPrivateController => cancelRequest: userId={}, requestId={}", userId, requestId);
        return participationRequestService.cancelRequest(userId, requestId);
    }
}
