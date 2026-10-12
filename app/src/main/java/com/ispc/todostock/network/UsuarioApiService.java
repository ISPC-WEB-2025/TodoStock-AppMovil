package com.ispc.todostock.network;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * TK109 / TK37 - Servicio de API para administración de usuarios y roles.
 * Acceso exclusivo a Administradores (requiere token JWT).
 *
 * Utilizado por:
 *  - TK109: Cargar usuarios directamente y aprobar/rechazar solicitudes pendientes.
 *  - TK37: Listar usuarios activos, editar datos/rol y baja lógica.
 */
public interface UsuarioApiService {

    /** Obtiene los roles del sistema (ADMINISTRADOR, EMPLEADO). */
    @GET("api/usuarios/roles/")
    Call<List<RolDto>> obtenerRoles();

    /** Obtiene la lista completa de usuarios (activos y pendientes). */
    @GET("api/usuarios/")
    Call<List<UsuarioDto>> listarUsuarios();

    /** Crea un usuario nuevo directamente (activo con rol). */
    @POST("api/usuarios/")
    Call<ResponseBody> crearUsuario(@Body CrearUsuarioRequest datos);

    /** Aprueba o edita los datos/rol de un usuario (actualización parcial). */
    @PATCH("api/usuarios/{id}/")
    Call<ResponseBody> actualizarUsuario(@Path("id") int id, @Body AprobarUsuarioRequest datos);

    /** Desactiva o rechaza una cuenta (baja lógica: is_active = false). */
    @DELETE("api/usuarios/{id}/")
    Call<ResponseBody> desactivarUsuario(@Path("id") int id);
}
