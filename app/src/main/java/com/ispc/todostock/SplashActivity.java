package com.ispc.todostock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Temporizador para cambiar de pantalla automáticamente
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Crear el Intent explícito hacia LoginActivity
                Intent intent = new Intent(
                        SplashActivity.this,
                        LoginActivity.class
                );
                startActivity(intent);

                // Cerrar SplashActivity para que el usuario
                // no vuelva a ella con el botón Atrás
                finish();
            }
        }, 3000); // Tiempo en milisegundos
    }
}