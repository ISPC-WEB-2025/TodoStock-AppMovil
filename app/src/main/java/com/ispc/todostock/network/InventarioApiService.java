package com.ispc.todostock.network;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
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

    /** TK23: categorías para el desplegable del alta de producto. */
    @GET("api/inventario/categorias/")
    Call<List<CategoriaDto>> getCategorias();

    /**
     * TK23: alta de un producto (solo Administrador).
     *   201 -> producto creado
     *   400 -> datos inválidos, por ejemplo código duplicado
     *   401 -> sesión vencida o sin token
     *   403 -> el usuario no es Administrador
     */
    @POST("api/inventario/productos/")
    Call<ProductoDto> crearProducto(@Body ProductoRequest datos);

    /** TK39: catálogo completo de productos (lista simple, sin paginación). */
    @GET("api/inventario/productos/")
    Call<List<ProductoDto>> getProductos();

    /** TK39: un producto, para precargar el formulario de edición. */
    @GET("api/inventario/productos/{id}/")
    Call<ProductoDto> getProducto(@Path("id") int idProducto);

    /**
     * TK39: edición parcial de un producto (solo Administrador). Solo cambia los campos enviados.
     *   200 -> producto actualizado
     *   400 -> datos inválidos, por ejemplo código que ya pertenece a otro producto
     *   401 -> sesión vencida o sin token
     *   403 -> el usuario no es Administrador
     *   404 -> el producto ya no existe
     */
    @PATCH("api/inventario/productos/{id}/")
    Call<ProductoDto> actualizarProducto(@Path("id") int idProducto,
                                         @Body ProductoRequest datos);
}