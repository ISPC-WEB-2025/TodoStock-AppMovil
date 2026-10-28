# 🧪 Plan de Pruebas, Calidad de Software y Testing (Sprint 1 & Sprint 2)

Responsable de Calidad: **Octavio Arnaudo**  
Planilla de Trazabilidad Externa: [Google Sheets - Planilla General de Test Cases](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?usp=drive_link)  

---

## 📊 Links Directos a Hojas de Test Cases por Área (Google Sheets)

| Área / Módulo | Descripción de Hoja de Prueba | Enlace Directo a Hoja Externa |
| :--- | :--- | :--- |
| **Aclaraciones** | Criterios generales, reglas de asignación y ejecuciones | 📄 [Ver Hoja Aclaraciones](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=2099542543#gid=2099542543) |
| **Login / Autenticación** | Casos de prueba de inicio de sesión y sesión JWT | 🔐 [Ver Hoja Login](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=1952750266#gid=1952750266) |
| **Registro de Usuario** | Casos de prueba de alta de cuenta, edad +18 y contraseñas | 📝 [Ver Hoja Registro](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=349361852#gid=349361852) |
| **Selección de Sucursal** | Casos de prueba de listado y filtrado por sucursal | 🏢 [Ver Hoja Selección de Sucursal](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=1797528588#gid=1797528588) |
| **Stock e Inventario** | Casos de prueba de consulta de existencias y umbral mínimo | 📦 [Ver Hoja Stock](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=237179274#gid=237179274) |
| **Movimientos de Stock** | Casos de prueba de transferencias atómicas entre sucursales | 🔄 [Ver Hoja Movimientos](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=1940159724#gid=1940159724) |
| **Carga de Producto** | Casos de prueba de alta, edición y catálogo de productos | ➕ [Ver Hoja Carga de Producto](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=832156876#gid=832156876) |
| **Contacto y Soporte** | Casos de prueba de envío de mensajes e incidencias | ✉️ [Ver Hoja Contacto](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=2053023542#gid=2053023542) |
| **Seguridad & Hardening** | Casos de prueba de ciberseguridad, permisos y auditoría | 🛡️ [Ver Hoja Seguridad](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=2083563036#gid=2083563036) |

---

## 🎯 1. Testing y Calidad del Sprint 2 (Incremento v2.0)

### 🧪 1.1 Cobertura de Pruebas Unitarias e Integración Android (`app/src/test/`)

En el cliente Android nativo se implementaron ocho suites automatizadas en `app/src/test/java/com/ispc/todostock/`:

1. **`ValidadoresTest.java` (TK60 - PR #213, PR #226, PR #267):**
   - **Patrón de Diseño:** Arrange-Act-Assert (AAA).
   - **Casos de Prueba:** Formato de e-mail, fortaleza de contraseña (mínimo 9 caracteres, mayúscula, número y especial), edad mínima +18 y formato de DNI.
2. **`StockItemTest.java` (TK61 - PR #277, PR #323):**
   - **Patrón de Diseño:** Arrange-Act-Assert (AAA).
   - **Lógica Probada:** Evaluación del cálculo de stock bajo el umbral mínimo (`isStockBajo()`), asignación de colores de advertencia y mapeo correcto entre DTOs de API REST (`StockSucursalDto`) e instancias de modelo de UI (`StockItem`).
3. **`LoginViewModelTest.java` (TK62 - PR #293):**
   - **Pruebas de ViewModel y Repositorio:** Evaluación de estados de UI de autenticación (`ResultadoLogin`), manejo de tokens JWT y emulación de llamadas asíncronas con `AuthRepository` simulado.
4. **`network/ParseoRespuestasTest.java` (AUT-NET-01 - TK64 - PR #292):**
   - **Pruebas de Integración de Red (MockWebServer - 200 OK):** Verificación del parseo de respuestas HTTP 200 OK en JSON hacia DTOs (`LoginResponse`, `SucursalDto`, `PerfilResponse`, `StockSucursalDto`).
5. **`repositorio/AuthRepositoryTest.java` (AUT-NET-02 - TK65 - PR #295, PR #297):**
   - **Pruebas de Integración de Red con Manejo de Errores (MockWebServer - HTTP 500):** Verificación del flujo de autenticación exitoso, guardado de tokens y manejo seguro de fallas de servidor HTTP 500 (con y sin cuerpo HTML) en `AuthRepository`, garantizando que el estado de UI `ResultadoLogin.Error` informe limpiamente al usuario sin romper la aplicación.
6. **`network/MovimientoRequestTest.java` (AUT-UNIT-05 - TK118 - PR #302):**
   - **Patrón de Diseño:** Arrange-Act-Assert (AAA).
   - **Lógica Probada:** Serialización JSON estricta y construcción de payloads para los 4 tipos de movimientos (`Traslado`, `Entrada`, `Salida` por venta y `Salida` por merma/rotura).
7. **`BajaDeProductoTest.java` (AUT-UNIT-06 - TK122 - PR #323):**
   - **Patrón de Diseño:** Arrange-Act-Assert (AAA).
   - **Lógica Probada:** Detección de inactividad (`estaInactivo()`), remoción de prefijo `[INACTIVO]` y blindaje ante valores nulos para excluir productos dados de baja lógica en los selectores de movimientos.
8. **`MascaraFechaWatcherTest.java` (TK325 - PR #326):**
   - **Patrón de Diseño:** Arrange-Act-Assert (AAA).
   - **Lógica Probada:** Batería de 10 tests unitarios que valida formateo en tiempo real (`dd/MM/yyyy`), auto-inserción de barras, retroceso seguro con backspace, normalización de pegado y truncado a 10 caracteres.

---

### 🤖 1.2 Pruebas Automatizadas de Interfaz de Usuario (UI / Espresso) y Colecciones Postman (PR #298, PR #299)

- **Espresso UI Tests (AUT-UI-01 - TK70 - PR #299):**
  - **Ubicación:** `app/src/androidTest/java/com/ispc/todostock/LoginFlujoTest.java`.
  - **Patrón:** Page Object Model (`LoginScreen.java`, `MenuPrincipalScreen.java`).
  - **Herramientas & Seguridad:** Integración de `AccessibilityChecks.enable()` para auditoría visual automática durante la ejecución e `IdlingResource` para sincronización de llamadas de red.
  - **Flujo Validado:** Inicio de sesión completo desde `LoginActivity`, ingreso de credenciales, recepción de token JWT y transición verificada hacia `MenuPrincipalActivity`.
- **Colecciones de Prueba Postman / Newman (AUT-API-02 - TK68 - PR #298):**
  - **Ubicación:** `app/src/test/java/com/ispc/todostock/feat/PostmanConfig.java` y colección JSON de Postman.
  - **Ámbito:** Pruebas automáticas de integración de API para el catálogo completo de productos.

---

### 🐍 1.3 Cobertura de Pruebas Backend Django REST (`lib/Backend/`)

El submódulo backend en `lib/` cuenta con suites automatizadas integradas en el pipeline de GitHub Actions (`.github/workflows/django-tests.yml`):

- **`lib/Backend/usuarios/tests.py` (729 líneas):** Pruebas de autenticación JWT, creación de SuperUser, permisos RBAC, cambio de clave y desactivación de cuenta.
- **`lib/Backend/inventario/test_mobile_api.py` (226 líneas):** Pruebas específicas para el consumo de la app móvil (Endpoints `/api/inventario/productos/`, `/api/inventario/sucursales/`, `/api/inventario/movimientos/`).
- **`lib/Backend/inventario/test_traslados.py` (177 líneas):** Verificación de transacciones ACID en transferencias duales de stock entre sucursales.

---

### 🐛 1.4 Ciclo de Vida y Resolución de Defectos (Issues #167, #168, #169, #183, #184)

| Bug ID | Módulo / Componente | Descripción del Defecto | Severidad | PR Corrección (`issues.md`) | Verificación |
| :--- | :--- | :--- | :---: | :--- | :--- |
| **#167** | Registro / API | El registro aceptaba contraseñas débiles | Alta | PR #171, PR #223 | `ValidadoresTest.java` & API HTTP 400 |
| **#168** | Registro / API | Retornaba HTTP 500 al enviar DNI repetido | Alta | PR #173 | API HTTP 409 Conflict |
| **#169** | Registro / API | Aceptaba DNI con letras o longitud inválida | Media | PR #171 | `ValidadoresTest.java` |
| **#183** | Movimientos Stock | TC-MOV-007: Fallaba validación de stock origen | Alta | PR #185 | Peer Testing y log de API |
| **#184** | Perfil Usuario | Excepción visual al editar datos en nulo | Media | PR #180 | `EditarPerfilActivity` |

---

### 🚀 1.5 Cómo Ejecutar los Tests Automatizados (Guía Paso a Paso — #TK84)

Para que cualquier integrante del equipo o evaluador pueda replicar y ejecutar la totalidad de las pruebas automatizadas del proyecto, se deben respetar los siguientes requisitos de entorno y comandos:

#### ⚙️ Requisitos Previos del Entorno

- **IDE:** Android Studio Giraffe (2022.3.1) — versión oficial fijada para el proyecto.
- **JDK:** **Java 17** provisto internamente por Android Studio Giraffe (carpeta `jbr/`).  
  > ⚠️ **Importante:** NO utilizar el JDK 21 del sistema operativo ni ejecutar Gradle desde una terminal externa desvinculada de Android Studio, ya que genera incompatibilidad con el Gradle Wrapper 8.0.
- **Emulador / Dispositivo:** AVD `Pixel_6_API_33` (Android 13.0 - Tiramisu, API Level 33, arquitectura x86_64).
- **Credenciales de Prueba (`secrets.properties`):** Para los tests instrumentados de UI que interactúan con el backend real, asegurarse de contar con el archivo `secrets.properties` en la raíz de la app (basado en `secrets.properties.example`):

  ```properties
  TEST_USER_EMAIL="admin@todostock.com"
  TEST_USER_PASSWORD="tu_password_segura"
  ```

---

#### 🧪 1. Pruebas Unitarias Locales e Integración (`testDebugUnitTest`)

Se ejecutan sobre la JVM local de desarrollo sin requerir emulador ni conexión de red, cubriendo validaciones de reglas de negocio (`ValidadoresTest`), ViewModels (`LoginViewModelTest`), lógica de stock (`StockItemTest`) y simulación de respuestas HTTP 200 y 500 con MockWebServer (`ParseoRespuestasTest`, `AuthRepositoryTest`).

- **Comando por terminal (dentro de Android Studio Giraffe):**

  ```powershell
  ./gradlew testDebugUnitTest
  ```

  *(o desde el árbol de proyecto: click derecho sobre la carpeta `app/src/test/java` → **Run 'Tests in todostock'**).*
- **Ubicación del Reporte HTML generado:**

  ```text
  app/build/reports/tests/testDebugUnitTest/index.html
  ```

---

#### 🤖 2. Pruebas Instrumentadas de UI con Espresso (`connectedDebugAndroidTest`)

Ejecutan el flujo crítico de inicio de sesión (`LoginFlujoTest`) sobre el emulador Android, utilizando el patrón **Screen Object** (`LoginScreen`, `MenuPrincipalScreen`), sincronización determinística con `IdlingResource` (sin `Thread.sleep()`) y verificación automática de accesibilidad en cada acción (`AccessibilityChecks.enable()`).

1. Iniciar previamente el emulador `Pixel_6_API_33` desde el Device Manager de Android Studio.
2. Ejecutar la suite instrumentada:

   ```powershell
   ./gradlew connectedDebugAndroidTest
   ```

   *(o click derecho sobre `LoginFlujoTest` en `app/src/androidTest/java` → **Run 'LoginFlujoTest'**).*

- **Ubicación del Reporte HTML generado:**

  ```text
  app/build/reports/androidTests/connected/index.html
  ```

---

#### 🌐 3. Pruebas de Contratos de API con Newman / Postman (Headless)

Ejecución de colecciones de integración sobre el catálogo de productos contra el backend de producción:

```bash
newman run tests/api/productos.postman_collection.json -e tests/api/alwaysdata.postman_environment.json
```

---

#### 4. Pruebas Automatizadas de Backend Django REST (`lib/Backend/`)

```bash
python manage.py test usuarios inventario
```

---

## 📋 2. Criterios de Ejecución y Asignaciones (Sprint 2 & Sprint 1)

### Aclaración sobre la ejecución de los casos de prueba (Hoja [Aclaraciones](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=2099542543#gid=2099542543))

Algunos casos del Sprint 1 y del Sprint 2 fueron ejecutados por la misma persona que desarrolló la funcionalidad, por falta de tiempo. Cada uno lo indica en la columna Comentarios de la planilla de test cases.

Casos alcanzados: TC-L-03, TC-L-04 (Login), TC-SSUC-01, TC-SSUC-02 (Selección de sucursal), TC-MOV-001 a TC-MOV-007 (Movimientos), TC-CP-01 (Carga de producto), TC-C-01 (Contacto), TC-STK-001 (Stock), TC-REG-EDAD-01, TC-REG-EDAD-02 (Registro) y TC-SEG-05 (Seguridad). El bug #183 (TC-MOV-007) fue corregido y cerrado por la misma persona que lo reportó.

Para el resto del Sprint 2, las ejecuciones (TK85 a TK89) se reasignaron para que nadie ejecute casos de una funcionalidad que programó. Los casos nuevos del Registro (TC-REG-01 a 04) los diseñan y ejecutan personas distintas de quien lo programó.

Los tests automatizados (`ValidadoresTest`, `StockItemTest`, `LoginViewModelTest`, `ParseoRespuestasTest`, `AuthRepositoryTest`) los escribe y ejecuta quien desarrolló el código, como es habitual en las pruebas unitarias.

---

## 3. Estrategia de Testing (Shift-Left) y Definition of Done (DoD)

Se aplican pruebas desde las fases tempranas del desarrollo para reducir costes de corrección.

### ✅ Definition of Done (DoD)

- [ ] Código sin errores de lint.
- [ ] Revisión por pares (Code Review) aprobada.
- [ ] Pasa los casos de prueba definidos.
- [ ] Sin secretos expuestos (Security Audit).

### Plantilla para Reporte de Defectos (#BUG)

```markdown
**Título:** #BUG[XX] - [Pantalla] Descripción breve
**Severidad:** Crítica / Alta / Media / Baja
**Pasos:**
1. Abrir app.
2. Ir a [Pantalla].
3. Realizar [Acción].
**Resultado Esperado:** ...
**Resultado Obtenido:** ...
**Evidencia:** ![Imagen](URL_AQUÍ)
```

---

## 📜 4. Casos de Prueba Manuales e Históricos del Sprint 1 (#TK30)

*Total: 12 casos (2 por integrante).*

### TC-L-01: Validación de campos obligatorios vacíos en Login

- **ID #:** TC-L-01
- **Hoja Externa:** [Ver Hoja Login](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=1952750266#gid=1952750266)
- **Título:** Validación de campos obligatorios vacíos en Login
- **Descripción:** Verificar que al pulsar "Ingresar" con campos vacíos el sistema bloquee el avance y alerte al usuario.
- **Precondiciones:** App instalada y abierta en LoginActivity.
- **Datos de Prueba:** `etUsuario: ""` | `etPassword: ""` (ambos vacíos)
- **Pasos:**
  1. Dejar ambos campos vacíos.
  2. Presionar el botón "Ingresar".
- **Resultado Esperado:** La app no navega. Muestra Toast de advertencia y resalta en texto rojo el error en los campos indicando que son obligatorios.
- **Status:** PASS
- **Prioridad:** Alta
- **#Etiquetas:** #Funcionales #UI #Validacion
- **Evidencia:**
  <details>
    <summary>📷 Ver captura del emulador</summary>
    <br>
    <img width="225" height="465" alt="image" src="https://github.com/user-attachments/assets/440bd652-a63a-42bc-ae8c-361f6da743ee" />
  </details>
- **Diseñado por:** Miguel Flores
- **Ejecutado por:** Virginia García
- **Fecha de Ejecución:** 14/09/2026
- **Resultado Obtenido:** Bloquea el avance y emite Toast correctamente.
- **Comentarios:** Observación / Bug asociado: Bug-Log-01.

---

### TC-L-02: Navegación exitosa desde Login hacia Menú Principal con datos completos (TK01 / TK03)

- **ID #:** TC-L-02

- **Hoja Externa:** [Ver Hoja Login](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=1952750266#gid=1952750266)
- **Título:** Navegación exitosa desde Login hacia Menú Principal con datos completos (TK01 / TK03)
- **Descripción:** Verificar que al ingresar datos en ambos campos, se lance MenuPrincipalActivity y se cierre el Login.
- **Precondiciones:** App abierta en LoginActivity.
- **Datos de Prueba:** `etUsuario: "admin@todostock.com"`, `contraseña: "12345"`
- **Pasos:**
  1. Ingresar texto en el campo Usuario.
  2. Ingresar texto en Contraseña.
  3. Presionar "INGRESAR".
- **Resultado Esperado:** Se realiza la transición de pantalla hacia MenuPrincipalActivity.
- **Status:** PASS
- **Prioridad:** Crítica
- **#Etiquetas:** #Smoke #Funcionales #Navegacion
- **Evidencia:**
    <details>
    <summary>Ver captura del emulador</summary>
    <br>
    <img width="215" height="450" alt="test2" src="https://github.com/user-attachments/assets/b4e33ae3-b5c4-40c4-941f-651362df0bbb" />
  </details>  
- **Diseñado por:** Miguel Flores
- **Ejecutado por:** Virginia García
- **Fecha de Ejecución:** 14/09/2026

---

### TC-L-03: Persistencia de estado y prevención de re-login al presionar "Atrás"

- **ID #:** TC-L-03

- **Hoja Externa:** [Ver Hoja Login](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=1952750266#gid=1952750266)
- **Título:** Persistencia de estado y prevención de re-login al presionar "Atrás"
- **Descripción:** Verificar que al presionar el botón "Atrás" desde el Menú Principal, la aplicación se minimice o cierre la sesión correctamente.
- **Precondiciones:** Usuario autenticado y posicionado en `MenuPrincipalActivity`.
- **Pasos:**
  1. Iniciar sesión exitosamente.
  2. Presionar el botón físico/gesto "Atrás" de Android.
- **Resultado Esperado:** La `LoginActivity` debe haber sido finalizada (`finish()`).
- **Status:** PASS
- **Prioridad:** Alta
- **#Etiquetas:** #Navegacion #CicloDeVida #Seguridad #TK03
- **Diseñado por:** Octavio Arnaudo
- **Ejecutado por:** Miguel Flores

---

### TC-L-04: Validación de robustez y condiciones mínimas de contraseña (#TK31)

- **ID #:** TC-L-04

- **Hoja Externa:** [Ver Hoja Login](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=1952750266#gid=1952750266)
- **Título:** Validación de robustez y condiciones mínimas de contraseña (#TK31)
- **Descripción:** Evaluar el comportamiento del sistema ante el ingreso de contraseñas cortas.
- **Precondiciones:** App abierta en `LoginActivity`.
- **Status:** FAIL (Sprint 1) / FIXED (Sprint 2 - PR #171)
- **Prioridad:** Crítica
- **#Etiquetas:** #Seguridad #Ciberseguridad #Bug #Validacion #TK31
- **Diseñado por:** Octavio Arnaudo
- **Ejecutado por:** Miguel Flores

---

### TC-STK-01: Visualización de lista de productos con stock destacando ítems por debajo del umbral mínimo

- **ID #:** TC-STK-01

- **Hoja Externa:** [Ver Hoja Stock](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=237179274#gid=237179274)
- **Título:** Visualización de lista de productos con stock destacando ítems por debajo del umbral mínimo
- **Status:** PASS
- **Prioridad:** Alta
- **#Etiquetas:** #Funcionales #UI #Stock #TK13 #TK14
- **Diseñado por:** Virginia Garcia
- **Ejecutado por:** Miguel Flores

---

### TC-STK-02: Filtrado de lista de stock al recibir `sucursal_id` distinto desde US02

- **ID #:** TC-STK-02

- **Hoja Externa:** [Ver Hoja Stock](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=237179274#gid=237179274)
- **Título:** Filtrado de lista de stock al recibir `sucursal_id` distinto desde US02
- **Status:** PASS
- **Prioridad:** Media
- **#Etiquetas:** #Funcionales #Integracion #Stock #TK15
- **Diseñado por:** Virginia Garcia
- **Ejecutado por:** Miguel Flores

---

### TC-SSUC-01 & TC-SSUC-02 - Selección de Sucursales

- **Diseñados por:** Candelaria Barjollo  

- **Referencia Externa:** [Ver Hoja Selección de Sucursal](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=1797528588#gid=1797528588)

---

### TC-MOV-01 a TC-MOV-07 - Movimientos entre Sucursales

- **Diseñados por:** Aylén Bartolino Luna  

- **Referencia Externa:** [Ver Hoja Movimientos](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=1940159724#gid=1940159724)

---

### TC-CP-01 - Carga de Producto

- **Diseñados por:** Pedro González  

- **Referencia Externa:** [Ver Hoja Carga de Producto](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=832156876#gid=832156876)

---

### TC-C-01 - Contacto y Soporte

- **Diseñados por:** Pedro González  

- **Referencia Externa:** [Ver Hoja Contacto](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=2053023542#gid=2053023542)

---

### TC-SEG-01 a TC-SEG-06- Seguridad & Hardening (#RNF-SEG)

- **Desarrollados y Diseñados por:** Aylén Bartolino Luna / Pedro González / Octavio Arnaudo

- **Referencia Externa:** [Ver Hoja Seguridad](https://docs.google.com/spreadsheets/d/1W3yR2TZJiWFqeGJ7NlSx_uIQXClxOI8k/edit?gid=2083563036#gid=2083563036)

#### TC-SEG-04: Fuerza bruta — bloqueo temporal del login y registro en auditoría

- **ID #:** TC-SEG-04

- **HU / Tareas:** RNF-SEG (#68) - US14, TK58 (depende de TK46 y TK51)
- **Título:** Fuerza bruta: bloqueo temporal del login y registro en auditoría
- **Descripción:** Verificar que, después de 5 intentos de login en el mismo minuto, el siguiente se rechaza aunque la contraseña sea correcta, que se puede volver a entrar pasado el minuto y que el bloqueo queda registrado en la auditoría.
- **Política de Seguridad:** Protección contra fuerza bruta (límite de intentos de login - TK46, PR #307)
- **Frecuencia:** En cada release
- **Precondiciones:**
  1. App instalada desde develop.
  2. Backend en Alwaysdata con límite de login (TK46, PR #307) y auditoría (TK51) desplegados.
  3. Cuenta de prueba activa (`test_pedro1@test.com`).
  4. Esperar 1 minuto sin intentos antes de empezar.
  5. Cuenta de Administrador con acceso al panel de Django.
- **Datos de Prueba:** Cuenta `test_pedro1@test.com` | Contraseña incorrecta cualquiera
- **Pasos:**
  1. Abrir la app en Login.
  2. Ingresar el email de la cuenta de prueba con contraseña incorrecta y tocar Ingresar; repetir hasta completar 5 intentos dentro del mismo minuto.
  3. Antes de que pase el minuto, ingresar la contraseña correcta.
  4. Esperar 60 segundos e ingresar de nuevo con la contraseña correcta.
  5. En `https://todostock.alwaysdata.net/admin/` (Administrador) abrir Usuarios > Logs de Auditoría y buscar los eventos.
- **Resultado Esperado:**
  1-2. Los 5 intentos muestran "Email o contraseña incorrectos."
  3. El login se rechaza aunque la contraseña sea correcta: la app muestra "No se pudo iniciar sesión. Intentá de nuevo más tarde."
  4. Pasado el minuto, el login es exitoso y entra al Menú Principal.
  5. La auditoría tiene 5 registros "Intento de inicio de sesión fallido", 1 "Bloqueo por exceso de intentos" y 1 "Inicio de sesión exitoso".
- **Status:** PASS
- **Prioridad:** Alta
- **#Etiquetas:** #Seguridad #FuerzaBruta #Auditoría #Login #CajaNegra #Negativa
- **Evidencias:** `tk58_django auditoria`, `tk58_login correcto`, `tk58_paso2 intentos fallidos`, `tk58_paso3 bloqueo`
- **Diseñado por:** Pedro González
- **Ejecutado por:** Pedro González
- **Fecha de Ejecución:** 12/10/2026
- **Resultado Obtenido:** Los 5 intentos con contraseña incorrecta mostraron "Email o contraseña incorrectos." (20:19). El intento siguiente con contraseña correcta se rechazó con "No se pudo iniciar sesión. Intentá de nuevo más tarde.". A las 20:20, pasado el minuto, el login fue exitoso. En Logs de Auditoría quedaron los 5 "Intento de inicio de sesión fallido", el "Bloqueo por exceso de intentos" y el "Inicio de sesión exitoso".
- **Comentarios:** El límite cuenta todos los pedidos de login por IP en el último minuto. La auditoría registra un "Bloqueo por exceso de intentos" por cada intento rechazado. Ante el bloqueo, la app muestra el mensaje genérico de error.

---

#### TC-SEG-05: Legislación y auditoría — aceptación de términos y baja de cuenta

- **ID #:** TC-SEG-05

- **HU / Tareas:** US07 (#61) - Registro, US11 (#67) - Términos y baja, RNF-SEG (#68) - TK59 (depende de TK52 y TK51)
- **Título:** Legislación y auditoría: aceptación de términos y baja de cuenta
- **Descripción:** Verificar que sin aceptar los términos no se registra la cuenta y que, al darse de baja, la cuenta no puede volver a entrar y queda un registro de la baja en la auditoría.
- **Política de Seguridad:** Legislación y auditoría (datos personales, Ley 25.326 y aceptación de términos)
- **Frecuencia:** En cada release
- **Precondiciones:**
  1. App instalada desde develop.
  2. Backend desplegado (`https://todostock.alwaysdata.net/`, producción).
  3. Cuentas de prueba activas para la baja (`test_aylen4@test.com`, `test_aylen6@test.com`).
  4. Auditoría de la TK51 disponible.
- **Datos de Prueba:** Parte 1: Datos de registro válidos, casilla de términos sin marcar. Parte 2 y 3: Cuenta de prueba `test_aylen6@test.com`.
- **Pasos:**
  - **Parte 1:** 1- Abrir Registro. 2- Completar datos válidos sin marcar casilla de términos. 3- Tocar Registrarme.
  - **Parte 2:** 4- Iniciar sesión con la cuenta de prueba. 5- En el Menú Principal tocar 'Darme de baja' y confirmar. 6- Intentar iniciar sesión de nuevo con la misma cuenta.
  - **Parte 3:** 7- Consultar la auditoría (un Administrador) y buscar el evento de baja de la cuenta.
- **Resultado Esperado:**
  1. No se envía el registro y la casilla se marca con error.
  2. Después de la baja, el login responde HTTP 403 y la app muestra "Esta cuenta no está activa".
  3. La auditoría tiene un registro de la baja con fecha, usuario y evento.
- **Status:** PASS (Re-ejecutado tras despliegue de TK51)
- **Prioridad:** Alta
- **#Etiquetas:** #Seguridad #Legislación #Auditoría #Registro #CajaNegra #Funcionales #Negativa
- **Evidencias:** `tk52_registro bloqueado por no aceptar terminos.jpeg`, `baja_queres baja.jpeg`, `Baja_procesando baja.jpeg`, `baja_toast baja.jpeg`, `tk52_cuenta no activa.jpeg`, `tk59_paso 1 confirmacion.jpeg`, `tk59_paso2.jpeg`, `tk59_paso 3 intento de relogueo.jpeg`, `tk59_django baja.JPG`
- **Diseñado por:** Aylén Bartolino Luna
- **Ejecutado por:** Aylén Bartolino Luna
- **Fecha de Ejecución:** 11/10/2026
- **Resultado Obtenido:** La baja se confirmó a las 16:05 y el login posterior mostró "Esta cuenta no está activa". En Logs de Auditoría quedó el registro "Baja voluntaria de cuenta" de `test_aylen6@test.com` a las 16:05, con fecha, usuario e IP de origen.
- **Comentarios:** Re-ejecución de la Parte 3 completada tras despliegue de la TK51. La auditoría también registró el intento de login posterior como "Intento de inicio de sesión fallido".

---

#### TC-SEG-06: Almacenamiento seguro de sesión — encriptación de Tokens JWT en EncryptedSharedPreferences (`SesionManager`)

- **ID #:** TC-SEG-06

- **HU y TK que cubre:** RNF-SEG (#68) - US14. Criterio: los tokens JWT y datos de sesión se almacenan cifrados localmente en memoria con EncryptedSharedPreferences y no en texto plano. TK49.
- **Título:** Almacenamiento seguro de sesión: encriptación de Tokens JWT en EncryptedSharedPreferences
- **Descripción:** Verificar que los tokens JWT de acceso y refresco (`token_access`, `token_refresh`) y la información del usuario autenticado se almacenen de forma segura y encriptada en la memoria local mediante la librería `EncryptedSharedPreferences` (`SesionManager`), impidiendo la lectura en texto plano desde el sistema de archivos del dispositivo.
- **Política de Seguridad:** Cifrado local de credenciales y tokens JWT (#TK49 - Ciberseguridad / Privacy by Design)
- **Frecuencia:** En cada release
- **Precondiciones:**
  1) App instalada en emulador o dispositivo físico Android con depuración USB / ADB activa.
  2) Usuario registrado e iniciado sesión correctamente desde `LoginActivity`.
  3) Acceso root / ADB Shell para inspeccionar el directorio de datos internos `/data/data/com.ispc.todostock/shared_prefs/`.
- **Datos de Prueba:** Credenciales de usuario válidas | Archivo `sesion_todostock.xml`
- **Pasos:**
  1- Iniciar sesión exitosamente en la aplicación con una cuenta activa.
  2- Abrir una terminal ADB o inspeccionar el almacenamiento privado con Device File Explorer de Android Studio.
  3- Navegar hasta `/data/data/com.ispc.todostock/shared_prefs/sesion_todostock.xml`.
  4- Abrir y examinar el contenido XML de las preferencias guardadas.
- **Resultado Esperado:** Todas las llaves y valores (incluyendo `token_access`, `token_refresh` y datos de usuario) están completamente cifradas mediante AES-256-GCM. Imposible recuperar o leer el token JWT en texto claro.
- **Status:** PASS
- **Prioridad:** Crítica
- **#Etiquetas:** #Seguridad #Cifrado #EncryptedSharedPreferences #JWT #CajaNegra #PruebaExitosa
- **Evidencias:** `tc_seg_06_encrypted_shared_prefs.png` (disponible en Drive / Anexo II)
- **Diseñado por:** Octavio Arnaudo
- **Ejecutado por:** Octavio Arnaudo
- **Fecha de Ejecución:** 12/10/2026
- **Resultado Obtenido:** Las llaves y valores dentro del archivo `sesion_todostock.xml` figuran 100% encriptados con cadenas alfanuméricas aleatorias inutilizables en texto plano.
- **Comentarios:** Prueba exitosa de la capa de persistencia segura local (#TK49). Sin defectos ni vulnerabilidades detectadas.

---
[Volver al Home](Home)
