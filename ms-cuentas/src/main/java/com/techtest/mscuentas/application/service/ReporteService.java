package com.techtest.mscuentas.application.service;

import com.techtest.mscuentas.application.dto.ReporteDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ReporteService {

    List<ReporteDto> generarEstadoCuenta(String clienteId,
                                         LocalDateTime fechaInicio,
                                         LocalDateTime fechaFin);
}