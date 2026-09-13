package com.ispc.todostock;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvBienvenida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBienvenida = findViewById(R.id.tvBienvenida);

        // Recuperar el dato enviado desde LoginActivity
        if (getIntent() != null && getIntent().hasExtra("EXTRA_USUARIO")) {
            String nombreUsuario = getIntent().getStringExtra("EXTRA_USUARIO");
            tvBienvenida.setText("¡Bienvenido/a, " + nombreUsuario + "!");
        }
    }
}