# TodoStock Mobile 📱
Aplicación móvil nativa Android (Java) para la gestión de inventario en tiempo real.

---

## 📌 Sobre el proyecto

Muchas pymes controlan su inventario con planillas o procesos manuales, lo que genera falta de visibilidad, pérdida de ventas por quiebres de stock y desorganización entre sucursales. **TodoStock App** extiende la solución web a un dispositivo móvil para que el control de stock, productos y movimientos entre sucursales se pueda realizar desde cualquier lugar de forma ágil, segura y centralizada.

### Funcionalidades principales (según rol)

| Rol | Alcance Operativo y Jerarquía de Permisos |
|---|---|
| **SuperAdministrador** (`is_superuser=True`) | **Control Total:** Crear cuentas de administradores, aprobar solicitudes pendientes de usuarios con rol Administrador, editar perfiles y restablecer claves de administradores y empleados, gestión global de sucursales e inventario. |
| **Administrador Estándar** (`rol=ADMINISTRADOR`) | **Permiso Acotado:** Aprobar solicitudes pendientes y editar perfiles **únicamente de usuarios con rol Empleado (`es_empleado`)**. Carga y alta de productos con categorías, consulta de stock consolidado y por sucursal, registro de movimientos atómicos de stock entre sucursales, selección de sucursal activa y soporte. La API le restringe el control sobre otros administradores (`HTTP 403 Forbidden`). |
| **Empleado / Vendedor** (`es_empleado=True`) | **Operativa Diaria:** Consulta de stock por sucursal, registro y consulta de movimientos de stock (entradas, salidas y traslados entre sucursales), selección de sucursal activa, edición de su propio perfil y contacto a soporte. |

#### 📝 Registro de Usuarios
Cualquier usuario puede solicitar el alta desde el formulario de registro ingresando **nombre, email, DNI, fecha de nacimiento (ingreso directo por teclado con máscara) y contraseña**. Tras registrarse, la cuenta queda en estado **pendiente** hasta que un **Administrador** le habilite el acceso y le asigne su rol correspondiente (Administrador o Empleado).

---

## 🛠️ Tecnologías e Infraestructura

- **Frontend móvil:** Android nativo (Java, Android SDK, Material Design Components)
- **Red & API REST:** Retrofit 2 + OkHttp 3 con `AuthInterceptor` e inyección de tokens JWT
- **Persistencia & Cifrado:** `EncryptedSharedPreferences` (`SesionManager`)
- **Backend:** Django 6 + Django REST Framework (desplegado en Alwaysdata `https://todostock.alwaysdata.net/api/`)
- **Base de datos:** MySQL / MariaDB (transacciones ACID)
- **Autenticación:** JWT (JSON Web Tokens vía SimpleJWT)
- **Requisitos mínimos:** Android 7.0 (API 24) o superior

---

## 👥 Equipo CodeLab

| Integrante | Rol en el proyecto |
|---|---|
| Candelaria | Desarrollo Full Stack |
| Miguel | Desarrollo Full Stack |
| Pedro | Desarrollo Full Stack |
| Virginia | Desarrollo Full Stack |
| Aylen | Scrum Master / Desarrollo Full Stack |
| Octavio | Documentación, Legales y QA |

---

## 🌿 Esquema de branching

Basado en Gitflow simplificado:

- `main` → versiones estables, listas para producción (cierre de cada Sprint)
- `develop` → integración de las features en desarrollo
- `release` → preparación de cada entrega/Sprint
- `feature/nombre-apellido-TKXX` → rama individual de cada integrante

```
feature/candelaria-XXXXX  ─┐
feature/miguel-XXXXX      ─┤
feature/pedro-XXXXX       ─┼──▶ develop ──▶ release ──▶ main
feature/virginia-XXXXX    ─┤
feature/aylen-XXXXX       ─┤
feature/octavio-XXXXX     ─┘
```

---

## 🚀 Cómo correr el proyecto

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/ISPC-WEB-2025/TodoStock-AppMovil.git
   ```
2. Abrir el proyecto en **Android Studio**.
3. Sincronizar Gradle (`Sync Project with Gradle Files`).
4. Ejecutar en un emulador (AVD, Android 7.0+ / API 24+) o en un dispositivo físico con depuración USB habilitada.

---

## 🔌 Configuración de Conexión a la API (Backend)

Por defecto, la aplicación se conecta automáticamente al **backend en producción desplegado en la nube**:
👉 **`https://todostock.alwaysdata.net/api/`** *(es el mismo backend centralizado que utiliza la plataforma web TodoStock)*.

Por ende, **ya no hace falta levantar ningún servidor de forma local** para ejecutar ni probar la aplicación móvil.

### ⚙️ Opción para Desarrollo Local (Opcional)
Si querés conectar la app a una instancia local de backend en lugar del servidor desplegado, podés configurarlo mediante `local.properties`:

**Si probás con el emulador de Android Studio:**
1. Levantá el backend localmente: `python manage.py runserver`
2. En `local.properties` (en la raíz del proyecto), configurá:
   ```properties
   api.baseUrl=http://10.0.2.2:8000/
   ```

**Si probás con un celular físico conectado por Wi-Fi / USB:**
1. Conectá el celular a la misma red Wi-Fi de tu computadora.
2. Levantá el backend local aceptando conexiones: `python manage.py runserver 0.0.0.0:8000`
3. Agregá en `local.properties`:
   ```properties
   api.baseUrl=http://192.168.X.X:8000/
   ```
4. Sincronizá Gradle (`Sync Now`) y volvé a ejecutar la app.

---

## 📊 Gestión, Enlaces y Plantillas del Proyecto (#TK108)

- **Roadmap Interactivo del Proyecto:** https://ispc-web-2025.github.io/TodoStock-Mobile-RoadMap/ *(Username: `CodeLab` \| Password: `RoadMap-2026-Mobile`)*
- **Backlog Consolidado de Sprint 2 (#TK108):** [`docs/backlog/sprint2.md`](docs/backlog/sprint2.md)
- **Plantillas de Trabajo en GitHub (#TK108):**
  - [Plantilla de Reporte de Bugs](.github/ISSUE_TEMPLATE/bug_report.md)
  - [Plantilla de Solicitud de Feature / Historias de Usuario](.github/ISSUE_TEMPLATE/feature_request.md)
  - [Plantilla de Pull Request (PR)](.github/PULL_REQUEST_TEMPLATE.md)
- **API REST Backend (Desplegado):** https://todostock.alwaysdata.net/api/
- **Aplicación Web:** https://todo-stock.vercel.app/
- **Tablero Kanban:** https://github.com/orgs/ISPC-WEB-2025/projects/18
- **Milestones / Sprints:** https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/milestones
- **Wiki del proyecto:** https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/wiki
- **Diseño (Figma):** https://www.figma.com/design/Aa1IGF6PBIY5ID9SQHCCG4/StockSip---Mobile-Application--Community-

---

## 🏛️ Decisiones de Arquitectura (ADR-0001 a ADR-0010)

El proyecto se rige por los Registros de Decisiones de Arquitectura documentados en `lib/Docs/ADR/`:
- **ADR-0001:** Filtrado de movimientos en servidor.
- **ADR-0002:** Registro dual de traslados entre sucursales.
- **ADR-0003:** Desacople de operador en movimientos.
- **ADR-0004:** Enforcement global de autenticación JWT.
- **ADR-0005:** Sucursal central y distribución jerárquica.
- **ADR-0006:** Arquitectura de consumo de backend desde App Mobile.
- **ADR-0007:** Unificación operativa de roles Vendedor/Depósito en `es_empleado`.
- **ADR-0008:** Blindaje de jerarquía administrativa y SuperAdmin.
- **ADR-0009:** Modelo de log de auditoría desacoplado (`LogAuditoria`).
- **ADR-0010:** Baja lógica de productos (`activo = False`) preservando trazabilidad.

---

## 🔒 Seguridad

- Autenticación segura mediante **JWT** (Access y Refresh Tokens) gestionada por `SesionManager`.
- **Regla de contraseña:** Mínimo **9 caracteres**, debiendo incluir letras (mayúsculas/minúsculas), números y al menos **un carácter especial** (`Validadores.java`).
- **Gestión de Registro y Permisos:** Los usuarios registrados ingresan en estado pendiente hasta la asignación de rol (**Administrador** / **Empleado**) por parte de un Administrador.
- Control de sesiones y comunicación encriptada vía **HTTPS (SSL/TLS)** en `network_security_config.xml`.
- Detalle completo del plan de seguridad en la sección **Ciberseguridad** de la Wiki.

---

## 🧪 Testing y Accesibilidad

- **Suites Unitarias Locales (`app/src/test/`):** `ValidadoresTest.java` (patrón AAA, valores límite y clases de equivalencia), `StockItemTest.java` (stock bajo umbral y mappers) y `LoginViewModelTest.java` (ViewModel aislado con Mockito).
- **Suites de Integración de Red (`app/src/test/`):** `AuthRepositoryLoginTest.java`, `AuthRepositoryTest.java` y `ParseoRespuestasTest.java` (contratos HTTP 200 y 500 con `MockWebServer`, sin tocar red real).
- **Suite Instrumentada de UI (`app/src/androidTest/`):** `LoginFlujoTest.java` con **Espresso**, patrón **Screen Object** (`LoginScreen`, `MenuPrincipalScreen`) y auditoría automática `AccessibilityChecks.enable()`.
- **Configuración de credenciales para tests instrumentados y Postman (`secrets.properties`):**
  Para ejecutar pruebas que interactúan con el backend sin exponer credenciales en el repositorio:
  1. Copiar la plantilla `secrets.properties.example` como `secrets.properties` en la raíz del proyecto (archivo ignorado por Git).
  2. Completar las variables locales:
     ```properties
     POSTMAN_API_KEY=tu_api_key_aqui
     TEST_USER_EMAIL=tu_usuario_de_prueba@test.com
     TEST_USER_PASSWORD=tu_clave_de_prueba
     ```
  3. Sincronizar Gradle (`Sync Now`) y ejecutar `LoginFlujoTest` desde Android Studio en el emulador.
- **Accesibilidad:** Cumplimiento de WCAG 2.0 (Disposición ONTI 6/2019) y WCAG 2.2, objetivos táctiles de 48×48 dp, compatibilidad TalkBack y contraste > 4.5:1.
- **Documentación de Pruebas:** [`docs/PLAN_DE_PRUEBAS.md`](docs/PLAN_DE_PRUEBAS.md) y [Wiki Testing y Calidad](https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/wiki/Testing-y-Calidad)

---

## 📄 Licencia

*CodeLab — Proyecto Integrador II (2026)*
