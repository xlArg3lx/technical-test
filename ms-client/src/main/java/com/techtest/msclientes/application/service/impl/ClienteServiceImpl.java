package com.techtest.msclientes.application.service.impl;

import com.techtest.msclientes.application.dto.ClienteRequestDto;
import com.techtest.msclientes.application.dto.ClienteResponseDto;
import com.techtest.msclientes.application.mapper.ClienteMapper;
import com.techtest.msclientes.application.service.ClienteService;
import com.techtest.msclientes.domain.entity.Cliente;
import com.techtest.msclientes.domain.repository.ClienteRepository;
import com.techtest.msclientes.infrastructure.messaging.event.ClienteCreadoEvent;
import com.techtest.msclientes.infrastructure.messaging.publisher.ClienteEventPublisher;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final ClienteEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDto> findAll() {
        return clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDto findById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cliente con id " + id + " no encontrado"));
        return clienteMapper.toDto(cliente);
    }

    @Override
    @Transactional
    public ClienteResponseDto create(ClienteRequestDto dto) {
        if (clienteRepository.existsByClienteId(dto.getClienteId())) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente con clienteId: " + dto.getClienteId());
        }
        if (clienteRepository.existsByIdentificacion(dto.getIdentificacion())) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente con identificación: " + dto.getIdentificacion());
        }

        Cliente cliente = clienteMapper.toEntity(dto);
        Cliente saved = clienteRepository.save(cliente);

        log.info("Cliente creado con id: {}", saved.getId());

        eventPublisher.publishClienteCreado(
                ClienteCreadoEvent.builder()
                        .id(saved.getId())
                        .clienteId(saved.getClienteId())
                        .nombre(saved.getNombre())
                        .estado(saved.getEstado())
                        .build()
        );

        return clienteMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ClienteResponseDto update(Long id, ClienteRequestDto dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cliente con id " + id + " no encontrado"));
        clienteMapper.updateEntity(cliente, dto);
        Cliente updated = clienteRepository.save(cliente);
        log.info("Cliente actualizado con id: {}", updated.getId());
        return clienteMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Cliente con id " + id + " no encontrado");
        }
        clienteRepository.deleteById(id);
        log.info("Cliente eliminado con id: {}", id);
    }
}
