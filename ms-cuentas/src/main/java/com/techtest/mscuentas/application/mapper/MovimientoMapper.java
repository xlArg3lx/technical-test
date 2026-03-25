package com.techtest.mscuentas.application.mapper;

import com.techtest.mscuentas.application.dto.MovimientoResponseDto;
import com.techtest.mscuentas.domain.entity.Movimiento;
import org.springframework.stereotype.Component;

@Component
public class MovimientoMapper {

    public MovimientoResponseDto toDto(Movimiento movimiento) {
        return MovimientoResponseDto.builder()
                .id(movimiento.getId())
                .fecha(movimiento.getFecha())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .valor(movimiento.getValor())
                .saldo(movimiento.getSaldo())
                .numeroCuenta(movimiento.getCuenta().getNumeroCuenta())
                .build();
    }
}