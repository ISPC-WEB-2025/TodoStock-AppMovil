package com.ispc.todostock.network;

import com.google.gson.annotations.SerializedName;

/** TK94/TK95 - Respuesta de GET/PATCH /api/usuarios/me/ */
public class PerfilResponse {
    public int id;
    public String email;
    public String nombre;
    public String dni;
    @SerializedName("fecha_nacimiento")
    public String fechaNacimiento; // yyyy-MM-dd
    public Rol rol;                // puede venir null (cuenta sin rol)
    @SerializedName("es_admin")
    public boolean esAdmin;
    @SerializedName("es_empleado")
    public boolean esEmpleado;

    public static class Rol {
        public int id;
        public String nombre;
    }
}