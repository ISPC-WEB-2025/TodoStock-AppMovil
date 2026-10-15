package com.ispc.todostock;

import android.content.Context;

import com.ispc.todostock.network.MovimientoRequest;

/**
 * Define las operaciones transaccionales de inventario y sus propiedades
 * operativas y visuales en la aplicación.
 */
public enum TipoMovimiento {
    TRASLADO(
            R.string.mov_titulo,
            R.string.mov_btn_confirmar,
            R.string.mov_etiqueta_origen,
            true,   // muestraDestino
            false,  // soloCasaCentral
            true,   // validaStock
            false,  // usaCatalogo
            R.string.mov_exito_titulo
    ),
    VENTA(
            R.string.mov_titulo_venta,
            R.string.mov_btn_confirmar_venta,
            R.string.mov_etiqueta_sucursal,
            false,  // muestraDestino
            false,  // soloCasaCentral
            true,   // validaStock
            false,  // usaCatalogo
            R.string.mov_exito_venta_titulo
    ),
    MERMA(
            R.string.mov_titulo_merma,
            R.string.mov_btn_confirmar_merma,
            R.string.mov_etiqueta_sucursal,
            false,  // muestraDestino
            false,  // soloCasaCentral
            true,   // validaStock
            false,  // usaCatalogo
            R.string.mov_exito_merma_titulo
    ),
    INGRESO(
            R.string.mov_titulo_ingreso,
            R.string.mov_btn_confirmar_ingreso,
            R.string.mov_etiqueta_sucursal_receptora,
            false,  // muestraDestino
            true,   // soloCasaCentral
            false,  // validaStock
            true,   // usaCatalogo
            R.string.mov_exito_ingreso_titulo
    );

    public final int tituloRes;
    public final int textoBotonRes;
    public final int etiquetaOrigenRes;
    public final boolean muestraDestino;
    public final boolean soloCasaCentral;
    public final boolean validaStock;
    public final boolean usaCatalogo;
    public final int tituloExitoRes;

    TipoMovimiento(int tituloRes, int textoBotonRes, int etiquetaOrigenRes,
                   boolean muestraDestino, boolean soloCasaCentral, boolean validaStock,
                   boolean usaCatalogo, int tituloExitoRes) {
        this.tituloRes = tituloRes;
        this.textoBotonRes = textoBotonRes;
        this.etiquetaOrigenRes = etiquetaOrigenRes;
        this.muestraDestino = muestraDestino;
        this.soloCasaCentral = soloCasaCentral;
        this.validaStock = validaStock;
        this.usaCatalogo = usaCatalogo;
        this.tituloExitoRes = tituloExitoRes;
    }

    public static TipoMovimiento desdeString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de operacion no puede ser nulo ni vacio");
        }
        for (TipoMovimiento tipo : values()) {
            if (tipo.name().equalsIgnoreCase(valor.trim())) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de operacion desconocido o no soportado: " + valor);
    }

    public MovimientoRequest crearRequest(int productoId, int origenId, Integer destinoId, int cantidad) {
        switch (this) {
            case INGRESO:
                return MovimientoRequest.ingreso(productoId, origenId, cantidad);
            case VENTA:
                return MovimientoRequest.venta(productoId, origenId, cantidad);
            case MERMA:
                return MovimientoRequest.merma(productoId, origenId, cantidad);
            case TRASLADO:
            default:
                return MovimientoRequest.traslado(productoId, origenId, destinoId != null ? destinoId : 0, cantidad);
        }
    }

    public String construirMensajeExito(Context context, int cantidad, String nombreProducto,
                                        String nombreOrigen, String nombreDestino) {
        switch (this) {
            case INGRESO:
                return context.getString(R.string.mov_exito_ingreso_mensaje, cantidad, nombreProducto, nombreOrigen);
            case VENTA:
                return context.getString(R.string.mov_exito_venta_mensaje, cantidad, nombreProducto, nombreOrigen);
            case MERMA:
                return context.getString(R.string.mov_exito_merma_mensaje, cantidad, nombreProducto, nombreOrigen);
            case TRASLADO:
            default:
                return context.getString(R.string.mov_exito_mensaje, cantidad, nombreProducto, nombreOrigen, nombreDestino);
        }
    }
}
