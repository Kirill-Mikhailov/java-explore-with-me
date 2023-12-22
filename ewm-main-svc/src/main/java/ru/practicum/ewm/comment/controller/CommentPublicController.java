package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.service.CommentService;
import ru.practicum.ewm.util.Util;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@Positive @PathVariable(name = "commentId") Long commentId) {
        log.info("CommentPublicController => getCommentById: commentId={}", commentId);
        return commentService.getCommentById(commentId);
    }

    @GetMapping
    public List<CommentDto> getCommentsByEventId(
            @Positive @RequestParam(name = "eventId") Long eventId,
            @RequestParam(name = "rangeStart", required = false)
            @DateTimeFormat(pattern = Util.DATE_TIME_FORMATTER) LocalDateTime rangeStart,
            @RequestParam(name = "rangeEnd", required = false)
            @DateTimeFormat(pattern = Util.DATE_TIME_FORMATTER) LocalDateTime rangeEnd,
            @RequestParam(name = "participantsOnly", required = false, defaultValue = "false") boolean participantsOnly,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Positive int size,
            @RequestParam(name = "sort", required = false, defaultValue = "OLD")
            @Pattern(regexp = "RECENT|OLD") String sort) {
        log.info("CommentPublicController => getCommentById: eventId={}, rangeStart={}, rangeEnd={}, " +
                "participantsOnly={}, from={}, size={}, sort={}",
                eventId, rangeStart, rangeEnd, participantsOnly, from, size, sort);
        return commentService.getCommentsByEventId(eventId, rangeStart, rangeEnd, participantsOnly, from, size, sort);
    }
}
