package com.ispc.todostock.network;

/**
 * TK05 - Datos que se envían a POST /api/usuarios/login/.
 * Los nombres de los campos son los que espera el backend: no cambiarlos.
 */
public class LoginRequest {

    private final String email;
    private final String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}