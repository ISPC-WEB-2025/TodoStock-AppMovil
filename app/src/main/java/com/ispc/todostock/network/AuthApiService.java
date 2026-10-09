package com.ispc.todostock.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * TK05 - Endpoint de inicio de sesión (público, no lleva token).
 *
 * Respuestas del backend:
 *   200 -> LoginResponse con el token y los datos del usuario
 *   401 -> {"error": "Email o contraseña incorrectos."}
 *   403 -> {"error": "Cuenta pendiente de aprobación por el administrador."}
 */
public interface AuthApiService {

    @POST("api/usuarios/login/")
    Call<LoginResponse> login(@Body LoginRequest datos);
}