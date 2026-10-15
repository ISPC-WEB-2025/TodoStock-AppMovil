package com.ispc.todostock.network;

import com.google.gson.annotations.SerializedName;

/**
 * TK27 - Una sucursal, tal como la devuelve GET /api/inventario/sucursales/.
 * Solo se leen los campos que usa la app.
 */
public class SucursalDto {

    @SerializedName("id_suc")
    private int id;

    private String nombre;
    private String direccion;

    @SerializedName("es_central")
    private boolean esCentral;

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public boolean isEsCentral() {
        return esCentral;
    }
}