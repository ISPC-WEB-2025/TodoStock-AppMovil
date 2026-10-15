# Evidencia de Pruebas Automatizadas de UI y Accesibilidad — Flujo Login a Menú (TK70 & TK76)
## TodoStock Mobile — Suite Instrumentada con Espresso y Screen Objects

> **Documento de Evidencia:** `EVID-TK70-UI-01`  
> **Tareas Cubiertas:**
> - [#123 #TK70 [AUT-UI-01] Espresso: flujo Login → Menú con LoginScreen](https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/issues/123)
> - [#129 #TK76 [ACC-02] AccessibilityChecks.enable() en los tests Espresso](https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/issues/129)
> - [#207 [AUT-UI-01] Flujo crítico con Espresso en androidTest con ActivityScenarioRule](https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/issues/207)
> - [#208 [AUT-UI-02] Al menos un Screen Object en el test de Espresso](https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/issues/208)
> - [#209 [AUT-UI-03] Sin Thread.sleep() en los tests de Espresso](https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/issues/209)
> - [#210 [ACC-01] AccessibilityChecks.enable() en el test de Espresso](https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/issues/210)
>
> **Historia de Usuario:** US01 (Inicio de sesión contra API JWT y acceso al Menú Principal según rol)  
> **Casos de Prueba Trazados:** `TC-L-01` (Validación de campos vacíos en Login) y `TC-L-02` (Login exitoso hacia Menú Principal)  
> **Épica:** [#69 [ENABLER-QA] Suite de pruebas automatizadas, pipeline CI/CD e informe de accesibilidad](https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/issues/69)  
> **Responsable:** Miguel Flores (@mikefink22)  
> **Fecha de Ejecución:** 13/10/2026  
> **Entorno de Ejecución:** AVD `Pixel_6_API_33` (Android 13.0, API Level 33) conectado a Backend Alwaysdata (`https://todostock.alwaysdata.net/api/`)  
> **Herramientas:** AndroidX Test (`ActivityScenarioRule`), Espresso Core `3.5.1`, Espresso Accessibility `3.5.1`, `IdlingRegistry` / `IdlingResource`  

---

## 1. Resumen Ejecutivo de la Ejecución

| Métrica | Valor Obtenido | Estado / Meta |
| :--- | :---: | :---: |
| **Tests Instrumentados Ejecutados** | 2 (`LoginFlujoTest`) | 100% planificados |
| **Resultado de Suite (`connectedDebugAndroidTest`)** | `2 passed, 0 failed` | **100% PASS** |
| **Tiempo Total de Suite en Emulador** | 12 s (Build & Run: 25 s) | Superado |
| **Chequeos Automáticos de Accesibilidad (`ACC-01`)** | Activos en cada `ViewAction` | `0 infracciones bloqueantes` |
| **Uso de `Thread.sleep()` (`AUT-UI-03`)** | `0 ocurrencias` | Sincronizado vía `IdlingResource` |
| **Exposición de Credenciales en Repositorio** | `0 credenciales expuestas` | Inyección desde `secrets.properties` |

---

## 2. Arquitectura Implementada: Patrón Screen Object (Page Object Model)

Siguiendo las pautas de diseño de pruebas de UI del Sprint 2 (`AUT-UI-02` / #208), se desacoplaron completamente los selectores de vistas (`R.id`) de la lógica de los tests:

| Clase | Ubicación | Responsabilidad |
| :--- | :--- | :--- |
| **`LoginScreen`** | `app/src/androidTest/java/com/ispc/todostock/LoginScreen.java` | Encapsula como atributos privados `R.id.etUsuario`, `R.id.etPassword`, `R.id.btnLogin` y `R.id.tvIrARegistro`. Expone métodos de negocio fluidos (`ingresarEmail()`, `ingresarPassword()`, `verificarVisible()`) y el método de transición `clickIngresar()`, que retorna una instancia de `MenuPrincipalScreen`. |
| **`MenuPrincipalScreen`** | `app/src/androidTest/java/com/ispc/todostock/MenuPrincipalScreen.java` | Encapsula como atributos privados `R.id.toolbarMenu`, `R.id.btnCerrarSesion` y `R.id.tvRolActivo`. Expone las aserciones de negocio `verificarVisible()` y `verificarSesionIniciada()`. |
| **`LoginFlujoTest`** | `app/src/androidTest/java/com/ispc/todostock/LoginFlujoTest.java` | Clase de prueba anotada con `@RunWith(AndroidJUnit4.class)` y `@LargeTest`. Levanta `LoginActivity` mediante `ActivityScenarioRule`, habilita `AccessibilityChecks.enable()` en `@BeforeClass` y ejecuta los casos bajo el patrón **AAA (`Arrange`, `Act`, `Assert`)** sin ninguna invocación directa a `onView` o `withId`. |

---

## 3. Matriz de Casos de Prueba Automatizados en `LoginFlujoTest`

| ID Caso | Método de Test (`condicion_resultadoEsperado`) | Historia / TC | Descripción del Flujo (AAA) | Duración | Resultado |
| :---: | :--- | :---: | :--- | :---: | :---: |
| **AUT-UI-01a** | `credencialesValidas_navegaAMenuPrincipal` | US01 / `TC-L-02` | **Arrange:** Limpia sesión previa, verifica `LoginScreen` visible y valida presencia de credenciales desde `BuildConfig`.<br>**Act:** Ingresa email y contraseña de prueba y presiona *INGRESAR*.<br>**Assert:** Sincroniza vía `IdlingResource` la respuesta HTTP 200 de Alwaysdata y comprueba `MenuPrincipalScreen` visible con sesión activa (`tvRolActivo`). | 8 s | **PASS** ✅ |
| **AUT-UI-01b** | `camposVacios_permaneceEnLogin` | US01 / `TC-L-01` | **Arrange:** Limpia sesión previa y verifica `LoginScreen` visible.<br>**Act:** Presiona *INGRESAR* con los campos vacíos.<br>**Assert:** Verifica que la navegación se bloquea localmente y el usuario permanece en `LoginScreen`. | 3 s | **PASS** ✅ |

---

## 4. Hallazgo Real Detectado Durante el Sprint y Solución Técnica (`AUT-UI-03`)

En cumplimiento del criterio de aceptación *"Evidencia de al menos un fallo real detectado en el sprint"* (#123 y #209), se documenta el defecto de sincronización asíncrona relevado durante la primera ejecución contra el backend real desplegado en Alwaysdata:

### 4.1 Síntoma y Evidencia del Fallo en `Logcat` / `TestRunner`
Al ejecutar inicialmente `credencialesValidas_navegaAMenuPrincipal` sin esperas fijas (`Thread.sleep()` prohibido por `AUT-UI-03`), el test falló con `NoMatchingViewException` al buscar `R.id.toolbarMenu`:

```text
10-13 05:22:35.754 E TestRunner: +------->AppCompatButton{id=2131230828, res-name=btnLogin, visibility=VISIBLE, is-enabled=false, text=INGRESANDO…}
10-13 05:22:35.754 E TestRunner: androidx.test.espresso.NoMatchingViewException: No views in hierarchy found matching: with id: com.ispc.todostock:id/toolbarMenu
    at com.ispc.todostock.MenuPrincipalScreen.verificarVisible(MenuPrincipalScreen.java:19)
    at com.ispc.todostock.LoginFlujoTest.credencialesValidas_navegaAMenuPrincipal(LoginFlujoTest.java:69)
```

Instantes después en el mismo `Logcat`, el servidor Alwaysdata respondió `HTTP 200 OK` (latencia registrada por `OkHttpClient`: `2508ms` a `3320ms`) y la actividad destino pasó a estado `RESUMED`:

```text
D LifecycleMonitor: Lifecycle status change: com.ispc.todostock.MenuPrincipalActivity@7ddd074 in: CREATED
D LifecycleMonitor: Lifecycle status change: com.ispc.todostock.MenuPrincipalActivity@7ddd074 in: STARTED
D LifecycleMonitor: Lifecycle status change: com.ispc.todostock.MenuPrincipalActivity@7ddd074 in: RESUMED
```

### 4.2 Causa Raíz Técnica
`LoginViewModel` ejecuta la petición `repositorio.login(...)` en un hilo secundario mediante `Executors.newSingleThreadExecutor()`. Por defecto, Espresso sincroniza el hilo principal (`Main Looper`) y los `AsyncTask` del SDK, pero **no conoce el estado de hilos creados en `ExecutorService` propios ni del pool de conexiones de `OkHttp`**. Como consecuencia, tras ejecutar `click()` sobre `btnLogin` (que pasa a estado deshabilitado con texto `"INGRESANDO…"`), Espresso evaluaba la aserción de `MenuPrincipalScreen` de forma inmediata mientras la petición HTTP seguía en vuelo hacia Alwaysdata.

### 4.3 Solución Implementada (Sin `Thread.sleep()`)
Se implementó y registró en `@Before` un **`IdlingResource`** (`LoginNetworkIdlingResource`) mediante `IdlingRegistry.getInstance().register(...)` y su desregistro en `@After`:
1. Reporta estado ocupado (`isIdleNow() == false`) mientras `LoginActivity` mantiene `btnLogin` deshabilitado esperando la respuesta HTTP del backend (`!btnLogin.isEnabled()`).
2. Mantiene la espera durante la transición de cierre de `LoginActivity` (`isFinishing()`) hasta que `ActivityLifecycleMonitorRegistry` confirma que `MenuPrincipalActivity` alcanzó el estado `Stage.RESUMED`.
3. Al alcanzar el estado inactivo, invoca `callback.onTransitionToIdle()`, permitiendo que Espresso continúe de manera 100% determinística y sin pausas artificiales (`Thread.sleep()`).

---

## 5. Auditoría Automática de Accesibilidad (`TK76` / `ACC-01`)

Mediante la invocación de `AccessibilityChecks.enable()` en el método `@BeforeClass` de `LoginFlujoTest`, el framework **Android Accessibility Test Framework (ATF)** auditó automáticamente cada vista interactuada (`etUsuario`, `etPassword`, `btnLogin`), validando:
- Tamaño mínimo de área táctil (`48dp × 48dp`).
- Relación de contraste de texto y fondo conforme a WCAG 2.0 AA (`> 4.5:1`).
- Presencia de etiquetas descriptivas (`hint` / `contentDescription`) y ausencia de solapamiento de íconos en `TextInputLayout` (verificando la corrección previa de `TK92`).

Registro en `Logcat` durante la ejecución:
```text
I ViewInteraction: Performing 'Running view assertions[Accessibility Checks, ] and then running: single click' action on view view.getId()
I ViewAssertion: Asserting Accessibility Checks
```

---

## 6. Seguridad y Gestión de Secretos en Pruebas

Para cumplir estrictamente con la política de seguridad del proyecto (cero credenciales hardcodeadas en el repositorio y cero falsos positivos):
1. Las credenciales del usuario de prueba se leen desde el archivo local `secrets.properties` (ignorado en `.gitignore`) a través de `BuildConfig.TEST_USER_EMAIL` y `BuildConfig.TEST_USER_PASSWORD`.
2. Se documentó la plantilla pública en `secrets.properties.example` y las instrucciones de configuración en `README.md`.
3. El test valida al inicio mediante `assertTrue(...)` que las credenciales estén configuradas antes de ejecutar el flujo contra el servidor, evitando falsos positivos si el entorno local no fue configurado.
