package ru.practicum.ewm.participationrequest.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.participationrequest.model.ParticipationRequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ParticipationRequestDto {
    private Long id;
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private ParticipationRequestStatus status;
}
