package com.ispc.todostock.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * TK06 - Agrega "Authorization: Bearer <token>" a cada pedido al backend.
 *
 * Si no hay sesión, el pedido sale sin el encabezado (por ejemplo, el login o el
 * registro). Si un pedido ya trae su propio encabezado Authorization, no se pisa.
 *
 * Los endpoints públicos del backend (login, registro, contacto) ignoran el token,
 * así que mandarlo no les afecta.
 */
public class AuthInterceptor implements Interceptor {

    private static final String ENCABEZADO = "Authorization";
    private static final String PREFIJO = "Bearer ";

    private final ProveedorToken proveedorToken;

    public AuthInterceptor(ProveedorToken proveedorToken) {
        this.proveedorToken = proveedorToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = proveedorToken.getAccessToken();

        if (token == null || token.isEmpty() || original.header(ENCABEZADO) != null) {
            return chain.proceed(original);
        }

        Request conToken = original.newBuilder()
                .header(ENCABEZADO, PREFIJO + token)
                .build();
        return chain.proceed(conToken);
    }
}