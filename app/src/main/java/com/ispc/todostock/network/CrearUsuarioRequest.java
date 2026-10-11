package com.ispc.todostock.network;

import com.google.gson.annotations.SerializedName;

/**
 * TK109 - Datos para crear un usuario directamente desde el Administrador:
 * POST /api/usuarios/
 */
public class CrearUsuarioRequest {

    @SerializedName("nombre")
    private final String nombre;

    @SerializedName("email")
    private final String email;

    @SerializedName("dni")
    private final String dni;

    @SerializedName("fecha_nacimiento")
    private final String fechaNacimiento;

    @SerializedName("fdn")
    private final String fdn;

    @SerializedName("password")
    private final String password;

    @SerializedName("rol_id")
    private final Integer rolId;

    @SerializedName("is_active")
    private final boolean isActive;

    public CrearUsuarioRequest(String nombre, String email, String dni, String fechaNacimiento, String password, Integer rolId) {
        this.nombre = nombre;
        this.email = email;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.fdn = fechaNacimiento; // Envía ambos para máxima compatibilidad con el backend
        this.password = password;
        this.rolId = rolId;
        this.isActive = true;
    }
}
