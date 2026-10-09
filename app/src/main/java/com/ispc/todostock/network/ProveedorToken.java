package com.ispc.todostock.network;

/**
 * TK06 - De dónde saca el interceptor el token.
 *
 * Es una interfaz para que AuthInterceptor no dependa de Android: en la app la
 * implementa SesionManager, y en los tests (TK63) se puede reemplazar por un
 * proveedor falso sin emulador.
 */
public interface ProveedorToken {

    /** Token de acceso actual, o null si no hay sesión. */
    String getAccessToken();
}