package ru.practicum.ewm.rabbitmqconfig;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.for.save.hits.name}")
    private String queueForSaveHitsName;

    @Value("${rabbitmq.queue.for.get.stats.name}")
    private String queueForGetStatsName;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key.for.save.hits}")
    private String routingKeyForSaveHits;

    @Value("${rabbitmq.routing.key.for.get.stats}")
    private String routingKeyForGetStats;

    @Bean
    public Queue queueForSaveHits() {
        return new Queue(queueForSaveHitsName);
    }

    @Bean
    public Queue queueForGetStats() {
        return new Queue(queueForGetStatsName);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding bindingForSaveHits() {
        return BindingBuilder
                .bind(queueForSaveHits())
                .to(exchange())
                .with(routingKeyForSaveHits);
    }

    @Bean
    public Binding bindingForGetStats() {
        return BindingBuilder
                .bind(queueForGetStats())
                .to(exchange())
                .with(routingKeyForGetStats);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
