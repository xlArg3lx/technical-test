package com.techtest.msclientes.infrastructure.messaging.event;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteCreadoEvent implements Serializable {

    private Long id;
    private String clienteId;
    private String nombre;
    private Boolean estado;
}