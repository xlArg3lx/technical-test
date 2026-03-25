package com.techtest.mscuentas.domain.repository;

import com.techtest.mscuentas.domain.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

    List<Cuenta> findByClienteId(String clienteId);

    boolean existsByNumeroCuenta(String numeroCuenta);

    boolean existsByClienteId(String clienteId);
}