package com.ispc.todostock.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;

/**
 * TK05 - Endpoint de inicio de sesión (público, no lleva token).
 * TK52 - Baja de la cuenta propia (requiere token; lo agrega AuthInterceptor).
 *
 * Respuestas del backend:
 *   200 -> LoginResponse con el token y los datos del usuario
 *   401 -> {"error": "Email o contraseña incorrectos."}
 *   403 -> {"error": "Cuenta pendiente de aprobación por el administrador."}
 */
public interface AuthApiService {

    @POST("api/usuarios/login/")
    Call<LoginResponse> login(@Body LoginRequest datos);

    /**
     * TK52 - "Darme de baja": desactiva la cuenta del usuario logueado.
     *   200 -> {"mensaje": "Cuenta desactivada. ..."}
     *   401 -> sin token o token vencido
     *   403 -> la cuenta del Super Administrador no se puede desactivar
     */
    @DELETE("api/usuarios/me/")
    Call<ResponseBody> darmeDeBaja();
}