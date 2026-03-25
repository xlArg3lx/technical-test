package com.techtest.mscuentas.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoResponseDto {

    private Long id;

    @JsonFormat(pattern = "d/M/yyyy")
    private LocalDateTime fecha;

    private String tipoMovimiento;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal valor;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal saldo;

    private String numeroCuenta;
}