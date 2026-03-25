package com.techtest.mscuentas.application.service.impl;

import com.techtest.mscuentas.application.dto.MovimientoRequestDto;
import com.techtest.mscuentas.application.dto.MovimientoResponseDto;
import com.techtest.mscuentas.application.mapper.MovimientoMapper;
import com.techtest.mscuentas.application.service.MovimientoService;
import com.techtest.mscuentas.domain.entity.Cuenta;
import com.techtest.mscuentas.domain.entity.Movimiento;
import com.techtest.mscuentas.domain.repository.CuentaRepository;
import com.techtest.mscuentas.domain.repository.MovimientoRepository;
import com.techtest.mscuentas.infrastructure.exception.SaldoInsuficienteException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoMapper movimientoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponseDto> findAll() {
        return movimientoRepository.findAll()
                .stream()
                .map(movimientoMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoResponseDto findById(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Movimiento con id " + id + " no encontrado"));
        return movimientoMapper.toDto(movimiento);
    }

    @Override
    @Transactional
    public MovimientoResponseDto create(MovimientoRequestDto dto) {
        Cuenta cuenta = cuentaRepository
                .findByNumeroCuenta(dto.getNumeroCuenta())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cuenta con número " + dto.getNumeroCuenta() + " no encontrada"));

        if (!cuenta.getEstado()) {
            throw new IllegalArgumentException(
                    "La cuenta " + dto.getNumeroCuenta() + " no está activa");
        }

        BigDecimal saldoActual = cuenta.getSaldoDisponible();
        BigDecimal nuevoSaldo = saldoActual.add(dto.getValor());

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoInsuficienteException();
        }

        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);

        Movimiento movimiento = new Movimiento();
        movimiento.setCuenta(cuenta);
        movimiento.setTipoMovimiento(dto.getTipoMovimiento());
        movimiento.setValor(dto.getValor());
        movimiento.setSaldo(nuevoSaldo);

        Movimiento saved = movimientoRepository.save(movimiento);
        log.info("Movimiento registrado en cuenta: {} | valor: {} | saldo: {}",
                cuenta.getNumeroCuenta(), dto.getValor(), nuevoSaldo);

        return movimientoMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!movimientoRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Movimiento con id " + id + " no encontrado");
        }
        movimientoRepository.deleteById(id);
        log.info("Movimiento eliminado con id: {}", id);
    }
}