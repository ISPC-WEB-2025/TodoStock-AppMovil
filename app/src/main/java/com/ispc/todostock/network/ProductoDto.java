package com.ispc.todostock.network;

import com.google.gson.annotations.SerializedName;

/**
 * TK23 - Un producto del catálogo, tal como lo devuelve
 * /api/inventario/productos/. También lo usa el listado de productos (TK39).
 */
public class ProductoDto {

    @SerializedName("id_art")
    private int idProducto;

    private String nombre;

    private String descripcion;

    private String codigo;

    @SerializedName("precio_venta")
    private String precioVenta;

    @SerializedName("stock_min_global")
    private int stockMinimoGlobal;

    @SerializedName("id_cat")
    private int idCategoria;

    private CategoriaDto categoria;

    public int getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getPrecioVenta() {
        return precioVenta;
    }

    public int getStockMinimoGlobal() {
        return stockMinimoGlobal;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public CategoriaDto getCategoria() {
        return categoria;
    }
}