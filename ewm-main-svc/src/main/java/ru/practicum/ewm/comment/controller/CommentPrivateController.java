package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.service.CommentService;
import ru.practicum.ewm.util.Util;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentPrivateController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllUserComments(
            @Positive @PathVariable(name = "userId") Long userId,
            @RequestParam(name = "rangeStart", required = false)
            @DateTimeFormat(pattern = Util.DATE_TIME_FORMATTER) LocalDateTime rangeStart,
            @RequestParam(name = "rangeEnd", required = false)
            @DateTimeFormat(pattern = Util.DATE_TIME_FORMATTER) LocalDateTime rangeEnd,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Positive int size,
            @RequestParam(name = "sort", required = false, defaultValue = "OLD")
            @Pattern(regexp = "RECENT|OLD") String sort) {
        log.info("CommentPrivateController => getAllUserComments: userId={}, rangeStart={}, rangeEnd={}, from={}, " +
                        "size ={}, sort ={}", userId, rangeStart, rangeEnd, from, size, sort);
        return commentService.getAllUserComments(userId, rangeStart, rangeEnd, from, size, sort);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@Positive @PathVariable(name = "userId") Long userId,
                                 @Positive @RequestParam(name = "eventId") Long eventId,
                                 @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("CommentPrivateController => addComment: userId={}, eventId={}, newCommentDto ={}",
                userId, eventId, newCommentDto);
        return commentService.addComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@Positive @PathVariable(name = "userId") Long userId,
                                    @Positive @PathVariable(name = "commentId") Long commentId,
                                    @Valid @RequestBody UpdateCommentDto updateCommentDto) {
        log.info("CommentPrivateController => updateComment: userId={}, commentId={}, updateCommentDto ={}",
                userId, commentId, updateCommentDto);
        return commentService.updateComment(userId, commentId, updateCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@Positive @PathVariable(name = "userId") Long userId,
                              @Positive @PathVariable(name = "commentId") Long commentId) {
        log.info("CommentPrivateController => deleteComment: userId={}, commentId={}", userId, commentId);
        commentService.deleteComment(userId, commentId);
    }
}
