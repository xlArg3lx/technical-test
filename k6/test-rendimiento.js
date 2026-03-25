import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '10s', target: 10 },  // sube a 10 usuarios en 10s
        { duration: '20s', target: 50 },  // sube a 50 usuarios en 20s
        { duration: '10s', target: 0  },  // baja a 0 usuarios en 10s
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% de requests deben responder en menos de 500ms
        http_req_failed:   ['rate<0.01'], // menos del 1% de errores
    },
};

const BASE_CLIENTES = 'http://ms-clientes:8081/api';
const BASE_CUENTAS  = 'http://ms-cuentas:8082/api';

export default function () {

    // GET clientes
    const resClientes = http.get(`${BASE_CLIENTES}/clientes`);
    check(resClientes, {
        'GET /clientes - status 200':          (r) => r.status === 200,
        'GET /clientes - tiempo menor 500ms':  (r) => r.timings.duration < 500,
    });

    sleep(0.5);

    // GET cuentas
    const resCuentas = http.get(`${BASE_CUENTAS}/cuentas`);
    check(resCuentas, {
        'GET /cuentas - status 200':          (r) => r.status === 200,
        'GET /cuentas - tiempo menor 500ms':  (r) => r.timings.duration < 500,
    });

    sleep(0.5);

    // GET movimientos
    const resMovimientos = http.get(`${BASE_CUENTAS}/movimientos`);
    check(resMovimientos, {
        'GET /movimientos - status 200':          (r) => r.status === 200,
        'GET /movimientos - tiempo menor 500ms':  (r) => r.timings.duration < 500,
    });

    sleep(0.5);

    // POST cliente
    const payload = JSON.stringify({
        nombre:        'Usuario Test',
        genero:        'Masculino',
        edad:          25,
        identificacion: `${Math.floor(Math.random() * 9000000000) + 1000000000}`,
        direccion:     'Calle Test 123',
        telefono:      '0987654321',
        clienteId:     `user-${Math.random().toString(36).substring(7)}`,
        contrasena:    '1234',
        estado:        true,
    });

    const resCreate = http.post(`${BASE_CLIENTES}/clientes`, payload, {
        headers: { 'Content-Type': 'application/json' },
    });

    check(resCreate, {
        'POST /clientes - status 201':          (r) => r.status === 201,
        'POST /clientes - tiempo menor 500ms':  (r) => r.timings.duration < 500,
    });

    sleep(1);
}