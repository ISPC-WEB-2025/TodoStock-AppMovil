package com.ispc.todostock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * [AUT-UNIT] - Tests unitarios de la lógica de baja lógica e inactividad de productos.
 *
 * Sigue el patrón AAA (Arrange, Act, Assert) y verifica el comportamiento de
 * detección y formateo de la marca [INACTIVO].
 */
public class BajaDeProductoTest {

    @Test
    public void estaInactivo_conPrefijoInactivo_retornaTrue() {
        // Arrange
        String nombre = "[INACTIVO] Tornillo Hexagonal";

        // Act
        boolean inactivo = BajaDeProducto.estaInactivo(nombre);

        // Assert
        assertTrue(inactivo);
    }

    @Test
    public void estaInactivo_sinPrefijoInactivo_retornaFalse() {
        // Arrange
        String nombre = "Tornillo Hexagonal";

        // Act
        boolean inactivo = BajaDeProducto.estaInactivo(nombre);

        // Assert
        assertFalse(inactivo);
    }

    @Test
    public void estaInactivo_nombreNulo_retornaFalse() {
        // Arrange
        String nombre = null;

        // Act
        boolean inactivo = BajaDeProducto.estaInactivo(nombre);

        // Assert
        assertFalse(inactivo);
    }

    @Test
    public void nombreSinMarca_conPrefijoInactivo_retornaNombreLimpio() {
        // Arrange
        String nombre = "[INACTIVO] Tornillo Hexagonal";

        // Act
        String limpio = BajaDeProducto.nombreSinMarca(nombre);

        // Assert
        assertEquals("Tornillo Hexagonal", limpio);
    }

    @Test
    public void marcarInactivo_productoActivo_agregaPrefijo() {
        // Arrange
        String nombre = "Tornillo Hexagonal";

        // Act
        String marcado = BajaDeProducto.marcarInactivo(nombre);

        // Assert
        assertEquals("[INACTIVO] Tornillo Hexagonal", marcado);
        assertTrue(BajaDeProducto.estaInactivo(marcado));
    }
}
