package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {

    CommentDto getCommentById(Long commentId);

    List<CommentDto> getCommentsByEventId(Long eventId, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                          boolean participantsOnly, int from, int size, String sort);

    List<CommentDto> getAllUserComments(Long userId, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        int from, int size, String sort);

    CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto);

    void deleteComment(Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);
}
