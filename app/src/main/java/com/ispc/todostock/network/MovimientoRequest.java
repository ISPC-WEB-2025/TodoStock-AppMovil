package com.ispc.todostock.network;

import com.google.gson.annotations.SerializedName;

/**
 * TK27 - Datos que se envían a POST /api/inventario/movimientos/.
 * Los nombres del JSON son los que espera el backend: no cambiarlos.
 */
public class MovimientoRequest {

    public static final String TIPO_TRASLADO = "Traslado";
    public static final String TIPO_ENTRADA = "Entrada";
    public static final String TIPO_SALIDA = "Salida";

    private final String tipo;
    private final int cantidad;

    @SerializedName("id_art")
    private final int idProducto;

    @SerializedName("id_suc")
    private final int idSucursalOrigen;

    @SerializedName("id_suc_destino")
    private final Integer idSucursalDestino;

    private final String motivo;

    private MovimientoRequest(String tipo, int cantidad, int idProducto,
                              int idSucursalOrigen, Integer idSucursalDestino, String motivo) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.idProducto = idProducto;
        this.idSucursalOrigen = idSucursalOrigen;
        this.idSucursalDestino = idSucursalDestino;
        this.motivo = motivo;
    }

    /** Traslado entre dos sucursales. El backend lo registra de forma atómica. */
    public static MovimientoRequest traslado(int idProducto, int idOrigen, int idDestino, int cantidad) {
        return new MovimientoRequest(TIPO_TRASLADO, cantidad, idProducto, idOrigen, idDestino, null);
    }

    /** Ingreso de mercadería (Entrada de stock hacia Casa Central o sucursal). */
    public static MovimientoRequest ingreso(int idProducto, int idSucursal, int cantidad) {
        return new MovimientoRequest(TIPO_ENTRADA, cantidad, idProducto, idSucursal, null, "Ingreso de mercadería");
    }

    /** Salida de stock por venta en mostrador. */
    public static MovimientoRequest venta(int idProducto, int idSucursal, int cantidad) {
        return new MovimientoRequest(TIPO_SALIDA, cantidad, idProducto, idSucursal, null, "Venta en mostrador");
    }

    /** Salida de stock por merma o rotura. */
    public static MovimientoRequest merma(int idProducto, int idSucursal, int cantidad) {
        return new MovimientoRequest(TIPO_SALIDA, cantidad, idProducto, idSucursal, null, "Merma o rotura de stock");
    }

    public String getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public int getIdSucursalOrigen() {
        return idSucursalOrigen;
    }

    public Integer getIdSucursalDestino() {
        return idSucursalDestino;
    }

    public String getMotivo() {
        return motivo;
    }
}