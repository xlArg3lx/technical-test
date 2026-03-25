package com.techtest.msclientes.infrastructure.messaging.publisher;

import com.techtest.msclientes.infrastructure.messaging.config.RabbitMQConfig;
import com.techtest.msclientes.infrastructure.messaging.event.ClienteCreadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishClienteCreado(ClienteCreadoEvent event) {
        log.info("Publicando evento cliente.creado para clienteId: {}",
                event.getClienteId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_CLIENTES,
                RabbitMQConfig.ROUTING_KEY_CLIENTE_CREADO,
                event
        );
        log.info("Evento cliente.creado publicado exitosamente para clienteId: {}",
                event.getClienteId());
    }
}