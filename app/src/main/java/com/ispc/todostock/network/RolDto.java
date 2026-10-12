package com.ispc.todostock.network;

import com.google.gson.annotations.SerializedName;

/**
 * TK109 / TK37 - DTO para roles devueltos por GET /api/usuarios/roles/.
 * Compartido para alta, asignación y gestión de usuarios.
 */
public class RolDto {

    @SerializedName("id")
    public int id;

    @SerializedName("nombre")
    public String nombre;

    @SerializedName("descripcion")
    public String descripcion;

    public RolDto() {
    }

    public RolDto(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre != null ? nombre : "";
    }
}
