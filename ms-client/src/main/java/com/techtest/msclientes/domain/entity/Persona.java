package com.techtest.msclientes.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "personas", indexes = {
        @Index(name = "idx_persona_identificacion", columnList = "identificacion")
})
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El género es obligatorio")
    @Column(nullable = false)
    private String genero;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad debe ser un valor positivo")
    @Column(nullable = false)
    private Integer edad;

    @NotBlank(message = "La identificación es obligatoria")
    @Column(nullable = false, unique = true)
    private String identificacion;

    @Column
    private String direccion;

    @Column
    private String telefono;
}