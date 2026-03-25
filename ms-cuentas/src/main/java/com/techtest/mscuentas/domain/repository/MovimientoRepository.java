package com.techtest.mscuentas.domain.repository;

import com.techtest.mscuentas.domain.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByCuentaId(Long cuentaId);

    List<Movimiento> findByCuentaIdAndFechaBetween(
            Long cuentaId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    List<Movimiento> findByCuenta_ClienteIdAndFechaBetween(
            String clienteId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
}