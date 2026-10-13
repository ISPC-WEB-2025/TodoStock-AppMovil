package com.ispc.todostock.network;

import com.ispc.todostock.BuildConfig;

/**
 * Utilidad para verificar la disponibilidad de la API Key de Postman
 * configurada de forma segura en secrets.properties (ignorado por Git).
 */
public final class PostmanConfig {

    private PostmanConfig() {
        // Clase utilitaria
    }

    /**
     * Verifica si el entorno actual dispone de la clave de Postman configurada.
     * Si no existe secrets.properties, compila normalmente y retorna false de forma silenciosa.
     */
    public static boolean tieneApiKey() {
        return BuildConfig.POSTMAN_API_KEY != null && !BuildConfig.POSTMAN_API_KEY.trim().isEmpty();
    }

    /**
     * Devuelve la clave si existe, o cadena vacía si no fue configurada.
     */
    public static String getApiKey() {
        return BuildConfig.POSTMAN_API_KEY != null ? BuildConfig.POSTMAN_API_KEY : "";
    }
}
