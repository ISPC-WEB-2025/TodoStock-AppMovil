package com.ispc.todostock.sesion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Tests unitarios locales (JVM) para verificación de expiración de token en SesionManager.
 *
 * Sigue el patrón AAA (Arrange, Act, Assert).
 */
public class SesionManagerTest {

    private String crearJwtDummy(long expEnSegundos) {
        String header = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(("{\"exp\":" + expEnSegundos + ",\"user_id\":1}").getBytes(StandardCharsets.UTF_8));
        return header + "." + payload + ".dummy_signature";
    }

    @Test
    public void tokenConExpiracionFutura_noEstaExpirado() {
        // Arrange
        long futuro = (System.currentTimeMillis() / 1000) + 3600; // 1 hora en el futuro
        String jwt = crearJwtDummy(futuro);

        // Act
        boolean expirado = SesionManager.isTokenExpirado(jwt);

        // Assert
        assertFalse(expirado);
    }

    @Test
    public void tokenConExpiracionPasada_estaExpirado() {
        // Arrange
        long pasado = (System.currentTimeMillis() / 1000) - 3600; // 1 hora en el pasado
        String jwt = crearJwtDummy(pasado);

        // Act
        boolean expirado = SesionManager.isTokenExpirado(jwt);

        // Assert
        assertTrue(expirado);
    }

    @Test
    public void tokenNuloOInvalido_estaExpirado() {
        // Arrange & Act & Assert
        assertTrue(SesionManager.isTokenExpirado(null));
        assertTrue(SesionManager.isTokenExpirado(""));
        assertTrue(SesionManager.isTokenExpirado("token_invalido_sin_puntos"));
    }
}
