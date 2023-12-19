package ru.practicum.statistics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.statistics.exception.DateNotValidException;
import ru.practicum.statistics.model.HitMapper;
import ru.practicum.statistics.storage.HitRepository;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;

    @Override
    public HitDto saveHit(HitDto hitDto) {
        return HitMapper.toHitDto(hitRepository.save(HitMapper.toHit(hitDto)));
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new DateNotValidException("Параметр start не может быть позже параметра end");
        }
        if (unique) {
            return hitRepository.getUniqueStats(start, end, uris);
        } else {
            return hitRepository.getStats(start, end, uris);
        }
    }
}
