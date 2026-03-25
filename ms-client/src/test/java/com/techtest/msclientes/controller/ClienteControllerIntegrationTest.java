package com.techtest.msclientes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techtest.msclientes.application.dto.ClienteRequestDto;
import com.techtest.msclientes.application.dto.ClienteResponseDto;
import com.techtest.msclientes.application.service.ClienteService;
import com.techtest.msclientes.infrastructure.controller.ClienteController;
import com.techtest.msclientes.infrastructure.exception.GlobalExceptionHandler;
import com.techtest.msclientes.infrastructure.response.GlobalResponseAdvice;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@Import({GlobalExceptionHandler.class, GlobalResponseAdvice.class})
class ClienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClienteService clienteService;

    private ClienteRequestDto requestDto;
    private ClienteResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = ClienteRequestDto.builder()
                .nombre("Jose Lema")
                .genero("Masculino")
                .edad(30)
                .identificacion("1234567890")
                .direccion("Otavalo sn y principal")
                .telefono("098254785")
                .clienteId("jose-lema")
                .contrasena("1234")
                .estado(true)
                .build();

        responseDto = ClienteResponseDto.builder()
                .id(1L)
                .nombre("Jose Lema")
                .genero("Masculino")
                .edad(30)
                .identificacion("1234567890")
                .direccion("Otavalo sn y principal")
                .telefono("098254785")
                .clienteId("jose-lema")
                .estado(true)
                .build();
    }

    @Test
    @DisplayName("GET /clientes - debe retornar lista con status 200")
    void findAll_debeRetornarListaConStatus200() throws Exception {
        when(clienteService.findAll()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].nombre").value("Jose Lema"))
                .andExpect(jsonPath("$.data[0].clienteId").value("jose-lema"));
    }

    @Test
    @DisplayName("GET /clientes/{id} - debe retornar cliente con status 200")
    void findById_debeRetornarClienteConStatus200() throws Exception {
        when(clienteService.findById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.nombre").value("Jose Lema"))
                .andExpect(jsonPath("$.data.clienteId").value("jose-lema"));
    }

    @Test
    @DisplayName("GET /clientes/{id} - debe retornar 404 cuando no existe")
    void findById_debeRetornar404CuandoNoExiste() throws Exception {
        when(clienteService.findById(99L))
                .thenThrow(new EntityNotFoundException(
                        "Cliente con id 99 no encontrado"));

        mockMvc.perform(get("/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Cliente con id 99 no encontrado"));
    }

    @Test
    @DisplayName("POST /clientes - debe crear cliente con status 201")
    void create_debeCrearClienteConStatus201() throws Exception {
        when(clienteService.create(any())).thenReturn(responseDto);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.nombre").value("Jose Lema"))
                .andExpect(jsonPath("$.data.clienteId").value("jose-lema"));
    }

    @Test
    @DisplayName("POST /clientes - debe retornar 400 cuando campos son inválidos")
    void create_debeRetornar400CuandoCamposInvalidos() throws Exception {
        ClienteRequestDto dtoInvalido = ClienteRequestDto.builder().build();

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("PUT /clientes/{id} - debe actualizar cliente con status 200")
    void update_debeActualizarClienteConStatus200() throws Exception {
        when(clienteService.update(eq(1L), any())).thenReturn(responseDto);

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.nombre").value("Jose Lema"));
    }

    @Test
    @DisplayName("DELETE /clientes/{id} - debe eliminar cliente con status 204")
    void delete_debeEliminarClienteConStatus204() throws Exception {
        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());
    }
}