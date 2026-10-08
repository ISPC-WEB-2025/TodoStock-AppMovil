package com.ispc.todostock.validacion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.ispc.todostock.validacion.Validadores.RequisitoPassword;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * TK60 [AUT-UNIT-01] - Tests unitarios de los validadores del Registro.
 *
 * Corren en la JVM local, sin emulador. Cada test sigue el patrón AAA
 * (Arrange, Act, Assert) y está diseñado con clases de equivalencia y
 * valores límite.
 *
 * Trazabilidad: historia US07 (Registro de usuario), tareas TK34 y TK47.
 */
public class ValidadoresTest {

    // ---------- Contraseña (TK47) ----------

    /** Valor límite: 8 caracteres se rechaza, 9 se acepta. */
    @Test
    public void password_valorLimiteDeLongitud_rechaza8YAcepta9() {
        // Arrange
        String ochoCaracteres = "Prueb12!";
        String nueveCaracteres = "Prueba12!";

        // Act
        Set<RequisitoPassword> faltantesCon8 = Validadores.requisitosFaltantes(ochoCaracteres);
        boolean validaCon9 = Validadores.passwordValida(nueveCaracteres);

        // Assert
        assertEquals(EnumSet.of(RequisitoPassword.LONGITUD), faltantesCon8);
        assertTrue(validaCon9);
    }

    /** Clases de equivalencia: a cada contraseña le falta exactamente un requisito. */
    @Test
    public void password_sinUnRequisito_informaCualFalta() {
        // Arrange
        String sinLetra = "123456789!";
        String sinNumero = "Pruebaaaa!";
        String sinEspecial = "Prueba1234";

        // Act
        Set<RequisitoPassword> faltaLetra = Validadores.requisitosFaltantes(sinLetra);
        Set<RequisitoPassword> faltaNumero = Validadores.requisitosFaltantes(sinNumero);
        Set<RequisitoPassword> faltaEspecial = Validadores.requisitosFaltantes(sinEspecial);

        // Assert
        assertEquals(EnumSet.of(RequisitoPassword.LETRA), faltaLetra);
        assertEquals(EnumSet.of(RequisitoPassword.NUMERO), faltaNumero);
        assertEquals(EnumSet.of(RequisitoPassword.ESPECIAL), faltaEspecial);
    }

    /** Un espacio no cuenta como carácter especial y además invalida la contraseña. */
    @Test
    public void password_conEspacios_seRechaza() {
        // Arrange
        String conEspacio = "Prueba 123!";

        // Act
        Set<RequisitoPassword> faltantes = Validadores.requisitosFaltantes(conEspacio);

        // Assert
        assertEquals(EnumSet.of(RequisitoPassword.SIN_ESPACIOS), faltantes);
        assertFalse(Validadores.passwordValida(conEspacio));
    }

    /** Vacía o nula: faltan los cuatro requisitos y no se rompe. */
    @Test
    public void password_vaciaONula_informaTodosLosRequisitos() {
        // Arrange
        Set<RequisitoPassword> esperados = EnumSet.of(
                RequisitoPassword.LONGITUD, RequisitoPassword.LETRA,
                RequisitoPassword.NUMERO, RequisitoPassword.ESPECIAL);

        // Act
        Set<RequisitoPassword> faltantesVacia = Validadores.requisitosFaltantes("");
        Set<RequisitoPassword> faltantesNula = Validadores.requisitosFaltantes(null);

        // Assert
        assertEquals(esperados, faltantesVacia);
        assertEquals(esperados, faltantesNula);
    }

    /** La confirmación debe ser idéntica: un espacio de más ya no coincide. */
    @Test
    public void passwordsCoinciden_soloSiSonIdenticas() {
        // Arrange
        String password = "Prueba123!";

        // Act y Assert
        assertTrue(Validadores.passwordsCoinciden(password, "Prueba123!"));
        assertFalse(Validadores.passwordsCoinciden(password, "Prueba123! "));
        assertFalse(Validadores.passwordsCoinciden(password, "prueba123!"));
        assertFalse(Validadores.passwordsCoinciden(null, null));
    }

    // ---------- DNI (TK34) ----------

    /** Valores límite: 6 y 9 dígitos se rechazan; 7 y 8 se aceptan. */
    @Test
    public void dni_valoresLimite_aceptaSolo7U8Digitos() {
        // Arrange
        String seis = "123456";
        String siete = "1234567";
        String ocho = "12345678";
        String nueve = "123456789";

        // Act y Assert
        assertFalse(Validadores.dniValido(seis));
        assertTrue(Validadores.dniValido(siete));
        assertTrue(Validadores.dniValido(ocho));
        assertFalse(Validadores.dniValido(nueve));
    }

    /** Clases inválidas: con puntos, con letras, vacío y nulo. */
    @Test
    public void dni_conFormatoInvalido_seRechaza() {
        assertFalse(Validadores.dniValido("30.111.222"));
        assertFalse(Validadores.dniValido("3011122A"));
        assertFalse(Validadores.dniValido(""));
        assertFalse(Validadores.dniValido(null));
    }

    // ---------- Email y nombre (TK34) ----------

    @Test
    public void email_aceptaFormatoValidoYRechazaElResto() {
        assertTrue(Validadores.emailValido("test_aylen1@test.com"));
        assertFalse(Validadores.emailValido("prueba@test"));      // sin dominio completo
        assertFalse(Validadores.emailValido("prueba.test.com"));  // sin arroba
        assertFalse(Validadores.emailValido("@test.com"));        // sin usuario
        assertFalse(Validadores.emailValido(""));
        assertFalse(Validadores.emailValido(null));
    }

    /** Valor límite: 5 caracteres se rechaza, 6 se acepta. Los espacios de los bordes no cuentan. */
    @Test
    public void nombre_valorLimite_rechaza5YAcepta6() {
        assertFalse(Validadores.nombreValido("Ana P"));
        assertTrue(Validadores.nombreValido("Ana Pa"));
        assertFalse(Validadores.nombreValido("   Ana   "));
        assertFalse(Validadores.nombreValido(null));
    }

    // ---------- Fecha de nacimiento (TK34) ----------

    /** Valor límite: hoy se acepta, mañana se rechaza. */
    @Test
    public void fechaNacimiento_valorLimite_aceptaHoyYRechazaManana() throws Exception {
        // Arrange: se fija "hoy" para que el test dé siempre el mismo resultado
        Date hoy = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse("07/10/2026");

        // Act y Assert
        assertTrue(Validadores.fechaNacimientoValida("10/05/1995", hoy));
        assertTrue(Validadores.fechaNacimientoValida("07/10/2026", hoy));
        assertFalse(Validadores.fechaNacimientoValida("08/10/2026", hoy));
    }

    /** Fechas inexistentes, mal escritas o vacías se rechazan. */
    @Test
    public void fechaNacimiento_inexistenteOMalFormada_seRechaza() throws Exception {
        // Arrange
        Date hoy = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse("07/10/2026");

        // Act y Assert
        assertFalse(Validadores.fechaNacimientoValida("31/02/2000", hoy));
        assertFalse(Validadores.fechaNacimientoValida("1/5/95", hoy));
        assertFalse(Validadores.fechaNacimientoValida("", hoy));
        assertFalse(Validadores.fechaNacimientoValida(null, hoy));
    }

    /** La fecha se convierte al formato que espera el backend (campo fdn). */
    @Test
    public void fechaAFormatoApi_convierteAlFormatoDelBackend() {
        assertEquals("1995-05-10", Validadores.fechaAFormatoApi("10/05/1995"));
        assertNull(Validadores.fechaAFormatoApi("31/02/2000"));
        assertNull(Validadores.fechaAFormatoApi(""));
    }


    // ---------- Movimientos (TK26 / TK27) ----------

    /** Valor límite: 0 se rechaza y 1 se acepta. Negativos, vacíos, texto y números fuera de rango se rechazan. */
    @Test
    public void cantidad_valorLimiteYFormatoInvalido_aceptaSoloEnterosMayoresACero() {
        assertFalse(Validadores.cantidadValida("0"));
        assertTrue(Validadores.cantidadValida("1"));
        assertFalse(Validadores.cantidadValida("-1"));
        assertFalse(Validadores.cantidadValida(""));
        assertFalse(Validadores.cantidadValida(null));
        assertFalse(Validadores.cantidadValida("abc"));
        assertFalse(Validadores.cantidadValida("99999999999"));
    }

    /** Clases de equivalencia: origen igual a destino se rechaza; distintos se acepta. */
    @Test
    public void origenYDestino_igualesSeRechazanYDistintosSeAceptan() {
        assertFalse(Validadores.origenYDestinoDistintos(1, 1));
        assertTrue(Validadores.origenYDestinoDistintos(1, 2));
    }
}
