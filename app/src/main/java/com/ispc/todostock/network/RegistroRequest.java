package com.ispc.todostock.network;

/**
 * TK34 - Datos que se envían a POST /api/usuarios/registro/.
 * Los nombres de los campos son los que espera el backend: no cambiarlos.
 */
public class RegistroRequest {

    private final String nombre;
    private final String email;
    private final String dni;
    private final String fdn; // fecha de nacimiento, formato yyyy-MM-dd
    private final String password;

    public RegistroRequest(String nombre, String email, String dni, String fdn, String password) {
        this.nombre = nombre;
        this.email = email;
        this.dni = dni;
        this.fdn = fdn;
        this.password = password;
    }
}
