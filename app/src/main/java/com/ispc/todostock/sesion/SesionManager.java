package com.ispc.todostock.sesion;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.ispc.todostock.network.LoginResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * TK05 - Guarda y lee la sesión del usuario: el token y sus datos básicos.
 *
 * Usa EncryptedSharedPreferences (librería security-crypto): los datos quedan
 * cifrados en el teléfono con una clave guardada en el Android Keystore.
 *
 * Hay una sola sesión por app, así que se usa siempre con:
 *     SesionManager sesion = SesionManager.getInstance(context);
 */
public final class SesionManager {

    private static final String ARCHIVO = "sesion_todostock";

    private static final String CLAVE_ACCESS = "access_token";
    private static final String CLAVE_REFRESH = "refresh_token";
    private static final String CLAVE_ID = "usuario_id";
    private static final String CLAVE_NOMBRE = "usuario_nombre";
    private static final String CLAVE_EMAIL = "usuario_email";
    private static final String CLAVE_ES_ADMIN = "es_admin";
    private static final String CLAVE_ES_EMPLEADO = "es_empleado";

    private static SesionManager instancia;

    private final SharedPreferences preferencias;

    private SesionManager(Context context) {
        this.preferencias = crearPreferenciasCifradas(context.getApplicationContext());
    }

    public static synchronized SesionManager getInstance(Context context) {
        if (instancia == null) {
            instancia = new SesionManager(context);
        }
        return instancia;
    }

    private static SharedPreferences crearPreferenciasCifradas(Context context) {
        try {
            String claveMaestra = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            return EncryptedSharedPreferences.create(
                    ARCHIVO,
                    claveMaestra,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            // Nunca se guarda el token sin cifrar: si el cifrado no está disponible,
            // es preferible que falle a dejar el token en texto plano.
            throw new IllegalStateException("No se pudo abrir el almacenamiento cifrado de la sesión", e);
        }
    }

    /** Guarda el token y los datos del usuario después de un login exitoso. */
    public void guardarSesion(LoginResponse datos) {
        preferencias.edit()
                .putString(CLAVE_ACCESS, datos.getAccess())
                .putString(CLAVE_REFRESH, datos.getRefresh())
                .putInt(CLAVE_ID, datos.getId())
                .putString(CLAVE_NOMBRE, datos.getNombre())
                .putString(CLAVE_EMAIL, datos.getEmail())
                .putBoolean(CLAVE_ES_ADMIN, datos.isEsAdmin())
                .putBoolean(CLAVE_ES_EMPLEADO, datos.isEsEmpleado())
                .apply();
    }

    /** true si hay un token guardado (no controla si ya venció). */
    public boolean haySesion() {
        return getAccessToken() != null;
    }

    /** Token de acceso, o null si no hay sesión. Lo usa el interceptor (TK06). */
    public String getAccessToken() {
        return preferencias.getString(CLAVE_ACCESS, null);
    }

    public String getRefreshToken() {
        return preferencias.getString(CLAVE_REFRESH, null);
    }

    public int getUsuarioId() {
        return preferencias.getInt(CLAVE_ID, -1);
    }

    public String getNombre() {
        return preferencias.getString(CLAVE_NOMBRE, null);
    }

    public String getEmail() {
        return preferencias.getString(CLAVE_EMAIL, null);
    }

    public boolean esAdmin() {
        return preferencias.getBoolean(CLAVE_ES_ADMIN, false);
    }

    public boolean esEmpleado() {
        return preferencias.getBoolean(CLAVE_ES_EMPLEADO, false);
    }

    /** Borra todos los datos de la sesión. Lo usa Cerrar sesión (TK07). */
    public void cerrarSesion() {
        preferencias.edit().clear().apply();
    }
}
