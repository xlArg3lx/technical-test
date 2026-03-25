-- ============================================================
-- BASE DE DATOS: db_clientes (ms-clientes)
-- ============================================================

\c db_clientes;

-- Tabla personas
CREATE TABLE IF NOT EXISTS personas (
                                        id          BIGSERIAL PRIMARY KEY,
                                        nombre      VARCHAR(255) NOT NULL,
    genero      VARCHAR(255) NOT NULL,
    edad        INTEGER      NOT NULL CHECK (edad >= 0),
    identificacion VARCHAR(255) NOT NULL,
    direccion   VARCHAR(255),
    telefono    VARCHAR(255),
    CONSTRAINT uk_persona_identificacion UNIQUE (identificacion)
    );

CREATE INDEX IF NOT EXISTS idx_persona_identificacion
    ON personas (identificacion);

-- Tabla clientes
CREATE TABLE IF NOT EXISTS clientes (
                                        persona_id  BIGINT       NOT NULL,
                                        clienteid   VARCHAR(255) NOT NULL,
    contrasena  VARCHAR(255) NOT NULL,
    estado      BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_cliente PRIMARY KEY (persona_id),
    CONSTRAINT fk_cliente_persona FOREIGN KEY (persona_id)
    REFERENCES personas (id),
    CONSTRAINT uk_cliente_clienteid UNIQUE (clienteid)
    );

CREATE INDEX IF NOT EXISTS idx_cliente_clienteid
    ON clientes (clienteid);

-- ============================================================
-- DATOS DE PRUEBA: db_clientes
-- ============================================================

INSERT INTO personas (nombre, genero, edad, identificacion, direccion, telefono)
VALUES
    ('Jose Lema',          'Masculino', 30, '1234567890', 'Otavalo sn y principal',  '098254785'),
    ('Marianela Montalvo', 'Femenino',  28, '0987654321', 'Amazonas y NNUU',         '097548965'),
    ('Juan Osorio',        'Masculino', 25, '1122334455', '13 junio y Equinoccial',  '098874587');

INSERT INTO clientes (persona_id, clienteid, contrasena, estado)
VALUES
    (1, 'jose-lema',          '1234', TRUE),
    (2, 'marianela-montalvo', '5678', TRUE),
    (3, 'juan-osorio',        '1245', TRUE);


-- ============================================================
-- BASE DE DATOS: db_cuentas (ms-cuentas)
-- ============================================================

\c db_cuentas;

-- Tabla cuentas
CREATE TABLE IF NOT EXISTS cuentas (
                                       id               BIGSERIAL      PRIMARY KEY,
                                       numero_cuenta    VARCHAR(255)   NOT NULL,
    tipo_cuenta      VARCHAR(255)   NOT NULL,
    saldo_inicial    NUMERIC(19, 2) NOT NULL,
    saldo_disponible NUMERIC(19, 2) NOT NULL,
    estado           BOOLEAN        NOT NULL DEFAULT TRUE,
    clienteid        VARCHAR(255)   NOT NULL,
    CONSTRAINT uk_cuenta_numero UNIQUE (numero_cuenta)
    );

CREATE INDEX IF NOT EXISTS idx_cuenta_numero
    ON cuentas (numero_cuenta);

CREATE INDEX IF NOT EXISTS idx_cuenta_clienteid
    ON cuentas (clienteid);

-- Tabla movimientos
CREATE TABLE IF NOT EXISTS movimientos (
                                           id              BIGSERIAL      PRIMARY KEY,
                                           fecha           TIMESTAMP      NOT NULL,
                                           tipo_movimiento VARCHAR(255)   NOT NULL,
    valor           NUMERIC(19, 2) NOT NULL,
    saldo           NUMERIC(19, 2) NOT NULL,
    cuenta_id       BIGINT         NOT NULL,
    CONSTRAINT fk_movimiento_cuenta FOREIGN KEY (cuenta_id)
    REFERENCES cuentas (id)
    );

CREATE INDEX IF NOT EXISTS idx_movimiento_cuenta
    ON movimientos (cuenta_id);

CREATE INDEX IF NOT EXISTS idx_movimiento_fecha
    ON movimientos (fecha);

-- ============================================================
-- DATOS DE PRUEBA: db_cuentas
-- ============================================================

INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, clienteid)
VALUES
    ('478758', 'Ahorro',    2000.00, 2000.00, TRUE, 'jose-lema'),
    ('225487', 'Corriente',  100.00,  100.00, TRUE, 'marianela-montalvo'),
    ('495878', 'Ahorro',       0.00,    0.00, TRUE, 'juan-osorio'),
    ('496825', 'Ahorro',     540.00,  540.00, TRUE, 'marianela-montalvo'),
    ('585545', 'Corriente', 1000.00, 1000.00, TRUE, 'jose-lema');