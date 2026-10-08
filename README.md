# TodoStock 
Aplicación móvil (Android, Java) para la gestión de inventario en tiempo real.

---

## 📌 Sobre el proyecto

Muchas pymes controlan su inventario con planillas o procesos manuales, lo que genera falta de visibilidad, pérdida de ventas por quiebres de stock y desorganización entre sucursales. TodoStock App extiende la solución web a un dispositivo móvil para que el control de stock, productos y movimientos entre sucursales se pueda realizar desde cualquier lugar de forma ágil y centralizada.

### Funcionalidades principales (según rol)

| Rol | Puede hacer |
|---|---|
| **Administrador** | Carga y gestión de productos, consulta de stock consolidado y por sucursal, registro de movimientos de stock entre sucursales, selección de sucursal activa, aprobación/asignación de rol a usuarios registrados y soporte |
| **Empleado** | Consulta de stock por sucursal, registro y consulta de movimientos de stock (entradas, salidas y traslados entre sucursales), selección de sucursal activa, edición de perfil y contacto a soporte |
| **Ambos** | Iniciar sesión (autenticación JWT), ver y seleccionar sucursal activa, ver y editar perfil, cerrar sesión y enviar mensajes a soporte |

#### 📝 Registro de Usuarios
Cualquier usuario puede solicitar el alta desde el formulario de registro ingresando **nombre, email, DNI, fecha de nacimiento y contraseña**. Tras registrarse, la cuenta queda en estado **pendiente** hasta que un **Administrador** le habilite el acceso y le asigne su rol correspondiente (Administrador o Empleado).

---

## 🛠️ Tecnologías

- **Frontend móvil:** Android nativo (Java, Android SDK, Material Design Components)
- **Backend:** Django + Django REST Framework (API REST)
- **Base de datos:** MySQL / MariaDB (gestionada a través del backend Django REST Framework)
- **Autenticación:** JWT (JSON Web Tokens vía SimpleJWT)
- **Requisitos mínimos:** Android 7.0 (API 24) o superior

---

## 👥 Equipo

| Integrante | Rol en el proyecto |
|---|---|
| Candelaria | Desarrollo Full Stack |
| Miguel | Desarrollo Full Stack |
| Pedro | Desarrollo Full Stack |
| Aylen | Scrum Master / Desarrollo Full Stack |
| Octavio | Documentación y QA |

---

## 🌿 Esquema de branching

Basado en Gitflow simplificado:

- `main` → versiones estables, listas para producción (cierre de cada Sprint)
- `develop` → integración de las features en desarrollo
- `release` → preparación de cada entrega/Sprint
- `feature/nombre-apellido` → rama individual de cada integrante

```
feature/candelaria-XXXXX  ─┐
feature/miguel-XXXXX      ─┤
feature/pedro-XXXXX       ─┼──▶ develop ──▶ release ──▶ main
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

## 📊 Gestión y Enlaces del Proyecto

- **API REST Backend (Desplegado):** https://todostock.alwaysdata.net/api/ *(mismo backend que utiliza la plataforma web TodoStock)*
- **Aplicación Web:** https://todo-stock.vercel.app/
- **Tablero Kanban:** https://github.com/orgs/ISPC-WEB-2025/projects/18
- **Milestones / Sprints:** https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/milestones
- **Wiki del proyecto:** https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/wiki
- **Diseño (Figma):** https://www.figma.com/design/Aa1IGF6PBIY5ID9SQHCCG4/StockSip---Mobile-Application--Community-

---

## 🔒 Seguridad

- Autenticación segura mediante **JWT** (Access y Refresh Tokens).
- **Regla de contraseña:** Mínimo **9 caracteres**, debiendo incluir letras, números y al menos **un carácter especial**.
- **Gestión de Registro y Permisos:** Los usuarios registrados ingresan en estado pendiente hasta la asignación de rol (**Administrador** / **Empleado**) por parte de un Administrador.
- Control de sesiones y comunicación encriptada vía **HTTPS (SSL/TLS)**.
- Detalle completo del plan de seguridad en la sección **Ciberseguridad** de la Wiki.

---

## 🧪 Testing

- **Casos de prueba (Test Cases):** https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/wiki/Testing-y-Calidad

---

## 📄 Licencia

*Codelab*
