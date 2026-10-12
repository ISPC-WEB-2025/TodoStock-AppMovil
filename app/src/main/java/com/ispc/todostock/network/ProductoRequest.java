package com.ispc.todostock.network;

import com.google.gson.annotations.SerializedName;

/**
 * TK23 - Datos que se envían a POST /api/inventario/productos/.
 */
public class ProductoRequest {

    private final String nombre;
    private final String descripcion;
    private final String codigo;

    @SerializedName("precio_venta")
    private final String precioVenta;

    @SerializedName("id_cat")
    private final int idCategoria;

    private ProductoRequest(String nombre, String descripcion, String codigo,
                            String precioVenta, int idCategoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.codigo = codigo;
        this.precioVenta = precioVenta;
        this.idCategoria = idCategoria;
    }

    /** Alta de un producto nuevo. */
    public static ProductoRequest nuevo(String nombre, String descripcion, String codigo,
                                        String precioVenta, int idCategoria) {
        return new ProductoRequest(nombre, descripcion, codigo, precioVenta, idCategoria);
    }
}