package com.ispc.todostock;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MascaraFechaWatcherTest {

    @Test
    public void unDigito_retornaMismoDigito() {
        // Arrange
        String actual = "1";
        String previo = "";

        // Act
        String resultado = MascaraFechaWatcher.formatear(actual, previo);

        // Assert
        assertEquals("1", resultado);
    }

    @Test
    public void dosDigitos_agregaPrimeraBarra() {
        // Arrange
        String actual = "15";
        String previo = "1";

        // Act
        String resultado = MascaraFechaWatcher.formatear(actual, previo);

        // Assert
        assertEquals("15/", resultado);
    }

    @Test
    public void tresDigitos_mantienePrimeraBarra() {
        // Arrange
        String actual = "150";
        String previo = "15/";

        // Act
        String resultado = MascaraFechaWatcher.formatear(actual, previo);

        // Assert
        assertEquals("15/0", resultado);
    }

    @Test
    public void cuatroDigitos_agregaSegundaBarra() {
        // Arrange
        String actual = "1508";
        String previo = "150";

        // Act
        String resultado = MascaraFechaWatcher.formatear(actual, previo);

        // Assert
        assertEquals("15/08/", resultado);
    }

    @Test
    public void ochoDigitos_completaFormatoDiezCaracteres() {
        // Arrange
        String actual = "15081995";
        String previo = "15/08/199";

        // Act
        String resultado = MascaraFechaWatcher.formatear(actual, previo);

        // Assert
        assertEquals("15/08/1995", resultado);
    }

    @Test
    public void masDeOchoDigitos_truncaEnDiezCaracteres() {
        // Arrange
        String actual = "150819959999";
        String previo = "15/08/1995";

        // Act
        String resultado = MascaraFechaWatcher.formatear(actual, previo);

        // Assert
        assertEquals("15/08/1995", resultado);
    }

    @Test
    public void borradoSobrePrimeraBarra_eliminaDigitoPrevioSinTrabar() {
        // Arrange: el usuario pulsa backspace cuando estaba en "15/"
        String actual = "15";
        String previo = "15/";

        // Act
        String resultado = MascaraFechaWatcher.formatear(actual, previo);

        // Assert: se retrocede a "1" para no trabarse en la barra
        assertEquals("1", resultado);
    }

    @Test
    public void borradoSobreSegundaBarra_eliminaDigitoPrevioSinTrabar() {
        // Arrange: el usuario pulsa backspace cuando estaba en "15/08/"
        String actual = "15/08";
        String previo = "15/08/";

        // Act
        String resultado = MascaraFechaWatcher.formatear(actual, previo);

        // Assert: se retrocede a "15/0"
        assertEquals("15/0", resultado);
    }

    @Test
    public void pegarTextoConGuiones_unificaFormatoConBarras() {
        // Arrange
        String actual = "15-08-1995";
        String previo = "";

        // Act
        String resultado = MascaraFechaWatcher.formatear(actual, previo);

        // Assert
        assertEquals("15/08/1995", resultado);
    }

    @Test
    public void textoNulo_retornaCadenaVacia() {
        // Arrange & Act
        String resultado = MascaraFechaWatcher.formatear(null, null);

        // Assert
        assertEquals("", resultado);
    }
}
