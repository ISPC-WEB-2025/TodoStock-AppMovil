# TodoStock 
Aplicación móvil (Android, Java) para la gestión de inventario en tiempo real.

---

## 📌 Sobre el proyecto

Muchas pymes controlan su inventario con planillas o procesos manuales, lo que genera falta de visibilidad, pérdida de ventas por quiebres de stock y desorganización entre sucursales. TodoStock App extiende la solución web a un dispositivo móvil para que el control de stock, productos y movimientos entre sucursales se pueda realizar desde cualquier lugar de forma ágil y centralizada.

### Funcionalidades principales (según rol)

| Rol | Puede hacer |
|---|---|
| **Administrador** | Carga y gestión de productos, consulta de stock consolidado y por sucursal, registro de movimientos de stock entre sucursales, selección de sucursal activa, administración de usuarios/empleados (desde backend web/API) y consulta de soporte |
| **Empleado** | Consulta de stock por sucursal, registro y consulta de movimientos de stock (entradas, salidas y traslados entre sucursales), selección de sucursal activa, edición de perfil y contacto a soporte |
| **Ambos** | Iniciar sesión (autenticación JWT), ver y seleccionar sucursal activa, ver y editar perfil, cerrar sesión y enviar mensajes a soporte |

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
| Virginia | Scrum Master / Desarrollo Full Stack |
| Aylen | Desarrollo Full Stack |
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
feature/virginia-XXXXX    ─┤
feature/aylen-XXXXX       ─┘
```

---

## 🚀 Cómo correr el proyecto

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/ISPC-WEB-2025/TodoStock-AppMovil.git
   ```
2. Abrir el proyecto en **Android Studio**.
3. Sincronizar Gradle (`Sync Project with Gradle Files`).
4. Configurar la URL base de la API según tu entorno (ver sección de conexión a continuación).
5. Ejecutar en un emulador (AVD, Android 7.0+ / API 24+) o en un dispositivo físico con depuración USB habilitada.

---

## 🔌 Configuración de Conexión a la API (Backend)

El proyecto está configurado para conectarse al backend de forma dinámica, permitiendo que cada desarrollador use su propia IP local sin generar conflictos en Git.

**Si probás con el emulador de Android Studio:**

1. Levantá el backend en tu computadora:
   ```bash
   python manage.py runserver
   ```
2. No necesitás configurar nada más. Por defecto, la app se conecta a `http://10.0.2.2:8000/`, que es la dirección con la que el emulador llega a tu computadora.

**Si probás con un celular físico:**

1. Conectá el celular a la misma red Wi-Fi que tu computadora.
2. Levantá el backend aceptando conexiones de otros dispositivos de la red:
   ```bash
   python manage.py runserver 0.0.0.0:8000
   ```
3. Abrí la consola de Windows y ejecutá `ipconfig` para averiguar la IP de tu computadora (IPv4) en la red Wi-Fi.
4. Abrí el archivo `local.properties` (ubicado en la raíz del proyecto; no se sube al repositorio).
5. Agregá la siguiente línea al final del archivo, reemplazando con tu IP real:
   ```properties
   api.baseUrl=http://192.168.X.X:8000/
   ```
6. En Android Studio, sincronizá Gradle (Sync Now) y volvé a ejecutar la app.

**Si la app no logra conectarse:**

- Verificá que la URL termine con `/`.
- Si tu IP cambió (pasa al reconectarte al Wi-Fi), actualizala en `local.properties` y sincronizá de nuevo.
- Permití el acceso de Python en el Firewall de Windows cuando lo pregunte.
- Revisá que la IP de tu computadora esté permitida en `ALLOWED_HOSTS` del backend.

---

## 📊 Gestión del proyecto

- **Tablero Kanban:** https://github.com/orgs/ISPC-WEB-2025/projects/18
- **Milestones / Sprints:** https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/milestones
- **Wiki del proyecto:** https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/wiki
- **Diseño (Figma):** https://www.figma.com/design/Aa1IGF6PBIY5ID9SQHCCG4/StockSip---Mobile-Application--Community-

---

## 🔒 Seguridad

- Autenticación mediante **JWT** (Access y Refresh Tokens).
- Validación de credenciales procesada de forma segura por el servidor backend (Django REST Framework) con hashing de contraseñas.
- Gestión de permisos y accesos según rol (**Administrador** / **Empleado**).
- Control de sesiones y comunicación vía HTTPS (SSL/TLS).
- Detalle completo del plan de seguridad en la sección **Ciberseguridad** de la Wiki.

---

## 🧪 Testing

- **Casos de prueba (Test Cases):** https://github.com/ISPC-WEB-2025/TodoStock-AppMovil/wiki/Testing-y-Calidad

---

## 📄 Licencia

*Codelab*
