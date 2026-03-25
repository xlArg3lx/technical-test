package com.techtest.mscuentas.infrastructure.messaging.consumer;

import com.techtest.mscuentas.infrastructure.messaging.config.RabbitMQConfig;
import com.techtest.mscuentas.infrastructure.messaging.event.ClienteCreadoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClienteEventConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CLIENTE_CREADO)
    public void handleClienteCreado(ClienteCreadoEvent event) {
        log.info("Evento recibido - cliente.creado | clienteId: {} | nombre: {}",
                event.getClienteId(),
                event.getNombre());
    }
}