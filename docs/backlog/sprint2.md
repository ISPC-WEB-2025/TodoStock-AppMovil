# 📋 Product Backlog & Release Summary - Sprint 2 (v2.0)

**Proyecto:** TodoStock Mobile — Android Nativo (Java / Material Design 3)  
**Periodo del Sprint 2:** 17 de septiembre de 2026 al 14 de octubre de 2026  
**Responsable de Gestión:** Octavio Arnaudo / Equipo CodeLab (#TK108)  
**Story Points Entregados:** 106 SP (Acumulado Total del Proyecto: 142 SP)  

---

## 🎯 Objetivos y Alcance del Sprint 2

El Sprint 2 consolida el incremento funcional **v2.0** de la aplicación móvil TodoStock, conectando las 12 `Activities` nativas con la API REST centralizada en Django (desplegada en Alwaysdata `https://todostock.alwaysdata.net/api/`), implementando seguridad por tokens JWT, gestión de perfiles, legislación de protección de datos (Ley 25.326), arrepentimiento/baja de cuenta y la suite de pruebas unitarias automatizadas.

---

## 📊 Historias de Usuario (#US) y Tareas Técnicas (#TK)

### 🔐 Módulo 01: Autenticación, Sesiones y Navegación (#US01)
- **#TK05:** Retrofit login contra la API (`POST /api/usuarios/login/`) y guardado JWT en `SesionManager` (2 SP - PR #49).
- **#TK06:** `AuthInterceptor` HTTP para inyectar cabecera `Authorization: Bearer <token>` (1 SP - PR #50).
- **#TK07:** Botón "Cerrar sesión" con invalidación local y redirección a Login (1 SP - PR #160).
- **#TK90:** Configuración base de la app para consumir la API (`ApiClient`) (2 SP - PR #157).
- **#TK91:** `SplashActivity`: decidir destino según sesión activa en `SesionManager` (1 SP - PR #157).
- **#TK92:** Corregir defecto visual de errores en Login (1 SP - PR #162).
- **#TK112:** `AuthRepository` + `LoginViewModel` para arquitectura de Login (2 SP - PR #160).

### 🏢 Módulo 02: Selección de Sucursales (#US02)
- **#TK12:** Conexión con `GET /api/inventario/sucursales/` mediante `SucursalDto` (2 SP - PR #185).

### 📦 Módulo 03: Inventario y Stock (#US03)
- **#TK16:** Conectar con `InventarioApiService` (`GET /api/inventario/productos/{id}/stock/`) (3 SP - PR #216, PR #220).
- **#TK17:** Alerta visual de stock bajo el umbral mínimo en `StockAdapter` (2 SP - PR #216).

### ✉️ Módulo 04: Contacto y Soporte (#US04)
- **#TK20:** Selector de sucursal del formulario de contacto con datos reales (1 SP - PR #192).
- **#TK42:** Endpoint de contacto y envío real (`ContactoApiService`, `ContactoRequest`) (1 SP - PR #192).

### ➕ Módulo 05: Carga de Productos (#US05)
- **#TK23:** Conexión con `POST /api/inventario/productos/` con selección de categoría (2 SP - PR #197, PR #287).
- **#TK93:** Corregir defecto visual de errores en Carga de Producto (2 SP - PR #197).

### 🔄 Módulo 06: Movimientos Atómicos (#US06)
- **#TK27:** Conexión con API atómica de transferencias de stock (`MovimientoRequest`, ADR-0002) (3 SP - PR #185).

### 📝 Módulo 07: Registro de Usuarios (#US07)
- **#TK34:** `RegistroActivity`: layout, validaciones de edad mínima (+18) y API (4 SP - PR #171, PR #173).
- **#TK35:** Verificación de endpoint de registro `POST /api/usuarios/registro/` (1 SP - PR #173).
- **#TK47:** Política de contraseñas robustas y validación en `Validadores.java` (1 SP - PR #171, PR #223).
- **Fix #281:** Entrada de fecha por teclado con máscara directa en `RegistroActivity` (PR #283).

### 👑 Módulo 08: Administración y RBAC (#US08)
- **#TK36:** API de usuarios (listar, editar, baja) restringido a Administrador (2 SP - Backend).
- **#TK37:** Gestión de usuarios: lista, edición y baja con diálogo modal (2 SP - App/Web).
- **#TK48:** Autorización por perfiles (Administrador / Empleado) y menú condicional (2 SP - PR #160, ADR-0008).
- **#TK109:** Cargar usuario y aprobar registros pendientes desde la administración (1 SP - Backend).

### ✏️ Módulo 09: Edición y Eliminación (#US09)
- **#TK38:** API de productos: baja lógica (`activo = False`, ADR-0010) (2 SP - PR #323).
- **#TK39:** Listado de productos con opciones de edición y eliminación para Administrador (2 SP - PR #216).

### 🎨 Módulo 10: Ayuda y Adaptabilidad (#US10 / #RNF-UX)
- **#TK40:** Activity e instructivo de Ayuda visual para la aplicación (2 SP - PR #217).
- **#TK41:** Layouts landscape y tablet, y conservación de estado al rotar (2 SP - PR #217).

### ⚖️ Módulo 11: Legislación, Privacidad y Arrepentimiento (#US11)
- **#TK52:** Legislación en la app: `TerminosActivity` y opción 'Darme de baja' (`BajaDeCuenta`) (3 SP - PR #180, PR #195).
- **#TK105:** Sección de legislación en la Wiki y verificación de cumplimiento normativo (2 SP - PR #217).

### 👤 Módulo 12: Perfil Propio (#US12)
- **#TK94:** `PerfilApiService` y DTOs de perfil (`PerfilResponse`, `PerfilUpdateRequest`) (2 SP - PR #180).
- **#TK95:** `PerfilActivity` y `EditarPerfilActivity`: ver y editar mis datos personales (2 SP - PR #180).

### 🛡️ Módulo 13: Hardening de Ciberseguridad (#RNF-SEG)
- **#TK45:** Secreción de credenciales, eliminación de claves hardcodeadas y uso de `.env` (1 SP).
- **#TK46:** Configuración de CORS, `ALLOWED_HOSTS` y límite de intentos de login (1 SP).
- **#TK49:** Control de sesiones: access token corto, refresh token con rotación (2 SP).
- **#TK50:** TLS: redirección HTTPS y `network_security_config` en Android (1 SP).
- **#TK51:** Auditoría de seguridad: registro de eventos en la tabla `LogAuditoria` (2 SP - ADR-0009).
- **#TK53:** Sistema de Gestión de Riesgos y revisión del plan de seguridad (1 SP).
- **#TK54:** Video corto sobre arquitectura JWT, autenticación y autorización (1 SP).
- **#TK55..59:** Casos de prueba de seguridad (TC-SEG-01 al TC-SEG-05) (5 SP).

### 🧪 Módulo 14: Pruebas Automatizadas y Accesibilidad (#ENABLER-QA)
- **#TK60:** `[AUT-UNIT-01]` Tests unitarios de validadores en `ValidadoresTest.java` (3 SP - PR #213, PR #226, PR #267).
- **#TK61:** `[AUT-UNIT-02]` Tests unitarios de stock bajo y mappers en `StockItemTest.java` (2 SP - PR #277).
- **#TK62:** `[AUT-UNIT-03]` Tests unitarios de repositorios (2 SP).
- **#TK63:** `[AUT-UNIT-04]` Tests unitarios de token, sesión y `AuthInterceptor` (2 SP).
- **#TK80..89:** Plan de Pruebas, ejecución de casos de prueba y planilla de trazabilidad (10 SP).
- **#TK96:** Informe de Accesibilidad y auditoría visual de la app (1 SP - PR #264, PR #265).
- **#TK110:** Casos de prueba de API y reporte de bugs del endpoint de registro (1 SP - PR #171).

### 📚 Módulo 15: Documentación, Proceso y Gestión (#MGMT / #RELEASE)
- **#TK28:** Setup del repo, estrategia GitFlow, Kanban y Wiki (2 SP - PR #47).
- **#TK29:** Vincular documentación IEEE830 desde la Wiki del proyecto (1 SP - PR #47).
- **#TK30:** Consolidar plan de pruebas y los 12 test cases en la Wiki (2 SP - PR #48).
- **#TK31:** Redactar borrador del plan de ciberseguridad y arquitectura JWT (2 SP - PR #48).
- **#TK32:** Cargar Anexo II del IEEE830 con capturas de pantalla de la app (1 SP - PR #48).
- **#TK33:** GitHub Action para notificaciones de merges a `develop` (1 SP - PR #19).
- **#TK43:** Deploy del backend en Alwaysdata y baseUrl de release (1 SP).
- **#TK44:** APK release en `main` e instrucciones de instalación (1 SP).
- **#TK97:** Grabar y editar el video demo grupal (máx. 3 minutos) (2 SP).
- **#TK98:** Milestone Sprint 2, etiquetas e Issues en GitHub (2 SP).
- **#TK99:** Gestión de reporte y seguimiento de bugs (1 SP).
- **#TK100:** Registro de las 4 ceremonias del Sprint 2 en la Wiki (2 SP).
- **#TK101:** IEEE830 actualizado con los requerimientos del Sprint 2 (1 SP).
- **#TK102:** Diagrama de navegabilidad y Anexo II con capturas nuevas (2 SP).
- **#TK103:** Wiki: avances individuales, de equipo y enlaces externos (1 SP).
- **#TK104:** Product Backlog de la Wiki alineado con el backlog general (1 SP).
- **#TK106:** README del repositorio actualizado (1 SP - PR #161).
- **#TK107:** Guion y coordinación del video demo grupal (1 SP).
- **#TK108:** Commit en `main` con el backlog y plantillas de Issues/PR (1 SP).
- **#TK111:** Guía de Aclaraciones para Evaluación Docente (1 SP).
- **#TK113:** Pantallas intermedias Hub de Stock (`MenuStockActivity`) y Movimientos (`MenuMovimientosActivity`) (2 SP - PR #276).
- **#TK114:** Reorganización del Menú Principal con distribución vertical limpia y sin numeración (1 SP - PR #275).

---

## 🏆 Criterios de Aceptación y Estado del Release

- [x] **100% de Historias de Usuario (#US01 a #US12) completadas e integradas.**
- [x] **Suite de pruebas unitarias automatizadas (`ValidadoresTest.java` y `StockItemTest.java`) en estado VERDE.**
- [x] **Documentación IEEE 830, Wiki, README y diagramas UML/DER sincronizados con el código entregado.**
