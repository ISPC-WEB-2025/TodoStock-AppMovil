package com.ispc.todostock.network;

import com.google.gson.annotations.SerializedName;

/**
 * TK23 - Una categoría de producto, tal como la devuelve
 * GET /api/inventario/categorias/.
 */
public class CategoriaDto {

    @SerializedName("id_cat")
    private int idCategoria;

    private String nombre;

    public int getIdCategoria() {
        return idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    /** El desplegable de categorías muestra este texto. */
    @Override
    public String toString() {
        return nombre != null ? nombre : "";
    }
}