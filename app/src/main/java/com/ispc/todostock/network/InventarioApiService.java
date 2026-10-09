package com.ispc.todostock.network;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * TK27 - Endpoints de inventario. Todos requieren sesión: el token lo agrega
 * AuthInterceptor (TK06).
 *
 * Lo pueden reutilizar Sucursales (TK12) y Stock (TK16).
 */
public interface InventarioApiService {

    /** Todas las sucursales. */
    @GET("api/inventario/sucursales/")
    Call<List<SucursalDto>> getSucursales();

    /** Productos con su stock en una sucursal. Con soloConStock = true, solo los que tienen existencias. */
    @GET("api/inventario/sucursales/{id}/inventario/")
    Call<List<StockSucursalDto>> getStockDeSucursal(@Path("id") int idSucursal,
                                                    @Query("solo_con_stock") boolean soloConStock);

    /**
     * Registra un movimiento.
     *   201 -> movimiento registrado
     *   400 -> {"error": "..."}; si falta stock, incluye "stock_disponible"
     *   401 -> sesión vencida o sin token
     */
    @POST("api/inventario/movimientos/")
    Call<ResponseBody> registrarMovimiento(@Body MovimientoRequest datos);
}