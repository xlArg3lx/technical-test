package com.techtest.mscuentas.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaResponseDto {

    private Long id;
    private String numeroCuenta;
    private String tipoCuenta;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal saldoInicial;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal saldoDisponible;

    private Boolean estado;
    private String clienteId;
}