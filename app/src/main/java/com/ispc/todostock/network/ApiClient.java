package com.ispc.todostock.network;

import com.ispc.todostock.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * TK90 - Cliente único de Retrofit para toda la app.
 *
 * Todas las pantallas piden sus servicios acá, así la URL del backend y la
 * configuración de red quedan en un solo lugar:
 *
 *     SucursalApiService api = ApiClient.create(SucursalApiService.class);
 *
 * La URL sale de BuildConfig.BASE_URL (definida en app/build.gradle).
 */
public final class ApiClient {

    private static final int TIMEOUT_SEGUNDOS = 15;

    private static Retrofit retrofit;

    // TK06: de dónde se toma el token. Lo configura TodoStockApp al iniciar la app.
    private static ProveedorToken proveedorToken;

    private ApiClient() {
        // Clase utilitaria: no se instancia.
    }

    /**
     * TK06: configura de dónde se toma el token. Se llama una sola vez, al iniciar
     * la app (TodoStockApp), antes de cualquier pedido al backend.
     */
    public static synchronized void init(ProveedorToken proveedor) {
        proveedorToken = proveedor;
        retrofit = null; // se vuelve a crear con el interceptor
    }

    /** Devuelve la implementación de una interfaz de servicio de Retrofit. */
    public static <T> T create(Class<T> servicio) {
        return getRetrofit().create(servicio);
    }

    public static synchronized Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(crearHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient crearHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS);

        // TK06: adjunta "Authorization: Bearer <token>" a cada pedido si hay sesión.
        if (proveedorToken != null) {
            builder.addInterceptor(new AuthInterceptor(proveedorToken));
        }

        if (BuildConfig.DEBUG) {
            // Solo en desarrollo: muestra en Logcat el método, la URL y el código de respuesta.
            // Se usa BASIC (y no BODY) para que no queden contraseñas ni tokens en el log.
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(logging);
        }
        return builder.build();
    }
}