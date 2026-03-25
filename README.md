# Prueba Técnica — Arquitectura Microservicios

## Tecnologías

- Java 21
- Spring Boot 3.5.12
- Spring Data JPA + Hibernate
- PostgreSQL 15
- RabbitMQ 3.12
- Resilience4j
- Docker + Docker Compose
- Maven
- JUnit 5 + Mockito

---

## Arquitectura

El sistema está compuesto por dos microservicios independientes que se comunican de forma asíncrona a través de RabbitMQ.
```
ms-clientes  →  RabbitMQ  →  ms-cuentas
    ↓                             ↓
db_clientes                  db_cuentas
```

### ms-clientes (puerto 8081)
Gestiona las entidades Persona y Cliente. Publica eventos a RabbitMQ cuando se crea un cliente.

### ms-cuentas (puerto 8082)
Gestiona las entidades Cuenta y Movimientos. Consume eventos de RabbitMQ. Expone el reporte de estado de cuenta.

---

## Estructura del proyecto
```
technical-test/
├── docker-compose.yml
├── BaseDatos.sql
├── README.md
├── ms-clientes/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/techtest/msclientes/
│       │   ├── domain/
│       │   │   ├── entity/
│       │   │   └── repository/
│       │   ├── application/
│       │   │   ├── dto/
│       │   │   ├── mapper/
│       │   │   └── service/
│       │   └── infrastructure/
│       │       ├── controller/
│       │       ├── exception/
│       │       ├── messaging/
│       │       └── response/
│       └── test/
└── ms-cuentas/
    ├── Dockerfile
    ├── pom.xml
    └── src/
        ├── main/java/com/techtest/mscuentas/
        │   ├── domain/
        │   ├── application/
        │   └── infrastructure/
        └── test/
```

---

## Patrones y buenas prácticas aplicadas

- **Repository Pattern** — abstracción de acceso a datos mediante interfaces `JpaRepository`
- **DTO Pattern** — separación entre entidades de dominio y objetos de transferencia
- **Mapper Pattern** — conversión explícita entre entidades y DTOs
- **Service Interface + Impl** — desacoplamiento entre contrato e implementación
- **Global Exception Handler** — manejo centralizado de excepciones con `@RestControllerAdvice`
- **API Response Wrapper** — respuestas estandarizadas con formato `{ status, message, data, timestamp }`
- **Layered Architecture** — separación en capas `domain`, `application`, `infrastructure`

---

## Despliegue con Docker

### Requisitos
- Docker
- Docker Compose

### Pasos
```bash
# 1. Clonar el repositorio
git clone <url-repositorio>
cd technical-test

# 2. Levantar todos los servicios
docker-compose up --build

# 3. Verificar que todos los servicios estén corriendo
docker-compose ps
```

### Servicios disponibles

| Servicio | URL |
|---|---|
| ms-clientes | http://localhost:8081/api |
| ms-cuentas | http://localhost:8082/api |
| RabbitMQ Management | http://localhost:15672 |
| PostgreSQL clientes | localhost:5432 |
| PostgreSQL cuentas | localhost:5433 |

### Credenciales RabbitMQ
```
usuario: admin
contraseña: admin123
```

---

## Endpoints

### ms-clientes (http://localhost:8081/api)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | /clientes | Listar clientes |
| GET | /clientes/{id} | Buscar cliente por id |
| POST | /clientes | Crear cliente |
| PUT | /clientes/{id} | Actualizar cliente |
| DELETE | /clientes/{id} | Eliminar cliente |

### ms-cuentas (http://localhost:8082/api)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | /cuentas | Listar cuentas |
| GET | /cuentas/{id} | Buscar cuenta por id |
| POST | /cuentas | Crear cuenta |
| PUT | /cuentas/{id} | Actualizar cuenta |
| DELETE | /cuentas/{id} | Eliminar cuenta |
| GET | /movimientos | Listar movimientos |
| GET | /movimientos/{id} | Buscar movimiento por id |
| POST | /movimientos | Registrar movimiento |
| DELETE | /movimientos/{id} | Eliminar movimiento |
| GET | /reportes | Reporte estado de cuenta |

### Ejemplo reporte
```
GET /reportes?clienteId=marianela-montalvo
    &fechaInicio=2026-01-01T00:00:00
    &fechaFin=2026-12-31T23:59:59
```

---

## Correr pruebas
```bash
# ms-clientes
cd ms-clientes
./mvnw test

# ms-cuentas
cd ms-cuentas
./mvnw test
```

---

## Factores de calidad contemplados

### Rendimiento
- `@Transactional(readOnly = true)` en todas las consultas de solo lectura para optimizar el rendimiento de Hibernate
- Índices en columnas de alta frecuencia de consulta: `clienteid`, `identificacion`, `numero_cuenta`, `fecha`
- `FetchType.LAZY` en relaciones JPA para evitar consultas innecesarias
- Dos bases de datos separadas eliminando contención entre microservicios
- Pool de conexiones HikariCP configurado por defecto en Spring Boot

**Mejora futura:** Implementar caché con Redis para consultas frecuentes como el reporte de estado de cuenta.

### Escalabilidad
- Arquitectura de microservicios permite escalar cada servicio de forma independiente
- Comunicación asíncrona con RabbitMQ desacopla los servicios evitando cuellos de botella
- Cada microservicio tiene su propia base de datos eliminando dependencias de datos
- Contenedores Docker facilitan el despliegue de múltiples instancias

**Escalar horizontalmente:**
```bash
docker-compose up --scale ms-clientes=3 --scale ms-cuentas=3
```

**Mejora futura:** Implementar Nginx como load balancer y migrar a Kubernetes para orquestación automática.

### Resiliencia
- `healthcheck` en todos los servicios de infraestructura en `docker-compose.yml`
- `depends_on` con `condition: service_healthy` garantiza el orden correcto de arranque
- Colas RabbitMQ configuradas como `durable` — los mensajes sobreviven reinicios del broker
- Resilience4j configurado con Circuit Breaker para proteger comunicaciones síncronas futuras
- Comunicación asíncrona como estrategia principal — si `ms-cuentas` cae, `ms-clientes` sigue operando y los eventos se acumulan en la cola hasta que el servicio se recupere

**Mejora futura:** Implementar retry automático con backoff exponencial en el consumidor de RabbitMQ y Dead Letter Queue para mensajes que fallen repetidamente.

---

## Colección Postman

Importar el archivo `postman_collection.json` en Postman para acceder a todos los endpoints preconfigurados con los casos de uso del ejercicio.

---

## Decisiones arquitectónicas

### ¿Por qué comunicación asíncrona?
`ms-cuentas` necesita saber que un cliente existe antes de crear una cuenta. La solución síncrona sería hacer un HTTP request de `ms-cuentas` a `ms-clientes`, creando acoplamiento directo. Con RabbitMQ, cuando se crea un cliente se publica un evento `cliente.creado` que `ms-cuentas` consume. Si `ms-clientes` cae, `ms-cuentas` sigue funcionando con los datos que ya tiene.

### ¿Por qué Table Per Class para la herencia?
La estrategia `JOINED` mantiene la normalización — `personas` y `clientes` son tablas separadas con FK entre ellas. Evita columnas nulas que tendría la estrategia `SINGLE_TABLE` y es más limpia para consultas directas a cada entidad.

### ¿Por qué dos bases de datos separadas?
Cada microservicio es dueño de sus datos. Esto permite escalar, desplegar y hacer backup de cada base de datos de forma independiente, y evita que un cambio de esquema en un servicio afecte al otro.