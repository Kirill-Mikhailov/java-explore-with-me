package ru.practicum.ewm.participationrequest.dto;

import lombok.Data;
import ru.practicum.ewm.exception.IncorrectlyMadeRequest;
import ru.practicum.ewm.participationrequest.model.ParticipationRequestStatus;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    @NotEmpty
    private List<Long> requestIds;
    @NotNull
    private ParticipationRequestStatus status;

    @AssertTrue
    private boolean isValidStatus() {
        if (!(status.equals(ParticipationRequestStatus.CONFIRMED) || status.equals(ParticipationRequestStatus.REJECTED))) {
            throw new IncorrectlyMadeRequest("Field: status. Error: must be CONFIRMED or REJECTED. Value: " + status);
        }
        return true;
    }
}
