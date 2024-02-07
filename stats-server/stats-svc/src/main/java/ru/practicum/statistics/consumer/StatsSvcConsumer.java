package ru.practicum.statistics.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.ReqParamForGetStats;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.statistics.service.HitService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsSvcConsumer {

    private final HitService hitService;

    @RabbitListener(queues = "${rabbitmq.queue.for.save.hits.name}")
    public void saveHits(HitDto hitDto) {
        log.info("StatsSvcConsumer => saveHit: hitDto={}", hitDto);
        hitService.saveHit(hitDto);
    }

    @RabbitListener(queues = "${rabbitmq.queue.for.get.stats.name}")
    public List<StatsDto> getStats(ReqParamForGetStats param) {
        log.info("StatsSvcConsumer => getStats: start={}, end={}, uris={}, unique={}", param.getStart(),
                param.getEnd(), param.getUris(), param.getUnique());
        return hitService.getStats(param.getStart(), param.getEnd(),
                param.getUris(), param.getUnique() != null && param.getUnique());
    }
}
