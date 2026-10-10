package com.ispc.todostock.repositorio;

import com.ispc.todostock.network.LoginResponse;

/**
 * TK112 - Resultado de un intento de inicio de sesión.
 *
 * Lo devuelve AuthRepository. Dice qué pasó y, si salió bien, trae los datos del usuario.
 */
public final class ResultadoLogin {

    public enum Tipo {
        /** 200: sesión iniciada. Trae los datos del usuario. */
        EXITO,
        /** 401: email o contraseña incorrectos. */
        CREDENCIALES_INCORRECTAS,
        /** 403: la cuenta todavía no fue aprobada por un Administrador. */
        CUENTA_PENDIENTE,
        /** 500 u otro código inesperado, o una respuesta que no se pudo leer. */
        ERROR_SERVIDOR,
        /** No se pudo conectar con el backend (sin internet, timeout). */
        SIN_CONEXION
    }

    private final Tipo tipo;
    private final LoginResponse datos;

    private ResultadoLogin(Tipo tipo, LoginResponse datos) {
        this.tipo = tipo;
        this.datos = datos;
    }

    /** Inicio de sesión correcto, con los datos que devolvió el backend. */
    public static ResultadoLogin exito(LoginResponse datos) {
        return new ResultadoLogin(Tipo.EXITO, datos);
    }

    /** Cualquier resultado que no sea un éxito. */
    public static ResultadoLogin error(Tipo tipo) {
        if (tipo == Tipo.EXITO) {
            throw new IllegalArgumentException("Para un éxito usar ResultadoLogin.exito(datos)");
        }
        return new ResultadoLogin(tipo, null);
    }

    public Tipo getTipo() {
        return tipo;
    }

    /** Datos del usuario. Solo existen si el tipo es EXITO; en los demás casos es null. */
    public LoginResponse getDatos() {
        return datos;
    }

    public boolean esExito() {
        return tipo == Tipo.EXITO;
    }
}