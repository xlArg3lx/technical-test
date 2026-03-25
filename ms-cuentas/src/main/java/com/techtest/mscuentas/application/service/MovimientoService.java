package com.techtest.mscuentas.application.service;

import com.techtest.mscuentas.application.dto.MovimientoRequestDto;
import com.techtest.mscuentas.application.dto.MovimientoResponseDto;

import java.util.List;

public interface MovimientoService {

    List<MovimientoResponseDto> findAll();

    MovimientoResponseDto findById(Long id);

    MovimientoResponseDto create(MovimientoRequestDto dto);

    void delete(Long id);
}