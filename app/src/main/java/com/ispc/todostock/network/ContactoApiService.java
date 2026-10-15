package com.ispc.todostock.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface ContactoApiService {

    @POST("api/usuarios/contacto/")
    Call<ResponseBody> enviar(@Body ContactoRequest datos);
}