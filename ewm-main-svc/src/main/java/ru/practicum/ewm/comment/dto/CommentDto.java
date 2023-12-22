package ru.practicum.ewm.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.util.Util;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDto {
    private Long id;
    private String text;
    private UserShortDto author;
    private Long eventId;
    @JsonFormat(pattern = Util.DATE_TIME_FORMATTER)
    private LocalDateTime createdOn;
    @JsonFormat(pattern = Util.DATE_TIME_FORMATTER)
    private LocalDateTime updatedOn;
}
