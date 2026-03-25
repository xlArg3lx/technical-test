package com.techtest.mscuentas.application.service.impl;

import com.techtest.mscuentas.application.dto.ReporteDto;
import com.techtest.mscuentas.application.service.ReporteService;
import com.techtest.mscuentas.domain.entity.Movimiento;
import com.techtest.mscuentas.domain.repository.CuentaRepository;
import com.techtest.mscuentas.domain.repository.MovimientoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteServiceImpl implements ReporteService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReporteDto> generarEstadoCuenta(String clienteId,
                                                LocalDateTime fechaInicio,
                                                LocalDateTime fechaFin) {
        if (!cuentaRepository.existsByClienteId(clienteId)) {
            throw new EntityNotFoundException(
                    "No se encontraron cuentas para el clienteId: " + clienteId);
        }

        List<Movimiento> movimientos = movimientoRepository
                .findByCuenta_ClienteIdAndFechaBetween(
                        clienteId, fechaInicio, fechaFin);

        if (movimientos.isEmpty()) {
            throw new EntityNotFoundException(
                    "No se encontraron movimientos para el clienteId: "
                            + clienteId + " en el rango de fechas indicado");
        }

        log.info("Generando reporte para clienteId: {} | desde: {} | hasta: {}",
                clienteId, fechaInicio, fechaFin);

        return movimientos.stream()
                .map(movimiento -> ReporteDto.builder()
                        .fecha(movimiento.getFecha())
                        .cliente(movimiento.getCuenta().getClienteId())
                        .numeroCuenta(movimiento.getCuenta().getNumeroCuenta())
                        .tipo(movimiento.getCuenta().getTipoCuenta())
                        .saldoInicial(movimiento.getCuenta().getSaldoInicial())
                        .estado(movimiento.getCuenta().getEstado())
                        .movimiento(movimiento.getValor())
                        .saldoDisponible(movimiento.getSaldo())
                        .build())
                .toList();
    }
}