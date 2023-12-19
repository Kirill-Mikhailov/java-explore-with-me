package ru.practicum.statistics.service;

import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    HitDto saveHit(HitDto hitDto);

    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
