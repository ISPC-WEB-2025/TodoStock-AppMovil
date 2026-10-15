package com.ispc.todostock.network;

import com.google.gson.annotations.SerializedName;

/**
 * TK27 - Un producto con su stock en una sucursal, tal como lo devuelve
 * GET /api/inventario/sucursales/{id}/inventario/.
 */
public class StockSucursalDto {

    @SerializedName("id_art")
    private int idProducto;

    @SerializedName("id_suc")
    private int idSucursal;

    @SerializedName("nombre_producto")
    private String nombreProducto;

    @SerializedName("codigo_producto")
    private String codigoProducto;

    @SerializedName("cantidad_stock")
    private int cantidad;

    @SerializedName("stock_min")
    private int stockMinimo;

    public int getIdProducto() {
        return idProducto;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }
}