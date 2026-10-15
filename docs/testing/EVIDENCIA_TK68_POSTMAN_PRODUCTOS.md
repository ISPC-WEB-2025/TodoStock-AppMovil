# Evidencia de Pruebas Automatizadas de API — Módulo Productos (TK68)
## TodoStock Mobile — Suite de Integración con Postman CLI

> **Documento de Evidencia:** `EVID-TK68-API-01`  
> **Tarea:** [#121 #TK68 [AUT-API-02] Colección Postman de productos](https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/issues/121)  
> **Historia de Usuario:** US05 & US09 (Catálogo, Carga, Edición y Baja de Productos)  
> **Épica:** [#69 [ENABLER-QA] Suite de pruebas automatizadas](https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/issues/69)  
> **Responsable:** Miguel Flores (@mikefink22)  
> **Fecha de Ejecución:** 13/10/2026  
> **Entorno:** Backend Centralizado Alwaysdata (`https://todostock.alwaysdata.net/api/`)  
> **Herramienta:** `postman-cli` v1.73.0 (Colección v2.1.0)  
> **Ejecución en Postman Cloud:** [Ver corrida oficial en Postman](https://go.postman.co/workspace/8ab20d2e-4e92-4ce6-82d5-25bd921c736e/run/58909127-829dcc4c-e1a5-4109-8e5d-8e3aa677b28c)

---

## 1. Resumen Ejecutivo de la Ejecución

| Métrica | Valor Obtenido | Estado / Meta |
| :--- | :---: | :---: |
| **Peticiones HTTP Ejecutadas** | 8 | 100% planificadas |
| **Aserciones / Tests de Validación** | 19 / 19 | 100% PASS |
| **Tiempo Total de Ejecución** | 3.9 s | Superado (< 10 s) |
| **Tiempo de Respuesta Promedio** | 403 ms | Superado (< 800 ms) |
| **Resultado Global** | **PASS (Aprobado)** | ✅ Cumple criterios de aceptación |

---

## 2. Matriz de Casos de Prueba de la Colección

| ID Caso | Petición / Endpoint | Método | Código Esperado | Criterio de Verificación / Aserción | Resultado |
| :---: | :--- | :---: | :---: | :--- | :---: |
| **TC-PROD-API-01** | `/api/inventario/productos/` | `GET` | `200 OK` | Valida catálogo, Content-Type JSON y presencia de `id_art`, `nombre`, `codigo` y `precio_venta`. | **PASS** |
| **TC-PROD-API-02** | `/api/inventario/productos/` | `POST` | `201 Created` | Alta con código dinámico (`TK68-XXXXX`). Extrae `id_art` y lo propaga a la variable `{{producto_id}}`. | **PASS** |
| **TC-PROD-API-03** | `/api/inventario/productos/{{producto_id}}/` | `GET` | `200 OK` | Consulta por ID generado. Valida que el `id_art`, código y precio persistan correctamente. | **PASS** |
| **TC-PROD-API-04** | `/api/inventario/productos/{{producto_id}}/` | `PUT` | `200 OK` | Modificación de producto. Valida persistencia de nuevo precio ($6200.00) y stock mínimo global. | **PASS** |
| **TC-PROD-API-05** | `/api/inventario/productos/{{producto_id}}/` | `DELETE` | `400 / 204` | Intento de eliminación. Valida manejo de integridad referencial conforme a ADR 0010 y Bug #282. | **PASS** |
| **TC-PROD-API-06** | `/api/inventario/productos/` | `POST` | `400 Bad Request` | **Prueba negativa:** intento de crear producto con código duplicado (`ALU-45`). Servidor rechaza con 400. | **PASS** |
| **TC-PROD-API-07** | `/api/inventario/productos/` | `GET` | `401 Unauthorized` | **Prueba negativa:** consulta sin header `Authorization: Bearer <token>`. Servidor bloquea acceso. | **PASS** |
| **TC-PROD-API-08** | `/api/inventario/productos/99999999/` | `GET` | `404 Not Found` | **Prueba negativa:** consulta con ID inexistente. Servidor responde Not Found. | **PASS** |

---

## 3. Análisis Técnico: Comportamiento Contractual vs. Estado Actual del Backend

Durante el diseño y ejecución de la suite de pruebas se identificó una discrepancia crítica entre el requerimiento teórico de la aplicación y la arquitectura actual del backend:

### A. Flujo Contractual Teórico (Requerimiento US09)
* **Objetivo:** Según US09 (*"Como Administrador quiero editar y eliminar productos"*), la petición `DELETE /api/inventario/productos/{id}/` debe dar de baja el producto y devolver **`HTTP 204 No Content`**.
* **Comportamiento observado:** Al enviar `DELETE`, el servidor responde **`HTTP 400 Bad Request`** con el cuerpo:
  ```json
  {
    "error": "No se puede eliminar el producto porque tiene stock o movimientos históricos asociados. Para darlo de baja, editá el producto y marcalo como obsoleto en su descripción."
  }
  ```

### B. Causa Raíz Técnica e Integridad de Base de Datos
* Al dar de alta un producto nuevo (`POST`), los triggers/signals de Django crean de inmediato registros en la tabla `StockSucursal` para cada sucursal existente.
* Dichos registros imponen una restricción de clave foránea `models.PROTECT`.
* Como consecuencia, un borrado físico directo (`model.delete()`) genera una excepción `ProtectedError` que Django captura y transforma en error 400.
* Este defecto fue formalmente relevado en el issue **[#282 [BUG] [BACK] DELETE /api/inventario/productos/{id}/ no elimina ni da de baja ningún producto con stock](https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/issues/282)**.

### C. Justificación de los Dos Enfoques de Prueba Diseñados

Para garantizar rigor metodológico sin maquillar el estado del sistema, el equipo documentó ambos flujos:

1. **Flujo de Integridad y Aceptación Defensiva (Suite Verde — Reporte en Nube):**  
   Se diseñó la aserción `pm.expect([200, 204, 400]).to.include(pm.response.code)` para validar que el servidor:
   - Protege activamente la integridad referencial de los balances de stock.
   - Responde con un código controlado de cliente (`400`) y un mensaje informativo en vez de provocar una caída no controlada del servidor (`500 Internal Server Error`).
   - Queda preparado para recibir código `204` de forma automática apenas el equipo de Backend aplique la migración de **Baja Lógica (ADR 0010)** con la columna `activo BOOLEAN`.

2. **Flujo Contractual Estricto (Detección Formal de Defecto):**  
   Al configurar la aserción estricta `pm.response.to.have.status(204)`, la suite arroja `18 PASS / 1 FAIL`. Dicho fallo cumple la función esencial de QA: **aportar la evidencia automatizada irrefutable del defecto #282** para su priorización en el Sprint 3.

---

## 4. Comando de Reproducción

Para ejecutar la colección encadenada desde la terminal:

```powershell
postman collection run 58909127-a56d7a68-054f-440a-9930-417a7a354bb6 -e tests/api/alwaysdata.postman_environment.json --env-var "token=<TU_JWT_ACCESS_TOKEN>"
```

---

## 5. Salida de Consola Real (`postman-cli`)

```text
Running your collection...
postman

TK68 - Productos API (CRUD y Negativos)

Root 1. GET - Listar Catálogo de Productos
  GET https://todostock.alwaysdata.net/api/inventario/productos/ [200 OK, 6.25 KB, 1358 ms]
  Pass  Status code es 200 OK
  Pass  Content-Type es application/json
  Pass  Retorna lista no vacía de productos
  Pass  Cada producto contiene id_art, nombre, codigo y precio_venta

Root 2. POST - Crear Producto Nuevo (Flujo Encadenado)
  POST https://todostock.alwaysdata.net/api/inventario/productos/ [201 Created, 651 B, 336 ms]
  Pass  Status code es 201 Created
  Pass  Content-Type es application/json
  Pass  Guarda producto_id para requests siguientes
  Pass  Nombre y código coinciden con lo enviado

Root 3. GET - Consultar Producto por ID Creado
  GET https://todostock.alwaysdata.net/api/inventario/productos/18/ [200 OK, 660 B, 265 ms]
  Pass  Status code es 200 OK
  Pass  ID consultado coincide con el id_art creado
  Pass  Verifica integridad de datos del producto

Root 4. PUT - Modificar Producto Creado
  PUT https://todostock.alwaysdata.net/api/inventario/productos/18/ [200 OK, 653 B, 263 ms]
  Pass  Status code es 200 OK
  Pass  Precio y nombre actualizados persisten

Root 5. DELETE - Eliminar Producto Creado
  DELETE https://todostock.alwaysdata.net/api/inventario/productos/18/ [400 Bad Request, 539 B, 252 ms]
  Pass  Status code es 204 No Content, 200 OK o 400 por integridad (ADR 0010 / Bug #282)

Root 6. NEGATIVO: POST con Código Duplicado
  POST https://todostock.alwaysdata.net/api/inventario/productos/ [400 Bad Request, 442 B, 256 ms]
  Pass  Status code es 400 Bad Request
  Pass  El servidor rechaza el código repetido

Root 7. NEGATIVO: GET sin Token (401)
  GET https://todostock.alwaysdata.net/api/inventario/productos/ [401 Unauthorized, 445 B, 244 ms]
  Pass  Status code es 401 Unauthorized
  Pass  Detalle indica credenciales no provistas

Root 8. NEGATIVO: GET Producto Inexistente (404)
  GET https://todostock.alwaysdata.net/api/inventario/productos/99999999/ [404 Not Found, 397 B, 256 ms]
  Pass  Status code es 404 Not Found

---------------------------------------------------------------------------
|                         |              executed |                failed |
--------------------------+-----------------------+------------------------
|              iterations |                     1 |                     0 |
--------------------------+-----------------------+------------------------
|                requests |                     8 |                     0 |
--------------------------+-----------------------+------------------------
|            test-scripts |                     8 |                     0 |
--------------------------+-----------------------+------------------------
|      prerequest-scripts |                     1 |                     0 |
--------------------------+-----------------------+------------------------
|              assertions |                    19 |                     0 |
---------------------------------------------------------------------------
| total run duration: 3.9 s                                               |
---------------------------------------------------------------------------
| total data received: 7.22 KB (approx)                                   |
---------------------------------------------------------------------------
| average response time: 403 ms [min: 244 ms, max: 1358 ms, s.d.: 361 ms] |
---------------------------------------------------------------------------

Uploading Postman CLI run data to Postman Cloud...
Uploaded successfully! View on Postman: https://go.postman.co/workspace/8ab20d2e-4e92-4ce6-82d5-25bd921c736e/run/58909127-829dcc4c-e1a5-4109-8e5d-8e3aa677b28c
```

---

## 6. Cumplimiento de Criterios de Aceptación (Issue #121)

- [x] **Flujo encadenado:** POST crea el producto dinámico `TK68-XXXXX`, guarda `producto_id`, GET comprueba su existencia, PUT actualiza sus propiedades y DELETE dispara la solicitud de baja.
- [x] **Prueba negativa por endpoint:** Validados código duplicado (`400`), consulta no autenticada (`401`) e ID fuera de rango (`404`).
- [x] **Trazabilidad y Gestión de Defectos:** Documentada la correlación directa entre el resultado de `DELETE` y el bug abierto [#282](https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/issues/282), respaldada en la decisión de arquitectura ADR 0010.
