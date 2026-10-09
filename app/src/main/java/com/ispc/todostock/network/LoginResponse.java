package com.ispc.todostock.network;

import com.google.gson.annotations.SerializedName;

/**
 * TK05 - Respuesta 200 de POST /api/usuarios/login/.
 *
 * El backend también manda "token" (una copia de "access" para la web)
 * e "is_superuser": la app no los usa.
 */
public class LoginResponse {

    private int id;
    private String nombre;
    private String email;
    private String access;
    private String refresh;

    @SerializedName("es_admin")
    private boolean esAdmin;

    @SerializedName("es_empleado")
    private boolean esEmpleado;

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    /** Token de acceso (JWT). Vence a los 60 minutos. */
    public String getAccess() {
        return access;
    }

    /** Token para renovar el de acceso. Vence a los 5 días. */
    public String getRefresh() {
        return refresh;
    }

    public boolean isEsAdmin() {
        return esAdmin;
    }

    public boolean isEsEmpleado() {
        return esEmpleado;
    }
}