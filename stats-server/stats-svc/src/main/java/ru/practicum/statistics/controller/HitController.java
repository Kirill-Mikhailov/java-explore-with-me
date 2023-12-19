package ru.practicum.statistics.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statistics.service.HitServiceImpl;
import ru.practicum.statistics.util.Util;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HitController {

    private final HitServiceImpl hitService;

    @PostMapping("/hit")
    public HitDto saveHit(@Valid @RequestBody HitDto hitDto) {
        log.info("HitController => saveHit: hitDto={}", hitDto);
        return hitService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam @DateTimeFormat(pattern = Util.DATE_TIME_FORMATTER) LocalDateTime start,
                             @RequestParam @DateTimeFormat(pattern = Util.DATE_TIME_FORMATTER) LocalDateTime end,
                             @RequestParam(required = false) List<String> uris,
                             @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("HitController => getStats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return hitService.getStats(start, end, uris, unique);
    }
}
