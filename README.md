
# TodoStock App Móvil
 
Aplicación móvil (Android, Java) para la gestión de inventario en tiempo real, desarrollada como continuación del proyecto web **TodoStock** (Programador Web) en el marco de Proyecto Integrador II — TSDWAD, ISPC.
 
Permite a **Administradores** y **Vendedores** acceder desde el celular a las mismas funcionalidades de control de stock que la plataforma web, consumiendo las APIs REST ya desarrolladas en Django.
 
---
 
##  Sobre el proyecto
 
Muchas pymes controlan su inventario con planillas o procesos manuales, lo que genera falta de visibilidad, pérdida de ventas por quiebres de stock y desorganización entre sucursales. TodoStock App extiende la solución web a un dispositivo móvil para que el control de stock, productos, proveedores y movimientos se pueda hacer desde cualquier lugar.
 
### Funcionalidades principales (según rol)
 
| Rol | Puede hacer |
|---|---|
| **Administrador** | Gestionar productos y proveedores, ver catálogo consolidado, dar de alta usuarios, editar su perfil |
| **Vendedor** | Registrar entradas/salidas de stock, consultar productos, editar su perfil |
| **Ambos** | Iniciar sesión, ver su perfil, cerrar sesión, contactar a soporte |
 
 
##  Tecnologías
 
- **Frontend móvil:** Android nativo, Java
- **Backend:** Django + Django REST Framework (repo del proyecto web)
- **Base de datos:** *(completar: PostgreSQL / MySQL / la que use el backend web)*
- **Autenticación:** JWT
- **Requisitos mínimos:** Android 5.0 (API 21) o superior
---
 
##  Equipo
 
| Integrante | Rol en el proyecto |
|---|---|
| Candelaria | Desarrollo Full Stack  |
| Miguel | Desarrollo Full Stack |
| Pedro | Desarrollo Full Stack  |
| Virginia | Scrum Master Desarrollo Full Stack |
| Aylen | Desarrollo Full Stack  |
| Octavio | Documentación  |
 
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

 
## 🚀 Cómo correr el proyecto
 
1. Clonar el repositorio:
```bash
   git clone https://github.com/ISPC-WEB-2025/TodoStockApp.git
```
2. Abrir el proyecto en **Android Studio**.
3. Sincronizar Gradle.
4. Configurar la URL base de la API en *(completar: archivo de configuración/constants)*.
5. Ejecutar en un emulador (AVD, Android 5.0+) o en un dispositivo físico con depuración USB habilitada.
---
 
## 📋 Gestión del proyecto
 
- **Tablero Kanban:** *(link al Project de GitHub)*
- **Milestones / Sprints:** *(link a Milestones)*
- **Wiki del proyecto:** *(link a la Wiki: ceremonias, Testing, Ciberseguridad)*
- **Documentación IEEE830:** *(link a Google Drive)*
- **Diseño (Figma):** https://www.figma.com/design/Aa1IGF6PBIY5ID9SQHCCG4/StockSip---Mobile-Application--Community-
---
 
## 🔐 Seguridad
 
- Autenticación mediante **JWT**.
- Contraseñas con mínimo 8 caracteres, alfanuméricas y con caracteres especiales.
- Gestión de permisos según rol (Administrador / Vendedor).
- Control de sesiones y comunicación vía HTTPS (SSL/TLS).
- Detalle completo del plan de seguridad en la sección **Ciberseguridad** de la Wiki.
---
 
## 📄 Licencia
 
*Codelab*
