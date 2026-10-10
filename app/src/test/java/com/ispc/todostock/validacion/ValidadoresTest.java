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
 * (Arrange, Act, Assert, AUT-UNIT-04) y está diseñado con clases de
 * equivalencia y valores límite.
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

        // Act
        boolean identica = Validadores.passwordsCoinciden(password, "Prueba123!");
        boolean conEspacioDeMas = Validadores.passwordsCoinciden(password, "Prueba123! ");
        boolean conMayusculaDistinta = Validadores.passwordsCoinciden(password, "prueba123!");
        boolean ambasNulas = Validadores.passwordsCoinciden(null, null);

        // Assert
        assertTrue(identica);
        assertFalse(conEspacioDeMas);
        assertFalse(conMayusculaDistinta);
        assertFalse(ambasNulas);
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

        // Act
        boolean validoCon6 = Validadores.dniValido(seis);
        boolean validoCon7 = Validadores.dniValido(siete);
        boolean validoCon8 = Validadores.dniValido(ocho);
        boolean validoCon9 = Validadores.dniValido(nueve);

        // Assert
        assertFalse(validoCon6);
        assertTrue(validoCon7);
        assertTrue(validoCon8);
        assertFalse(validoCon9);
    }

    /** Clases inválidas: con puntos, con letras, vacío y nulo. */
    @Test
    public void dni_conFormatoInvalido_seRechaza() {
        // Arrange
        String conPuntos = "30.111.222";
        String conLetra = "3011122A";
        String vacio = "";

        // Act
        boolean validoConPuntos = Validadores.dniValido(conPuntos);
        boolean validoConLetra = Validadores.dniValido(conLetra);
        boolean validoVacio = Validadores.dniValido(vacio);
        boolean validoNulo = Validadores.dniValido(null);

        // Assert
        assertFalse(validoConPuntos);
        assertFalse(validoConLetra);
        assertFalse(validoVacio);
        assertFalse(validoNulo);
    }

    // ---------- Email y nombre (TK34) ----------

    @Test
    public void email_aceptaFormatoValidoYRechazaElResto() {
        // Arrange
        String valido = "test_aylen1@test.com";
        String sinDominioCompleto = "prueba@test";
        String sinArroba = "prueba.test.com";
        String sinUsuario = "@test.com";
        String vacio = "";

        // Act
        boolean aceptaValido = Validadores.emailValido(valido);
        boolean aceptaSinDominio = Validadores.emailValido(sinDominioCompleto);
        boolean aceptaSinArroba = Validadores.emailValido(sinArroba);
        boolean aceptaSinUsuario = Validadores.emailValido(sinUsuario);
        boolean aceptaVacio = Validadores.emailValido(vacio);
        boolean aceptaNulo = Validadores.emailValido(null);

        // Assert
        assertTrue(aceptaValido);
        assertFalse(aceptaSinDominio);
        assertFalse(aceptaSinArroba);
        assertFalse(aceptaSinUsuario);
        assertFalse(aceptaVacio);
        assertFalse(aceptaNulo);
    }

    /** Valor límite: 5 caracteres se rechaza, 6 se acepta. Los espacios de los bordes no cuentan. */
    @Test
    public void nombre_valorLimite_rechaza5YAcepta6() {
        // Arrange
        String cincoCaracteres = "Ana P";
        String seisCaracteres = "Ana Pa";
        String conEspaciosEnLosBordes = "   Ana   ";

        // Act
        boolean validoCon5 = Validadores.nombreValido(cincoCaracteres);
        boolean validoCon6 = Validadores.nombreValido(seisCaracteres);
        boolean validoConEspacios = Validadores.nombreValido(conEspaciosEnLosBordes);
        boolean validoNulo = Validadores.nombreValido(null);

        // Assert
        assertFalse(validoCon5);
        assertTrue(validoCon6);
        assertFalse(validoConEspacios);
        assertFalse(validoNulo);
    }

    // ---------- Fecha de nacimiento (TK34) ----------

    /** Valor límite: hoy se acepta, mañana se rechaza. */
    @Test
    public void fechaNacimiento_valorLimite_aceptaHoyYRechazaManana() throws Exception {
        // Arrange: se fija "hoy" para que el test dé siempre el mismo resultado
        Date hoy = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse("07/10/2026");

        // Act
        boolean validaEnElPasado = Validadores.fechaNacimientoValida("10/05/1995", hoy);
        boolean validaHoy = Validadores.fechaNacimientoValida("07/10/2026", hoy);
        boolean validaManana = Validadores.fechaNacimientoValida("08/10/2026", hoy);

        // Assert
        assertTrue(validaEnElPasado);
        assertTrue(validaHoy);
        assertFalse(validaManana);
    }

    /** Fechas inexistentes, mal escritas o vacías se rechazan. */
    @Test
    public void fechaNacimiento_inexistenteOMalFormada_seRechaza() throws Exception {
        // Arrange
        Date hoy = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse("07/10/2026");

        // Act
        boolean validaInexistente = Validadores.fechaNacimientoValida("31/02/2000", hoy);
        boolean validaMalFormada = Validadores.fechaNacimientoValida("1/5/95", hoy);
        boolean validaVacia = Validadores.fechaNacimientoValida("", hoy);
        boolean validaNula = Validadores.fechaNacimientoValida(null, hoy);

        // Assert
        assertFalse(validaInexistente);
        assertFalse(validaMalFormada);
        assertFalse(validaVacia);
        assertFalse(validaNula);
    }

    /** La fecha se convierte al formato que espera el backend (campo fdn). */
    @Test
    public void fechaAFormatoApi_convierteAlFormatoDelBackend() {
        // Arrange
        String fechaValida = "10/05/1995";
        String fechaInexistente = "31/02/2000";
        String vacia = "";

        // Act
        String convertida = Validadores.fechaAFormatoApi(fechaValida);
        String convertidaInexistente = Validadores.fechaAFormatoApi(fechaInexistente);
        String convertidaVacia = Validadores.fechaAFormatoApi(vacia);

        // Assert
        assertEquals("1995-05-10", convertida);
        assertNull(convertidaInexistente);
        assertNull(convertidaVacia);
    }


    // ---------- Movimientos (TK26 / TK27) ----------

    /** Valor límite: 0 se rechaza y 1 se acepta. Negativos, vacíos, texto y números fuera de rango se rechazan. */
    @Test
    public void cantidad_valorLimiteYFormatoInvalido_aceptaSoloEnterosMayoresACero() {
        // Arrange
        String cero = "0";
        String uno = "1";
        String negativo = "-1";
        String vacia = "";
        String texto = "abc";
        String fueraDeRango = "99999999999";

        // Act
        boolean validaCero = Validadores.cantidadValida(cero);
        boolean validaUno = Validadores.cantidadValida(uno);
        boolean validaNegativo = Validadores.cantidadValida(negativo);
        boolean validaVacia = Validadores.cantidadValida(vacia);
        boolean validaNula = Validadores.cantidadValida(null);
        boolean validaTexto = Validadores.cantidadValida(texto);
        boolean validaFueraDeRango = Validadores.cantidadValida(fueraDeRango);

        // Assert
        assertFalse(validaCero);
        assertTrue(validaUno);
        assertFalse(validaNegativo);
        assertFalse(validaVacia);
        assertFalse(validaNula);
        assertFalse(validaTexto);
        assertFalse(validaFueraDeRango);
    }

    /** Clases de equivalencia: origen igual a destino se rechaza; distintos se acepta. */
    @Test
    public void origenYDestino_igualesSeRechazanYDistintosSeAceptan() {
        // Arrange
        int sucursalA = 1;
        int sucursalB = 2;

        // Act
        boolean aceptaIguales = Validadores.origenYDestinoDistintos(sucursalA, sucursalA);
        boolean aceptaDistintas = Validadores.origenYDestinoDistintos(sucursalA, sucursalB);

        // Assert
        assertFalse(aceptaIguales);
        assertTrue(aceptaDistintas);
    }
}