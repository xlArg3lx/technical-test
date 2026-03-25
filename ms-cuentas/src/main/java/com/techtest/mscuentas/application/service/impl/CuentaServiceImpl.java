package com.techtest.mscuentas.application.service.impl;

import com.techtest.mscuentas.application.dto.CuentaRequestDto;
import com.techtest.mscuentas.application.dto.CuentaResponseDto;
import com.techtest.mscuentas.application.mapper.CuentaMapper;
import com.techtest.mscuentas.application.service.CuentaService;
import com.techtest.mscuentas.domain.entity.Cuenta;
import com.techtest.mscuentas.domain.repository.CuentaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final CuentaMapper cuentaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CuentaResponseDto> findAll() {
        return cuentaRepository.findAll()
                .stream()
                .map(cuentaMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaResponseDto findById(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cuenta con id " + id + " no encontrada"));
        return cuentaMapper.toDto(cuenta);
    }

    @Override
    @Transactional
    public CuentaResponseDto create(CuentaRequestDto dto) {
        if (cuentaRepository.existsByNumeroCuenta(dto.getNumeroCuenta())) {
            throw new IllegalArgumentException(
                    "Ya existe una cuenta con número: " + dto.getNumeroCuenta());
        }
        Cuenta cuenta = cuentaMapper.toEntity(dto);
        Cuenta saved = cuentaRepository.save(cuenta);
        log.info("Cuenta creada con número: {}", saved.getNumeroCuenta());
        return cuentaMapper.toDto(saved);
    }

    @Override
    @Transactional
    public CuentaResponseDto update(Long id, CuentaRequestDto dto) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cuenta con id " + id + " no encontrada"));
        cuentaMapper.updateEntity(cuenta, dto);
        Cuenta updated = cuentaRepository.save(cuenta);
        log.info("Cuenta actualizada con id: {}", updated.getId());
        return cuentaMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!cuentaRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Cuenta con id " + id + " no encontrada");
        }
        cuentaRepository.deleteById(id);
        log.info("Cuenta eliminada con id: {}", id);
    }
}