package com.techtest.msclientes.application.service;

import com.techtest.msclientes.application.dto.ClienteRequestDto;
import com.techtest.msclientes.application.dto.ClienteResponseDto;

import java.util.List;

public interface ClienteService {

    List<ClienteResponseDto> findAll();

    ClienteResponseDto findById(Long id);

    ClienteResponseDto create(ClienteRequestDto dto);

    ClienteResponseDto update(Long id, ClienteRequestDto dto);

    void delete(Long id);
}