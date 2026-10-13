package com.ispc.todostock.network;

import com.google.gson.annotations.SerializedName;

/**
 * TK37 - Datos para editar un usuario desde Gestión de usuarios:
 * PATCH /api/usuarios/<id>/
 *
 * El rol es opcional: si rolId es null, Gson no lo envía y el backend deja el rol
 * como estaba. Solo el Super Administrador cambia roles (ADR-0008).
 */
public class EditarUsuarioRequest {

    @SerializedName("nombre")
    private final String nombre;

    @SerializedName("email")
    private final String email;

    @SerializedName("dni")
    private final String dni;

    @SerializedName("rol_id")
    private final Integer rolId;

    public EditarUsuarioRequest(String nombre, String email, String dni, Integer rolId) {
        this.nombre = nombre;
        this.email = email;
        this.dni = dni;
        this.rolId = rolId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getDni() {
        return dni;
    }

    public Integer getRolId() {
        return rolId;
    }
}
