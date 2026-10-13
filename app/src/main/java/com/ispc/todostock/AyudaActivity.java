package com.ispc.todostock;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Pantalla Ayuda.
 *
 * Lista de opciones, parecida a la ayuda de WhatsApp:
 * - Cómo usar TodoStock: abre ComoUsarActivity (video).
 * - Términos y política de privacidad: abre TerminosActivity.
 * - Información de la app: abre InfoAppActivity.
 */
public class AyudaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);

        Toolbar toolbar = findViewById(R.id.toolbarAyuda);
        toolbar.setNavigationOnClickListener(v -> finish());

        abrirAlTocar(R.id.filaVideo, ComoUsarActivity.class);
        abrirAlTocar(R.id.filaTerminos, TerminosActivity.class);
        abrirAlTocar(R.id.filaInfo, InfoAppActivity.class);
    }

    private void abrirAlTocar(int idFila, Class<?> pantalla) {
        findViewById(idFila).setOnClickListener(v ->
                startActivity(new Intent(this, pantalla)));
    }
}
