package com.ispc.todostock.network;

import com.google.gson.annotations.SerializedName;

/** Solo los campos que el backend permite editar en /me/. */
public class PerfilUpdateRequest {
    private final String nombre;
    private final String dni;
    @SerializedName("fecha_nacimiento")
    private final String fechaNacimiento; // yyyy-MM-dd

    public PerfilUpdateRequest(String nombre, String dni, String fechaNacimiento) {
        this.nombre = nombre;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
    }
}
