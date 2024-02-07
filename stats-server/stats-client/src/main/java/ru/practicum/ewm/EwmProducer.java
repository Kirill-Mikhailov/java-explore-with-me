package ru.practicum.ewm;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.ReqParamForGetStats;
import ru.practicum.ewm.dto.StatsDto;

import java.util.List;

@Component
public class EwmProducer {

    private RabbitTemplate rabbitTemplate;

    public EwmProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key.for.save.hits}")
    private String routingKeyForSaveHits;

    @Value("${rabbitmq.routing.key.for.get.stats}")
    private String routingKeyForGetStats;

    public void saveHits(HitDto hitDto) {
        rabbitTemplate.convertAndSend(routingKeyForSaveHits, hitDto);
    }

    public List<StatsDto> getStats(ReqParamForGetStats param) {
        return rabbitTemplate.convertSendAndReceiveAsType(routingKeyForGetStats, param,
                new ParameterizedTypeReference<List<StatsDto>>() {});
    }
}
