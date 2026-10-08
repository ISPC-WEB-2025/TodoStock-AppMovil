package com.ispc.todostock.validacion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * TK47 / TK34 - Reglas de validación de los formularios de la app.
 *
 * Es Java puro (no usa clases de Android) para poder probarla con tests
 * unitarios locales (TK60) sin emulador.
 *
 * La regla de contraseña es la misma que aplica el backend al registrar
 * (mínimo 9, letra, número, carácter especial y sin espacios). Al 7/10/2026
 *  el backend no valida el formato del DNI (pendiente de revisión en el
 *  backend), así que la app lo valida antes de enviar.
 */
public final class Validadores {

    /** La consigna pide "más de 8" caracteres: mínimo 9. */
    public static final int PASSWORD_MIN = 9;

    /** Mismo mínimo que usa el formulario de registro de la web. */
    public static final int NOMBRE_MIN = 6;

    /** Formato en que la pantalla muestra la fecha. */
    public static final String FORMATO_FECHA_PANTALLA = "dd/MM/yyyy";

    /** Formato en que el backend espera la fecha (campo "fdn"). */
    public static final String FORMATO_FECHA_API = "yyyy-MM-dd";

    // Misma expresión que usa la web para el email.
    private static final Pattern EMAIL =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    // Misma regla que el modelo del backend: 7 u 8 dígitos.
    private static final Pattern DNI = Pattern.compile("^\\d{7,8}$");

    /** Lo que le puede faltar a una contraseña para ser robusta. */
    public enum RequisitoPassword { LONGITUD, LETRA, NUMERO, ESPECIAL, SIN_ESPACIOS }

    // Mismos caracteres especiales que acepta el backend (string.punctuation de Python).
    private static final String ESPECIALES = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    private Validadores() {
        // Clase utilitaria: no se instancia.
    }

    public static boolean nombreValido(String nombre) {
        return nombre != null && nombre.trim().length() >= NOMBRE_MIN;
    }

    public static boolean emailValido(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }

    public static boolean dniValido(String dni) {
        return dni != null && DNI.matcher(dni.trim()).matches();
    }

    /**
     * Devuelve los requisitos que la contraseña NO cumple.
     * Si el conjunto está vacío, la contraseña es robusta.
     */
    public static Set<RequisitoPassword> requisitosFaltantes(String password) {
        Set<RequisitoPassword> faltantes = EnumSet.noneOf(RequisitoPassword.class);
        String p = password == null ? "" : password;

        boolean tieneLetra = false;
        boolean tieneNumero = false;
        boolean tieneEspecial = false;
        boolean tieneEspacio = false;
        for (int i = 0; i < p.length(); i++) {
            char c = p.charAt(i);
            if (Character.isLetter(c)) {
                tieneLetra = true;
            } else if (Character.isDigit(c)) {
                tieneNumero = true;
            } else if (ESPECIALES.indexOf(c) >= 0) {
                tieneEspecial = true;
            } else if (Character.isWhitespace(c)) {
                tieneEspacio = true;
            }
        }

        if (p.length() < PASSWORD_MIN) faltantes.add(RequisitoPassword.LONGITUD);
        if (!tieneLetra) faltantes.add(RequisitoPassword.LETRA);
        if (!tieneNumero) faltantes.add(RequisitoPassword.NUMERO);
        if (!tieneEspecial) faltantes.add(RequisitoPassword.ESPECIAL);
        if (tieneEspacio) faltantes.add(RequisitoPassword.SIN_ESPACIOS);
        return faltantes;
    }

    public static boolean passwordValida(String password) {
        return requisitosFaltantes(password).isEmpty();
    }

    public static boolean passwordsCoinciden(String password, String confirmacion) {
        return password != null && password.equals(confirmacion);
    }

    /** La fecha (dd/MM/yyyy) existe y no es posterior a hoy. */
    public static boolean fechaNacimientoValida(String fechaPantalla) {
        return fechaNacimientoValida(fechaPantalla, new Date());
    }

    /** Igual que la anterior, pero recibe "hoy" para poder testearla. */
    public static boolean fechaNacimientoValida(String fechaPantalla, Date hoy) {
        Date fecha = parsear(fechaPantalla);
        return fecha != null && !fecha.after(hoy);
    }

    /**
     * Convierte la fecha de la pantalla (dd/MM/yyyy) al formato del backend
     * (yyyy-MM-dd). Devuelve null si la fecha no es válida.
     */
    public static String fechaAFormatoApi(String fechaPantalla) {
        Date fecha = parsear(fechaPantalla);
        if (fecha == null) return null;
        return new SimpleDateFormat(FORMATO_FECHA_API, Locale.US).format(fecha);
    }

    private static Date parsear(String fechaPantalla) {
        if (fechaPantalla == null || fechaPantalla.trim().length() != FORMATO_FECHA_PANTALLA.length()) {
            return null;
        }
        SimpleDateFormat formato = new SimpleDateFormat(FORMATO_FECHA_PANTALLA, Locale.US);
        formato.setLenient(false); // rechaza fechas inexistentes como 31/02/2000
        try {
            return formato.parse(fechaPantalla.trim());
        } catch (ParseException e) {
            return null;
        }
    }
}