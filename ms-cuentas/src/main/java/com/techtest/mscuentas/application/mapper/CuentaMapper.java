package com.techtest.mscuentas.application.mapper;

import com.techtest.mscuentas.application.dto.CuentaRequestDto;
import com.techtest.mscuentas.application.dto.CuentaResponseDto;
import com.techtest.mscuentas.domain.entity.Cuenta;
import org.springframework.stereotype.Component;

@Component
public class CuentaMapper {

    public Cuenta toEntity(CuentaRequestDto dto) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(dto.getNumeroCuenta());
        cuenta.setTipoCuenta(dto.getTipoCuenta());
        cuenta.setSaldoInicial(dto.getSaldoInicial());
        cuenta.setSaldoDisponible(dto.getSaldoInicial());
        cuenta.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        cuenta.setClienteId(dto.getClienteId());
        return cuenta;
    }

    public CuentaResponseDto toDto(Cuenta cuenta) {
        return CuentaResponseDto.builder()
                .id(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta())
                .saldoInicial(cuenta.getSaldoInicial())
                .saldoDisponible(cuenta.getSaldoDisponible())
                .estado(cuenta.getEstado())
                .clienteId(cuenta.getClienteId())
                .build();
    }

    public void updateEntity(Cuenta cuenta, CuentaRequestDto dto) {
        cuenta.setNumeroCuenta(dto.getNumeroCuenta());
        cuenta.setTipoCuenta(dto.getTipoCuenta());
        cuenta.setEstado(dto.getEstado());
        cuenta.setClienteId(dto.getClienteId());
    }
}