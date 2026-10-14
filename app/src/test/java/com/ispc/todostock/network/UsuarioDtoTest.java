package com.ispc.todostock.network;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests unitarios locales (JVM) para clasificación de estados de UsuarioDto.
 *
 * Valida la diferenciación de estados: activo, pendiente de aprobación y deshabilitado.
 * Estructurado bajo el patrón AAA (Arrange, Act, Assert).
 */
public class UsuarioDtoTest {

    @Test
    public void usuarioNuevoSinRolEInactivo_esPendienteTrue() {
        // Arrange
        UsuarioDto usuario = new UsuarioDto();
        usuario.isActive = false;
        usuario.rol = null;

        // Act & Assert
        assertTrue(usuario.esPendiente());
        assertFalse(usuario.esActivo());
        assertFalse(usuario.esDeshabilitado());
    }

    @Test
    public void usuarioConRolYActivo_esActivoTrue() {
        // Arrange
        UsuarioDto usuario = new UsuarioDto();
        usuario.isActive = true;
        usuario.rol = new RolDto(2, "EMPLEADO", "Rol operativo");

        // Act & Assert
        assertTrue(usuario.esActivo());
        assertFalse(usuario.esPendiente());
        assertFalse(usuario.esDeshabilitado());
    }

    @Test
    public void usuarioDadoDeBajaConRolEInactivo_esDeshabilitadoTrueYNoPendiente() {
        // Arrange: un usuario que fue dado de baja (baja lógica en backend)
        // conserva su rol previo en la base de datos pero tiene isActive = false.
        UsuarioDto usuario = new UsuarioDto();
        usuario.isActive = false;
        usuario.rol = new RolDto(2, "EMPLEADO", "Rol operativo");

        // Act & Assert
        assertTrue(usuario.esDeshabilitado());
        assertFalse(usuario.esPendiente());
        assertFalse(usuario.esActivo());
    }
}
