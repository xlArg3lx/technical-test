package com.techtest.mscuentas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techtest.mscuentas.application.dto.MovimientoRequestDto;
import com.techtest.mscuentas.application.dto.MovimientoResponseDto;
import com.techtest.mscuentas.application.service.MovimientoService;
import com.techtest.mscuentas.infrastructure.controller.MovimientoController;
import com.techtest.mscuentas.infrastructure.exception.GlobalExceptionHandler;
import com.techtest.mscuentas.infrastructure.exception.SaldoInsuficienteException;
import com.techtest.mscuentas.infrastructure.response.GlobalResponseAdvice;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovimientoController.class)
@Import({GlobalExceptionHandler.class, GlobalResponseAdvice.class})
class MovimientoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MovimientoService movimientoService;

    private MovimientoRequestDto requestDto;
    private MovimientoResponseDto responseDto;

    @BeforeEach
    void setUp() {
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
    @DisplayName("GET /movimientos - debe retornar lista con status 200")
    void findAll_debeRetornarListaConStatus200() throws Exception {
        when(movimientoService.findAll()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].numeroCuenta").value("478758"))
                .andExpect(jsonPath("$.data[0].tipoMovimiento").value("Retiro"));
    }

    @Test
    @DisplayName("GET /movimientos/{id} - debe retornar movimiento con status 200")
    void findById_debeRetornarMovimientoConStatus200() throws Exception {
        when(movimientoService.findById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/movimientos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.numeroCuenta").value("478758"))
                .andExpect(jsonPath("$.data.saldo").value(1425.00));
    }

    @Test
    @DisplayName("GET /movimientos/{id} - debe retornar 404 cuando no existe")
    void findById_debeRetornar404CuandoNoExiste() throws Exception {
        when(movimientoService.findById(99L))
                .thenThrow(new EntityNotFoundException(
                        "Movimiento con id 99 no encontrado"));

        mockMvc.perform(get("/movimientos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Movimiento con id 99 no encontrado"));
    }

    @Test
    @DisplayName("POST /movimientos - debe registrar movimiento con status 201")
    void create_debeRegistrarMovimientoConStatus201() throws Exception {
        when(movimientoService.create(any())).thenReturn(responseDto);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.saldo").value(1425.00));
    }

    @Test
    @DisplayName("POST /movimientos - debe retornar 400 cuando saldo insuficiente")
    void create_debeRetornar400CuandoSaldoInsuficiente() throws Exception {
        when(movimientoService.create(any()))
                .thenThrow(new SaldoInsuficienteException());

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Saldo no disponible"));
    }

    @Test
    @DisplayName("POST /movimientos - debe retornar 400 cuando campos son inválidos")
    void create_debeRetornar400CuandoCamposInvalidos() throws Exception {
        MovimientoRequestDto dtoInvalido = MovimientoRequestDto.builder().build();

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"));
    }

    @Test
    @DisplayName("DELETE /movimientos/{id} - debe eliminar con status 204")
    void delete_debeEliminarConStatus204() throws Exception {
        mockMvc.perform(delete("/movimientos/1"))
                .andExpect(status().isNoContent());
    }
}