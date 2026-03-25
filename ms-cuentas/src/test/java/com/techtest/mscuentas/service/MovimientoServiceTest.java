package com.techtest.mscuentas.service;

import com.techtest.mscuentas.application.dto.MovimientoRequestDto;
import com.techtest.mscuentas.application.dto.MovimientoResponseDto;
import com.techtest.mscuentas.application.mapper.MovimientoMapper;
import com.techtest.mscuentas.application.service.impl.MovimientoServiceImpl;
import com.techtest.mscuentas.domain.entity.Cuenta;
import com.techtest.mscuentas.domain.entity.Movimiento;
import com.techtest.mscuentas.domain.repository.CuentaRepository;
import com.techtest.mscuentas.domain.repository.MovimientoRepository;
import com.techtest.mscuentas.infrastructure.exception.SaldoInsuficienteException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private MovimientoMapper movimientoMapper;

    @InjectMocks
    private MovimientoServiceImpl movimientoService;

    private Cuenta cuenta;
    private Movimiento movimiento;
    private MovimientoRequestDto requestDto;
    private MovimientoResponseDto responseDto;

    @BeforeEach
    void setUp() {
        cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("478758");
        cuenta.setTipoCuenta("Ahorro");
        cuenta.setSaldoInicial(new BigDecimal("2000.00"));
        cuenta.setSaldoDisponible(new BigDecimal("2000.00"));
        cuenta.setEstado(true);
        cuenta.setClienteId("jose-lema");

        movimiento = new Movimiento();
        movimiento.setId(1L);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento("Retiro");
        movimiento.setValor(new BigDecimal("-575.00"));
        movimiento.setSaldo(new BigDecimal("1425.00"));
        movimiento.setCuenta(cuenta);

        requestDto = MovimientoRequestDto.builder()
                .numeroCuenta("478758")
                .tipoMovimiento("Retiro")
                .valor(new BigDecimal("-575.00"))
                .build();

        responseDto = MovimientoResponseDto.builder()
                .id(1L)
                .fecha(LocalDateTime.now())
                .tipoMovimiento("Retiro")
                .valor(new BigDecimal("-575.00"))
                .saldo(new BigDecimal("1425.00"))
                .numeroCuenta("478758")
                .build();
    }

    @Test
    @DisplayName("Debe registrar retiro y actualizar saldo correctamente")
    void create_debeRegistrarRetiroYActualizarSaldo() {
        when(cuentaRepository.findByNumeroCuenta("478758"))
                .thenReturn(Optional.of(cuenta));
        when(movimientoRepository.save(any())).thenReturn(movimiento);
        when(movimientoMapper.toDto(movimiento)).thenReturn(responseDto);

        MovimientoResponseDto resultado = movimientoService.create(requestDto);

        assertNotNull(resultado);
        assertEquals(new BigDecimal("1425.00"), resultado.getSaldo());
        assertEquals("Retiro", resultado.getTipoMovimiento());
        verify(cuentaRepository, times(1)).save(cuenta);
        verify(movimientoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Debe registrar depósito y actualizar saldo correctamente")
    void create_debeRegistrarDepositoYActualizarSaldo() {
        MovimientoRequestDto depositoDto = MovimientoRequestDto.builder()
                .numeroCuenta("478758")
                .tipoMovimiento("Deposito")
                .valor(new BigDecimal("500.00"))
                .build();

        Movimiento movimientoDeposito = new Movimiento();
        movimientoDeposito.setId(2L);
        movimientoDeposito.setTipoMovimiento("Deposito");
        movimientoDeposito.setValor(new BigDecimal("500.00"));
        movimientoDeposito.setSaldo(new BigDecimal("2500.00"));
        movimientoDeposito.setCuenta(cuenta);

        MovimientoResponseDto responseDtoDeposito = MovimientoResponseDto.builder()
                .id(2L)
                .tipoMovimiento("Deposito")
                .valor(new BigDecimal("500.00"))
                .saldo(new BigDecimal("2500.00"))
                .numeroCuenta("478758")
                .build();

        when(cuentaRepository.findByNumeroCuenta("478758"))
                .thenReturn(Optional.of(cuenta));
        when(movimientoRepository.save(any())).thenReturn(movimientoDeposito);
        when(movimientoMapper.toDto(movimientoDeposito)).thenReturn(responseDtoDeposito);

        MovimientoResponseDto resultado = movimientoService.create(depositoDto);

        assertNotNull(resultado);
        assertEquals(new BigDecimal("2500.00"), resultado.getSaldo());
        assertEquals("Deposito", resultado.getTipoMovimiento());
        verify(cuentaRepository, times(1)).save(cuenta);
    }

    @Test
    @DisplayName("Debe lanzar SaldoInsuficienteException cuando no hay saldo")
    void create_debeLanzarSaldoInsuficienteException() {
        MovimientoRequestDto retiroExcesivo = MovimientoRequestDto.builder()
                .numeroCuenta("478758")
                .tipoMovimiento("Retiro")
                .valor(new BigDecimal("-9999.00"))
                .build();

        when(cuentaRepository.findByNumeroCuenta("478758"))
                .thenReturn(Optional.of(cuenta));

        SaldoInsuficienteException ex = assertThrows(
                SaldoInsuficienteException.class,
                () -> movimientoService.create(retiroExcesivo)
        );

        assertEquals("Saldo no disponible", ex.getMessage());
        verify(cuentaRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException cuando cuenta no existe")
    void create_debeLanzarExcepcionCuandoCuentaNoExiste() {
        when(cuentaRepository.findByNumeroCuenta("999999"))
                .thenReturn(Optional.empty());

        MovimientoRequestDto dtoInvalido = MovimientoRequestDto.builder()
                .numeroCuenta("999999")
                .tipoMovimiento("Retiro")
                .valor(new BigDecimal("-100.00"))
                .build();

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> movimientoService.create(dtoInvalido)
        );

        assertEquals("Cuenta con número 999999 no encontrada", ex.getMessage());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando cuenta está inactiva")
    void create_debeLanzarExcepcionCuandoCuentaInactiva() {
        cuenta.setEstado(false);

        when(cuentaRepository.findByNumeroCuenta("478758"))
                .thenReturn(Optional.of(cuenta));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.create(requestDto)
        );

        assertEquals("La cuenta 478758 no está activa", ex.getMessage());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe retornar lista de movimientos")
    void findAll_debeRetornarListaDeMovimientos() {
        when(movimientoRepository.findAll()).thenReturn(List.of(movimiento));
        when(movimientoMapper.toDto(movimiento)).thenReturn(responseDto);

        List<MovimientoResponseDto> resultado = movimientoService.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(movimientoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar movimiento inexistente")
    void findById_debeLanzarExcepcionCuandoMovimientoNoExiste() {
        when(movimientoRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> movimientoService.findById(99L)
        );

        assertEquals("Movimiento con id 99 no encontrado", ex.getMessage());
    }
}