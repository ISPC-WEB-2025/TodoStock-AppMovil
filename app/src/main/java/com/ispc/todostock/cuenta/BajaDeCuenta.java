package com.ispc.todostock.cuenta;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ispc.todostock.LoginActivity;
import com.ispc.todostock.R;
import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.network.AuthApiService;
import com.ispc.todostock.sesion.SesionManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * TK52 - "Darme de baja" (derecho de arrepentimiento).
 *
 * Pide confirmación, llama a DELETE /api/usuarios/me/ y, si sale bien, borra la
 * sesión guardada y vuelve al Login. La cuenta queda desactivada en el backend:
 * no se puede volver a ingresar hasta que un Administrador la reactive.
 *
 * Pendiente en el backend (no se resuelve desde la app): anonimizar los datos
 * personales y registrar la baja en la auditoría (TK51).
 *
 * Uso, desde cualquier pantalla:
 *     new BajaDeCuenta(this).iniciar();
 */
public class BajaDeCuenta {

    private final AppCompatActivity pantalla;
    private AlertDialog dialogoProcesando;

    public BajaDeCuenta(AppCompatActivity pantalla) {
        this.pantalla = pantalla;
    }

    /** Muestra el diálogo de confirmación. Si se cancela, no pasa nada. */
    public void iniciar() {
        new MaterialAlertDialogBuilder(pantalla)
                .setTitle(R.string.baja_titulo)
                .setMessage(R.string.baja_mensaje)
                .setNegativeButton(R.string.baja_cancelar, null)
                .setPositiveButton(R.string.baja_confirmar, (dialogo, boton) -> enviar())
                .show();
    }

    private void enviar() {
        // Mientras se espera la respuesta no se puede tocar nada (evita enviar dos veces).
        dialogoProcesando = new MaterialAlertDialogBuilder(pantalla)
                .setMessage(R.string.baja_procesando)
                .setCancelable(false)
                .show();

        ApiClient.create(AuthApiService.class).darmeDeBaja().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> respuesta) {
                if (pantallaCerrada()) return;
                dialogoProcesando.dismiss();

                if (respuesta.isSuccessful()) {
                    terminarSesion(R.string.baja_exitosa);
                } else if (respuesta.code() == 401) {
                    // El token venció: no se dio de baja. Se vuelve al Login para ingresar de nuevo.
                    terminarSesion(R.string.baja_sesion_vencida);
                } else if (respuesta.code() == 403) {
                    mostrarError(R.string.baja_error_superadmin);
                } else {
                    mostrarError(R.string.baja_error_generico);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                if (pantallaCerrada()) return;
                dialogoProcesando.dismiss();
                mostrarError(R.string.baja_error_conexion);
            }
        });
    }

    /** Borra la sesión guardada, muestra el mensaje y vuelve al Login sin poder volver atrás. */
    private void terminarSesion(@StringRes int mensaje) {
        SesionManager.getInstance(pantalla).cerrarSesion();

        Context app = pantalla.getApplicationContext();
        Toast.makeText(app, mensaje, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(pantalla, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pantalla.startActivity(intent);
        pantalla.finish();
    }

    private void mostrarError(@StringRes int mensaje) {
        new MaterialAlertDialogBuilder(pantalla)
                .setTitle(R.string.baja_error_titulo)
                .setMessage(mensaje)
                .setPositiveButton(R.string.baja_aceptar, null)
                .show();
    }

    /** Si la pantalla se cerró mientras se esperaba la respuesta, no se muestra nada. */
    private boolean pantallaCerrada() {
        return pantalla.isFinishing() || pantalla.isDestroyed();
    }
}