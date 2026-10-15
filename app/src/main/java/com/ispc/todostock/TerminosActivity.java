package com.ispc.todostock;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * TK52 - Términos y condiciones y política de privacidad.
 *
 * Se abre desde "Leer términos y política de privacidad" del Registro. Muestra los
 * textos de la TK105 (Octavio), que están en res/raw. La aceptación se marca en la
 * casilla del Registro, no acá.
 */
public class TerminosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminos);

        // Flecha de la barra superior: vuelve al Registro.
        Toolbar toolbar = findViewById(R.id.toolbarTerminos);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvTerminos = findViewById(R.id.tvTextoTerminos);
        TextView tvPolitica = findViewById(R.id.tvTextoPolitica);
        tvTerminos.setText(leerTexto(R.raw.terminos_y_condiciones));
        tvPolitica.setText(leerTexto(R.raw.politica_de_privacidad));
    }

    /** Lee un archivo de texto de res/raw (UTF-8). */
    private String leerTexto(@RawRes int archivo) {
        try (InputStream entrada = getResources().openRawResource(archivo)) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int leidos;
            while ((leidos = entrada.read(buffer)) != -1) {
                bytes.write(buffer, 0, leidos);
            }
            return new String(bytes.toByteArray(), StandardCharsets.UTF_8).trim();
        } catch (IOException e) {
            return getString(R.string.terminos_error_lectura);
        }
    }
}