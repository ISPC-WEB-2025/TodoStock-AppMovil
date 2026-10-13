package com.ispc.todostock;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Cómo usar TodoStock.
 *
 * Reproduce el video de res/raw/como_usar_todostock.mp4 con los controles estándar
 * (reproducir, pausa y barra de avance). Al rotar el celular la pantalla se recrea
 * y el video sigue desde el mismo segundo, y reproduciéndose si lo estaba.
 *
 * En vertical se usa layout/activity_como_usar.xml y en horizontal layout-land/.
 */
public class ComoUsarActivity extends AppCompatActivity {

    private static final String ESTADO_POSICION = "posicion_video";
    private static final String ESTADO_REPRODUCIENDO = "reproduciendo_video";

    private VideoView videoView;
    private MediaController controles;

    // Estado del video para recuperarlo al rotar.
    private int posicion = 0;
    private boolean reproduciendo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_como_usar);

        // Flecha de la barra superior: vuelve a Ayuda.
        Toolbar toolbar = findViewById(R.id.toolbarComoUsar);
        toolbar.setNavigationOnClickListener(v -> finish());

        if (savedInstanceState != null) {
            posicion = savedInstanceState.getInt(ESTADO_POSICION, 0);
            reproduciendo = savedInstanceState.getBoolean(ESTADO_REPRODUCIENDO, false);
        }

        videoView = findViewById(R.id.videoComoUsar);
        controles = new MediaController(this);
        videoView.setMediaController(controles);

        videoView.setVideoURI(Uri.parse(
                "android.resource://" + getPackageName() + "/" + R.raw.como_usar_todostock));

        videoView.setOnPreparedListener(mp -> {
            // VideoView ancla los controles a su contenedor (más ancho que el video) cada vez
            // que abre el archivo. Acá ya está abierto: se anclan al video para que midan igual.
            controles.setAnchorView(videoView);
            // seekTo(1) como mínimo para que se vea el primer cuadro antes de reproducir.
            videoView.seekTo(Math.max(posicion, 1));
            if (reproduciendo) {
                videoView.start();
            }
        });

        videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(this, R.string.como_usar_video_error, Toast.LENGTH_LONG).show();
            return true;
        });
    }

    @Override
    protected void onPause() {
        // Se guarda el estado acá: al llegar a onSaveInstanceState el video ya puede estar en pausa.
        if (videoView != null) {
            posicion = videoView.getCurrentPosition();
            reproduciendo = videoView.isPlaying();
            videoView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle salida) {
        super.onSaveInstanceState(salida);
        salida.putInt(ESTADO_POSICION, posicion);
        salida.putBoolean(ESTADO_REPRODUCIENDO, reproduciendo);
    }

    @Override
    protected void onDestroy() {
        // Evita que los controles queden colgados de una ventana que ya no existe.
        if (controles != null) {
            controles.hide();
        }
        super.onDestroy();
    }
}