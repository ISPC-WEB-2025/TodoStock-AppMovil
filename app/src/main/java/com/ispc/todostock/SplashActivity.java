package com.ispc.todostock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.ispc.todostock.sesion.SesionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Temporizador para cambiar de pantalla automáticamente
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing() || isDestroyed()) return;

                SesionManager sesion = SesionManager.getInstance(SplashActivity.this);
                Intent intent;
                if (sesion.haySesion()) {
                    intent = new Intent(SplashActivity.this, MenuPrincipalActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(intent);

                // Cerrar SplashActivity para que el usuario
                // no vuelva a ella con el botón Atrás
                finish();
            }
        }, 3000); // Tiempo en milisegundos
    }
}