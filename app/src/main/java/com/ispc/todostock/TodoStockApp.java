package com.ispc.todostock;

import android.app.Application;

import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.sesion.SesionManager;

/**
 * TK06 - Clase que Android crea al iniciar la app, antes que cualquier pantalla.
 *
 * Conecta ApiClient con la sesión guardada, para que todos los pedidos al backend
 * lleven el token aunque la app se haya cerrado y vuelto a abrir.
 * Está declarada en AndroidManifest.xml con android:name=".TodoStockApp".
 */
public class TodoStockApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApiClient.init(SesionManager.getInstance(this));
    }
}