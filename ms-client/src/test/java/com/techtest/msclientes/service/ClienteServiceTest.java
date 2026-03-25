package com.techtest.msclientes.service;

import com.techtest.msclientes.application.dto.ClienteRequestDto;
import com.techtest.msclientes.application.dto.ClienteResponseDto;
import com.techtest.msclientes.application.mapper.ClienteMapper;
import com.techtest.msclientes.application.service.impl.ClienteServiceImpl;
import com.techtest.msclientes.domain.entity.Cliente;
import com.techtest.msclientes.domain.repository.ClienteRepository;
import com.techtest.msclientes.infrastructure.messaging.publisher.ClienteEventPublisher;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private ClienteEventPublisher eventPublisher;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;
    private ClienteRequestDto requestDto;
    private ClienteResponseDto responseDto;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Jose Lema");
        cliente.setGenero("Masculino");
        cliente.setEdad(30);
        cliente.setIdentificacion("1234567890");
        cliente.setDireccion("Otavalo sn y principal");
        cliente.setTelefono("098254785");
        cliente.setClienteId("jose-lema");
        cliente.setContrasena("1234");
        cliente.setEstado(true);

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
    @DisplayName("Debe retornar lista de clientes")
    void findAll_debeRetornarListaDeClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));
        when(clienteMapper.toDto(cliente)).thenReturn(responseDto);

        List<ClienteResponseDto> resultado = clienteService.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Jose Lema", resultado.get(0).getNombre());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar cliente por id")
    void findById_debeRetornarClientePorId() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toDto(cliente)).thenReturn(responseDto);

        ClienteResponseDto resultado = clienteService.findById(1L);

        assertNotNull(resultado);
        assertEquals("jose-lema", resultado.getClienteId());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando cliente no existe")
    void findById_debeLanzarExcepcionCuandoClienteNoExiste() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> clienteService.findById(99L)
        );

        assertEquals("Cliente con id 99 no encontrado", ex.getMessage());
        verify(clienteRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Debe crear cliente correctamente")
    void create_debeCrearClienteCorrectamente() {
        when(clienteRepository.existsByClienteId("jose-lema")).thenReturn(false);
        when(clienteRepository.existsByIdentificacion("1234567890")).thenReturn(false);
        when(clienteMapper.toEntity(requestDto)).thenReturn(cliente);
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.toDto(cliente)).thenReturn(responseDto);

        ClienteResponseDto resultado = clienteService.create(requestDto);

        assertNotNull(resultado);
        assertEquals("jose-lema", resultado.getClienteId());
        verify(clienteRepository, times(1)).save(cliente);
        verify(eventPublisher, times(1)).publishClienteCreado(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando clienteId ya existe")
    void create_debeLanzarExcepcionCuandoClienteIdYaExiste() {
        when(clienteRepository.existsByClienteId("jose-lema")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> clienteService.create(requestDto)
        );

        assertEquals("Ya existe un cliente con clienteId: jose-lema", ex.getMessage());
        verify(clienteRepository, never()).save(any());
        verify(eventPublisher, never()).publishClienteCreado(any());
    }

    @Test
    @DisplayName("Debe eliminar cliente correctamente")
    void delete_debeEliminarClienteCorrectamente() {
        when(clienteRepository.existsById(1L)).thenReturn(true);

        clienteService.delete(1L);

        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar cliente inexistente")
    void delete_debeLanzarExcepcionAlEliminarClienteInexistente() {
        when(clienteRepository.existsById(99L)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> clienteService.delete(99L)
        );

        assertEquals("Cliente con id 99 no encontrado", ex.getMessage());
        verify(clienteRepository, never()).deleteById(any());
    }
}