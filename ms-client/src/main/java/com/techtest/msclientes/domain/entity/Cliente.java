package com.techtest.msclientes.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "clientes", indexes = {
        @Index(name = "idx_cliente_clienteid", columnList = "clienteid")
})
@PrimaryKeyJoinColumn(name = "persona_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Persona {

    @NotBlank(message = "El clienteId es obligatorio")
    @Column(name = "clienteid", nullable = false, unique = true)
    private String clienteId;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false)
    private Boolean estado = true;
}