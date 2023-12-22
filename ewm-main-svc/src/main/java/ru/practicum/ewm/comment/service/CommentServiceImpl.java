package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.dto.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.storage.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.exception.CommentNotFoundException;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.IncorrectlyMadeRequest;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id=" + commentId + " was not found"));
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsByEventId(Long eventId, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                 boolean participantsOnly, int from, int size, String sort) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event with id=" + eventId + " was not found");
        }
        if (!Objects.isNull(rangeStart) && !Objects.isNull(rangeEnd) && rangeStart.isAfter(rangeEnd)) {
            throw new IncorrectlyMadeRequest("The start date must not be later than the end date");
        }
        return commentRepository.getCommentsByEventId(eventId, rangeStart, rangeEnd, participantsOnly,
                getPageable(from, size, sort)).stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllUserComments(Long userId, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               int from, int size, String sort) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id=" + userId + " was not found");
        }
        if (!Objects.isNull(rangeStart) && !Objects.isNull(rangeEnd) && rangeStart.isAfter(rangeEnd)) {
            throw new IncorrectlyMadeRequest("The start date must not be later than the end date");
        }
        return commentRepository.getAllUserComments(userId, rangeStart, rangeEnd, getPageable(from, size, sort))
                .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + " was not found"));
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(newCommentDto, user, event)));
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id=" + userId + " was not found");
        }
        Comment commentFromDb = commentRepository.findByIdAndAuthorId(commentId, userId)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id=" + commentId + " and authorId="
                        + userId + " was not found"));
        commentFromDb.setText(updateCommentDto.getText());
        commentFromDb.setUpdatedOn(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentFromDb);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id=" + userId + " was not found");
        }
        if (!commentRepository.existsByIdAndAuthorId(commentId, userId)) {
            throw new CommentNotFoundException("Comment with id=" + commentId + " and authorId="
                    + userId + " was not found");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException("Comment with id=" + commentId + " was not found");
        }
        commentRepository.deleteById(commentId);
    }

    private Pageable getPageable(int from, int size, String sort) {
        int page = from / size;
        Sort sorting = sort.equals("OLD") ? Sort.by("createdOn").ascending()
                : Sort.by("createdOn").descending();
        return PageRequest.of(page, size, sorting);
    }
}
