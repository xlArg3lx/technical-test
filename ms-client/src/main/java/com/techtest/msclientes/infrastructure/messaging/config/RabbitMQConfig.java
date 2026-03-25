package com.techtest.msclientes.infrastructure.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_CLIENTES = "clientes.exchange";
    public static final String QUEUE_CLIENTE_CREADO = "cliente.creado.queue";
    public static final String ROUTING_KEY_CLIENTE_CREADO = "cliente.creado";

    @Bean
    public TopicExchange clientesExchange() {
        return new TopicExchange(EXCHANGE_CLIENTES);
    }

    @Bean
    public Queue clienteCreadoQueue() {
        return QueueBuilder
                .durable(QUEUE_CLIENTE_CREADO)
                .build();
    }

    @Bean
    public Binding clienteCreadoBinding(Queue clienteCreadoQueue,
                                        TopicExchange clientesExchange) {
        return BindingBuilder
                .bind(clienteCreadoQueue)
                .to(clientesExchange)
                .with(ROUTING_KEY_CLIENTE_CREADO);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}