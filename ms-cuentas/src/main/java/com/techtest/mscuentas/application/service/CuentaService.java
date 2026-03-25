package com.techtest.mscuentas.application.service;

import com.techtest.mscuentas.application.dto.CuentaRequestDto;
import com.techtest.mscuentas.application.dto.CuentaResponseDto;

import java.util.List;

public interface CuentaService {

    List<CuentaResponseDto> findAll();

    CuentaResponseDto findById(Long id);

    CuentaResponseDto create(CuentaRequestDto dto);

    CuentaResponseDto update(Long id, CuentaRequestDto dto);

    void delete(Long id);
}