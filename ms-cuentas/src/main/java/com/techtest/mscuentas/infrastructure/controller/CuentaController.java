package com.techtest.mscuentas.infrastructure.controller;

import com.techtest.mscuentas.application.dto.CuentaRequestDto;
import com.techtest.mscuentas.application.dto.CuentaResponseDto;
import com.techtest.mscuentas.application.service.CuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    @GetMapping
    public ResponseEntity<List<CuentaResponseDto>> findAll() {
        return ResponseEntity.ok(cuentaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CuentaResponseDto> create(
            @Valid @RequestBody CuentaRequestDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cuentaService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody CuentaRequestDto dto) {
        return ResponseEntity.ok(cuentaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cuentaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}