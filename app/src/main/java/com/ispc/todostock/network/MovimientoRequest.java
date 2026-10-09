package com.ispc.todostock.network;

import com.google.gson.annotations.SerializedName;

/**
 * TK27 - Datos que se envían a POST /api/inventario/movimientos/.
 * Los nombres del JSON son los que espera el backend: no cambiarlos.
 */
public class MovimientoRequest {

    public static final String TIPO_TRASLADO = "Traslado";

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
}