package com.ispc.todostock.repositorio;

import com.ispc.todostock.network.AuthApiService;
import com.ispc.todostock.network.LoginRequest;
import com.ispc.todostock.network.LoginResponse;

import java.io.IOException;

import retrofit2.Response;

/**
 * TK112 - Repositorio de autenticación.
 *
 * Hace el pedido de login al backend y traduce la respuesta a un ResultadoLogin.
 * Recibe el servicio de Retrofit por constructor, así en los tests se le puede
 * pasar uno que apunte a MockWebServer (AUT-NET-01 y 02) en lugar del backend real.
 *
 * El método login() espera la respuesta antes de devolver el resultado:
 * NO se debe llamar desde la pantalla (hilo principal). LoginViewModel lo
 * llama en segundo plano.
 */
public class AuthRepository {

    private final AuthApiService api;

    public AuthRepository(AuthApiService api) {
        this.api = api;
    }

    /**
     * Envía email y contraseña a POST /api/usuarios/login/ y devuelve qué pasó.
     * Nunca lanza excepciones: cualquier problema se devuelve como ResultadoLogin.
     */
    public ResultadoLogin login(String email, String password) {
        try {
            Response<LoginResponse> respuesta = api.login(new LoginRequest(email, password)).execute();

            if (respuesta.isSuccessful() && respuesta.body() != null) {
                return ResultadoLogin.exito(respuesta.body());
            }
            if (respuesta.code() == 401) {
                return ResultadoLogin.error(ResultadoLogin.Tipo.CREDENCIALES_INCORRECTAS);
            }
            if (respuesta.code() == 403) {
                return ResultadoLogin.error(ResultadoLogin.Tipo.CUENTA_PENDIENTE);
            }
            // 500 u otro código, o un 200 sin cuerpo.
            return ResultadoLogin.error(ResultadoLogin.Tipo.ERROR_SERVIDOR);

        } catch (IOException e) {
            // Sin internet, el servidor no responde o se agotó el tiempo de espera.
            return ResultadoLogin.error(ResultadoLogin.Tipo.SIN_CONEXION);
        } catch (RuntimeException e) {
            // El backend respondió algo que no se pudo convertir (JSON con otro formato).
            return ResultadoLogin.error(ResultadoLogin.Tipo.ERROR_SERVIDOR);
        }
    }
}