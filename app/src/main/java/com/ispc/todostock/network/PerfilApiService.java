package com.ispc.todostock.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;

public interface PerfilApiService {

    @GET("api/usuarios/me/")
    Call<PerfilResponse> obtenerPerfil(@Header("Authorization") String bearer);

    @PATCH("api/usuarios/me/")
    Call<PerfilResponse> actualizarPerfil(@Header("Authorization") String bearer,
                                          @Body PerfilUpdateRequest datos);
}