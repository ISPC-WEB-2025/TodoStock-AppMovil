package com.ispc.todostock;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * TK40 - Pantalla Ayuda.
 *
 * Lista de opciones, parecida a la ayuda de WhatsApp:
 * - Cómo usar TodoStock (video): se conecta en la parte 3.
 * - Términos y política de privacidad: abre TerminosActivity (ya existe).
 */
public class AyudaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);

        Toolbar toolbar = findViewById(R.id.toolbarAyuda);
        toolbar.setNavigationOnClickListener(v -> finish());

        findViewById(R.id.filaTerminos).setOnClickListener(v ->
                startActivity(new Intent(this, TerminosActivity.class)));

        // TODO TK40 parte 3: filaVideo abre ComoUsarActivity.
        // TODO TK40 parte 4: filaInfo abre InfoAppActivity.
    }
}
