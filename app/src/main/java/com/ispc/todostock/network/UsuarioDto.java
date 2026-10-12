package com.ispc.todostock.network;

import com.google.gson.annotations.SerializedName;

/**
 * TK109 / TK37 - DTO para usuarios devueltos por GET /api/usuarios/.
 * Representa tanto usuarios activos como pendientes de aprobación (is_active = false).
 */
public class UsuarioDto {

    @SerializedName("id")
    public int id;

    @SerializedName("email")
    public String email;

    @SerializedName("nombre")
    public String nombre;

    @SerializedName("dni")
    public String dni;

    @SerializedName("fecha_nacimiento")
    public String fechaNacimiento;

    @SerializedName("rol")
    public RolDto rol;

    @SerializedName("is_active")
    public boolean isActive;

    @SerializedName("is_superuser")
    public boolean isSuperuser;

    @SerializedName("es_admin")
    public boolean esAdmin;

    @SerializedName("es_empleado")
    public boolean esEmpleado;

    public UsuarioDto() {
    }

    public boolean esPendiente() {
        return !isActive || rol == null;
    }
}
