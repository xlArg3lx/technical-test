package com.techtest.mscuentas.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteDto {

    @JsonProperty("Fecha")
    @JsonFormat(pattern = "d/M/yyyy")
    private LocalDateTime fecha;

    @JsonProperty("Cliente")
    private String cliente;

    @JsonProperty("Numero Cuenta")
    private String numeroCuenta;

    @JsonProperty("Tipo")
    private String tipo;

    @JsonProperty("Saldo Inicial")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal saldoInicial;

    @JsonProperty("Estado")
    private Boolean estado;

    @JsonProperty("Movimiento")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal movimiento;

    @JsonProperty("Saldo Disponible")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal saldoDisponible;
}