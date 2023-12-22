package ru.practicum.ewm.comment.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndAuthorId(Long id, Long authorId);

    boolean existsByIdAndAuthorId(Long id, Long authorId);

    @Query("select c " +
            "from Comment as c " +
            "where (c.author.id = :userId) " +
            "and (cast(:rangeStart as timestamp) is null or c.createdOn >= :rangeStart) " +
            "and (cast(:rangeEnd as timestamp) is null or c.createdOn <= :rangeEnd)")
    Page<Comment> getAllUserComments(Long userId, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("select c " +
            "from Comment as c " +
            "where (c.event.id = :eventId) " +
            "and (cast(:rangeStart as timestamp) is null or c.createdOn >= :rangeStart) " +
            "and (cast(:rangeEnd as timestamp) is null or c.createdOn <= :rangeEnd) " +
            "and (:participantsOnly = false or (:participantsOnly = true " +
            "and c.author.id in (select r.requester.id " +
            "from ParticipationRequest as r " +
            "where r.status = 'CONFIRMED')))")
    Page<Comment> getCommentsByEventId(Long eventId, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                       boolean participantsOnly, Pageable pageable);
}
