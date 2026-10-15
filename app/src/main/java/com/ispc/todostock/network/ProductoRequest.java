package com.ispc.todostock.network;

import com.google.gson.annotations.SerializedName;

/**
 * TK23 / TK39 - Datos que se envían a /api/inventario/productos/ (alta con POST,
 * edición parcial con PATCH).
 *
 * Los campos que valen null no se envían (Gson los omite), así que en un PATCH
 * el backend solo cambia lo que se manda. Los nombres del JSON son los que
 * espera el backend: no cambiarlos.
 *
 * El precio va como texto para no perder precisión decimal.
 */
public class ProductoRequest {

    private final String nombre;
    private final String descripcion;
    private final String codigo;

    @SerializedName("precio_venta")
    private final String precioVenta;

    @SerializedName("id_cat")
    private final Integer idCategoria;

    private ProductoRequest(String nombre, String descripcion, String codigo,
                            String precioVenta, Integer idCategoria) {
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

    /** Edición de todos los campos del formulario (PATCH). */
    public static ProductoRequest edicion(String nombre, String descripcion, String codigo,
                                          String precioVenta, int idCategoria) {
        return new ProductoRequest(nombre, descripcion, codigo, precioVenta, idCategoria);
    }

    /** Cambia solo el nombre. Se usa para dar de baja o reactivar (prefijo [INACTIVO]).ya que no podemos
     * desde el backend bug registrado */
    public static ProductoRequest soloNombre(String nombre) {
        return new ProductoRequest(nombre, null, null, null, null);
    }
}