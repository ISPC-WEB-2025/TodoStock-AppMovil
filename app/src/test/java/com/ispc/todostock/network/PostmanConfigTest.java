package com.ispc.todostock.network;

import org.junit.Assert;
import org.junit.Test;

public class PostmanConfigTest {

    @Test
    public void getApiKey_enCualquierEntorno_noLanzaExcepcion() {
        // Arrange & Act
        String apiKey = PostmanConfig.getApiKey();

        // Assert
        Assert.assertNotNull(apiKey);
    }

    @Test
    public void tieneApiKey_reflejaContenidoValido_sinFallar() {
        // Arrange
        String apiKey = PostmanConfig.getApiKey();

        // Act
        boolean tieneKey = PostmanConfig.tieneApiKey();

        // Assert
        if (apiKey.isEmpty()) {
            Assert.assertFalse(tieneKey);
        } else {
            Assert.assertTrue(tieneKey);
        }
    }
}
