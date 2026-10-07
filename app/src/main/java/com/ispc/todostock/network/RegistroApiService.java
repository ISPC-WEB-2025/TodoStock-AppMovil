package com.ispc.todostock.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * TK34 - Endpoint de registro (público, no lleva token).
 *
 * Respuestas del backend:
 *   201 -> usuario creado (queda sin rol: pendiente de aprobación)
 *   400 -> {"error": "..."} por datos faltantes o email repetido
 *   500 -> DNI repetido u otro error no controlado por el servidor
 */
public interface RegistroApiService {

    @POST("api/usuarios/registro/")
    Call<ResponseBody> registrar(@Body RegistroRequest datos);
}
